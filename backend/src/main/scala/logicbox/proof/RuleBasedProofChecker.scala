package logicbox.proof

import logicbox.framework.{RuleChecker, Proof, ProofChecker, Reference, Error}
import logicbox.rule.{ReferenceBoxImpl, ReferenceLineImpl}
import logicbox.framework.Error
import logicbox.framework.Error._

class RuleBasedProofChecker[F, R, B, Id](
  val ruleChecker: RuleChecker[F, R, B]
) extends ProofChecker[F, R, B, Id] {

  private type D = (Id, Error)
  private type Pf = Proof[F, R, B, Id]

  private def resolveBoxReference(proof: Pf, stepId: Id, refIdx: Int, boxId: Id, box: Proof.Box[B, Id]): Reference.Box[F, B] = {
    val ids = List(box.steps.headOption, box.steps.lastOption)
    val List(assRef, conclRef) = ids.map(_.flatMap { 
      ref => resolveReference(proof, boxId, ref, 0).toOption
    })
    ReferenceBoxImpl(box.info, assRef, conclRef)
  }

  private def resolveReference(proof: Pf, stepId: Id, refId: Id, refIdx: Int): Either[List[D], Reference[F, B]] = {
    (proof.getStep(refId): @unchecked) match {
      case Left(Proof.StepNotFound(_)) => 
        Left(List((stepId, MissingRef(refIdx))))

      case Right(Proof.Line(formula, _, _)) => 
        Right(ReferenceLineImpl(formula))

      case Right(b: Proof.Box[B, Id]) => 
        Right(resolveBoxReference(proof, stepId, refIdx, refId, b))
    }
  }

  private def resolveReferences(proof: Pf, stepId: Id, refIds: Seq[Id]): Either[List[D], List[Reference[F, B]]] = {
    val refIdxs = (0 until refIds.length)
    val mixed = (refIds.toList, refIdxs).zipped.map { 
      case (id, idx) => resolveReference(proof, stepId, id, idx)
    }

    for {
      _ <- mixed.collect { case Left(dgn) => dgn }.flatten match {
        case Nil => Right(())
        case dgns => Left(dgns)
      }

      refs = mixed.collect { case Right(r) => r }
    } yield refs
  }

  private def checkStep(proof: Proof[F, R, B, Id], id: Id, step: Proof.Step[F, R, B, Id]): List[D] = 
    (step: @unchecked) match {
      case Proof.Line(formula, rule, ids) =>
        resolveReferences(proof, id, ids) match {
          case Right(refs) => 
            ruleChecker.check(rule, formula, refs).map { v => (id, v) }

          case Left(diagnostics) => diagnostics
        }
      case Proof.Box(_, ids) => checkSteps(proof, ids)
    }

  private def checkSteps(proof: Proof[F, R, B, Id], stepIds: Seq[Id]): List[D] =
    val ids = stepIds.toList
    for {
      (either, id) <- ids.map(proof.getStep).zip(ids)
      res <- either match {
        case Right(step) => checkStep(proof, id, step)
        case Left(Proof.StepNotFound(id)) => ??? // List(StepNotFound(id))
      }
    } yield res

  
  override def check(proof: Proof[F, R, B, Id]): List[D] = 
    checkSteps(proof, proof.rootSteps)
}
