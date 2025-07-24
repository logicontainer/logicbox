package logicbox.proof

import logicbox.framework.Navigator
import logicbox.framework.Proof
import logicbox.framework.Location
import logicbox.framework.Proof.Line
import logicbox.framework.Proof.Box
import logicbox.framework.Location.Step

class ProofNavigator[F, B, Id, O](
  formulaNavigator: Navigator[F, O],
  boxInfoNavigator: Navigator[B, O],
) extends Navigator[(Proof[F, Any, B, Id], Id), O] {
  private enum Result {
    case Nothing
    case FoundFormula(formula: F, rest: Location)
    case FoundBoxInfo(info: B, rest: Location)
  }

  private def derefFirstStep(proof: Proof[F, ?, B, Id], stepId: Id, firstStep: Location.Step): Option[Proof.Step[F, ?, B, Id]] = {
    firstStep match {
      case Step.Premise(idx) => 
        proof.getStep(stepId).collect {
          case Proof.Line(_, _, refs) => refs
        }.flatMap(_.lift(idx))
         .flatMap(proof.getStep(_))

      case Step.Conclusion => proof.getStep(stepId)
      case _ => None
    }
  }
    
  import Location.Step
  private def getFormula(proof: Proof[F, ?, B, Id], stepId: Id, loc: Location): Result = {
    for {
      (fst, rest) <- loc.steps match {
        case x :: xs => Some(x, xs)
        case _ => None
      }
      (step, rest) <- (rest, derefFirstStep(proof, stepId, fst)) match {
        case (Step.FirstLine :: rest, Some(Proof.Box(_, first :: _))) => 
          proof.getStep(first).map((_, rest))
        case (Step.LastLine :: rest, Some(Proof.Box(_, _ :+ last))) =>
          proof.getStep(last).map((_, rest))
        case (rest, Some(step)) => Some(step, rest)
        case _ => None
      }
      res <- step match {
        case Proof.Line(f, _, _) => Some(Result.FoundFormula(f, Location(rest)))
        case Proof.Box(info, _) if rest.headOption == Some(Step.FreshVar) => 
          Some(Result.FoundBoxInfo(info, Location(rest.tail)))
        case _ => None
      }
    } yield res
  }.getOrElse(Result.Nothing)

  override def get(proofAndId: (Proof[F, ?, B, Id], Id), loc: Location): Option[O] = {
    getFormula(proofAndId._1, proofAndId._2, loc) match {
      case Result.FoundFormula(formula, remaining) => formulaNavigator.get(formula, remaining)
      case Result.FoundBoxInfo(info, remaining) => boxInfoNavigator.get(info, remaining)
      case Result.Nothing => None
    }
  }
}
