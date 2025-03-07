package logicbox.proof

import logicbox.framework.{RuleChecker, Proof, ProofChecker, Reference}

object RuledBasedProofChecker {
  sealed trait Diagnostic[+Id, +V] {
    def stepId: Id
  }

  case class RuleViolation[Id, V](stepId: Id, violation: V) extends Diagnostic[Id, V]
  case class StepNotFound[Id](stepId: Id, expl: String) extends Diagnostic[Id, Nothing]
  case class ReferenceIdNotFound[Id](stepId: Id, whichRef: Int, refId: Id, expl: String) extends Diagnostic[Id, Nothing]
  case class MalformedReference[Id](stepId: Id, whichRef: Int, refId: Id, expl: String) extends Diagnostic[Id, Nothing]
}

class RuledBasedProofChecker[F, R, B, V, Id](
  val ruleChecker: RuleChecker[F, R, B, V]
) extends ProofChecker[F, R, B, Id, RuledBasedProofChecker.Diagnostic[Id, V]] {

  import RuledBasedProofChecker._
  
  type Diagnostic = RuledBasedProofChecker.Diagnostic[Id, V]

  type Pf = Proof[F, R, B, Id]
  private def resolveBoxReference(proof: Pf, stepId: Id, refIdx: Int, boxId: Id, box: Proof.Box[B, Id]): Either[List[Diagnostic], Reference.Box[F, B]] =
    for {
      ids <- box.steps match {
        case Seq() => Left(List(MalformedReference(stepId, refIdx, boxId, "box is empty")))
        case steps => Right(List(steps.head, steps.last))
      }

      names = List("assumption", "conclusion")
      (ass, concl) <- (names, ids, ids.map(proof.getStep)).zipped.toList.collect {
        case (which, refId, Left(Proof.StepNotFound(_, expl))) => 
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

  private def resolveReferences(proof: Pf, stepId: Id, refIds: Seq[Id]): Either[List[Diagnostic], List[Reference[F, B]]] = {
    for {
      refIds <- Right(refIds.toList)
      refSteps = refIds.map(proof.getStep)
      refIdxs = (0 until refIds.length)

      mixed = (refIds, refIdxs, refSteps).zipped.toList.collect {
        case (refId, refIdx, Left(Proof.StepNotFound(_, expl))) => 
          Left(List(ReferenceIdNotFound(stepId, refIdx, refId, expl)))

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

  private def checkStep(proof: Proof[F, R, B, Id], id: Id, step: Proof.Step[F, R, B, Id]): List[Diagnostic] = 
    (step: @unchecked) match {
      case Proof.Line(formula: F @unchecked, rule: R @unchecked, ids: Seq[Id] @unchecked) =>
        resolveReferences(proof, id, ids) match {
          case Right(refs) => 
            ruleChecker.check(rule, formula, refs).map { v => RuleViolation(id, v) }

          case Left(diagnostics) => diagnostics
        }
      case Proof.Box(_, ids: Seq[Id] @unchecked) => checkSteps(proof, ids)
    }

  private def checkSteps(proof: Proof[F, R, B, Id], stepIds: Seq[Id]): List[Diagnostic] =
    val ids = stepIds.toList
    for {
      (either, id) <- ids.map(proof.getStep).zip(ids)
      res <- either match {
        case Right(step) => checkStep(proof, id, step)
        case Left(Proof.StepNotFound(id, expl)) => List(StepNotFound(id, expl))
      }
    } yield res

  
  override def check(proof: Proof[F, R, B, Id]): List[Diagnostic] = 
    checkSteps(proof, proof.rootSteps)
}
