package logicbox.proof

import logicbox.framework.{RuleChecker, Proof, ProofChecker, Reference, RuleViolation}
import logicbox.framework.StepDiagnostic
import logicbox.rule.{ReferenceBoxImpl, ReferenceLineImpl}
import logicbox.framework.Diagnostic
import logicbox.framework.Diagnostic._

class RuleBasedProofChecker[F, R, B, Id](
  val ruleChecker: RuleChecker[F, R, B]
) extends ProofChecker[F, R, B, Id] {

  private type D = Diagnostic[Id]
  private type Pf = Proof[F, R, B, Id]

  private def resolveBoxReference(proof: Pf, stepId: Id, refIdx: Int, boxId: Id, box: Proof.Box[B, Id]): Either[List[D], Reference.Box[F, B]] =
    for {
      ids <- box.steps match {
        case Seq() => Left(List(MalformedReference(stepId, refIdx, boxId, "box is empty")))
        case steps => Right(List(steps.head, steps.last))
      }

      names = List("assumption", "conclusion")
      (ass, concl) <- (names, ids, ids.map(proof.getStep)).zipped.toList.collect {
        case (which, refId, Left(Proof.StepNotFound(_))) => 
          Left(MalformedReference(stepId, refIdx, boxId, s"$which in box has invalid id (id: $refId)"))
        case (which, refId, Right(Proof.Box(_, _))) => 
          Left(MalformedReference(stepId, refIdx, boxId, s"$which in box is itself a box"))
        case (_, _, Right(Proof.Line(formula: F @unchecked, _, _))) => Right(formula)
      } match {
        case List(Right(ass), Right(concl)) => Right(ass, concl)
        case ls => 
          // if box only contains one line, don't have duplicate diagnostics
          val dgns = ls.collect { case Left(d) => d }
          val assumptionAndConclusionAreTheSame = ids(0) == ids(1)
          if (assumptionAndConclusionAreTheSame) Left(dgns.take(1))
          else Left(dgns)
      }
    } yield ReferenceBoxImpl(box.info, ass, concl)

  private def resolveReferences(proof: Pf, stepId: Id, refIds: Seq[Id]): Either[List[D], List[Reference[F, B]]] = {
    for {
      refIds <- Right(refIds.toList)
      refSteps = refIds.map(proof.getStep)
      refIdxs = (0 until refIds.length)

      mixed = (refIds, refIdxs, refSteps).zipped.toList.collect {
        case (refId, refIdx, Left(Proof.StepNotFound(_))) => 
          Left(List(ReferenceIdNotFound(stepId, refIdx, refId)))

        case (refId, refIdx, Right(b: Proof.Box[B, Id])) => 
          resolveBoxReference(proof, stepId, refIdx, refId, b)

        case (_, _, Right(Proof.Line(formula: F @unchecked, _, _))) => 
          Right(ReferenceLineImpl(formula))
      }

      _ <- mixed.collect { case Left(dgn) => dgn }.flatten match {
        case Nil => Right(())
        case dgns => Left(dgns)
      }

      refs = mixed.collect { case Right(r) => r }
    } yield refs
  }

  private def checkStep(proof: Proof[F, R, B, Id], id: Id, step: Proof.Step[F, R, B, Id]): List[D] = 
    (step: @unchecked) match {
      case Proof.Line(formula: F @unchecked, rule: R @unchecked, ids: Seq[Id] @unchecked) =>
        resolveReferences(proof, id, ids) match {
          case Right(refs) => 
            ruleChecker.check(rule, formula, refs).map { v => RuleViolationAtStep(id, v) }

          case Left(diagnostics) => diagnostics
        }
      case Proof.Box(_, ids: Seq[Id] @unchecked) => checkSteps(proof, ids)
    }

  private def checkSteps(proof: Proof[F, R, B, Id], stepIds: Seq[Id]): List[D] =
    val ids = stepIds.toList
    for {
      (either, id) <- ids.map(proof.getStep).zip(ids)
      res <- either match {
        case Right(step) => checkStep(proof, id, step)
        case Left(Proof.StepNotFound(id)) => List(StepNotFound(id))
      }
    } yield res

  
  override def check(proof: Proof[F, R, B, Id]): List[D] = 
    checkSteps(proof, proof.rootSteps)
}
