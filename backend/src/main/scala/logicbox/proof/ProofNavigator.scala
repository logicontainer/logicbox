package logicbox.proof

import logicbox.framework.Navigator
import logicbox.framework.Proof
import logicbox.framework.Location

class ProofNavigator[F, B, Id, O](
  formulaNavigator: Navigator[F, O],
  boxInfoNavigator: Navigator[B, O],
) extends Navigator[(Proof[F, Any, B, Id], Id), O] {
  private enum Result {
    case Nothing
    case FoundFormula(formula: F, rest: Location)
    case FoundBoxInfo(info: B, rest: Location)
  }

  private def getFormula(proof: Proof[F, ?, B, Id], stepId: Id, loc: Location): Result =
    proof.getStep(stepId) match {
      case Right(Proof.Line(formula, _, _)) => 
        Result.FoundFormula(formula, loc)

      case Right(Proof.Box(info, ids)) => loc.steps match {
        case 0 :: rest => ids.headOption
          .map(getFormula(proof, _, Location(rest)))
          .getOrElse(Result.Nothing)

        case 1 :: rest => ids.lastOption
          .map(getFormula(proof, _, Location(rest)))
          .getOrElse(Result.Nothing)

        case 2 :: rest => Result.FoundBoxInfo(info, Location(rest))

        case _ => Result.Nothing
      }

      case _ => Result.Nothing
    }

  override def get(proofAndId: (Proof[F, ?, B, Id], Id), loc: Location): Option[O] = {
    getFormula(proofAndId._1, proofAndId._2, loc) match {
      case Result.FoundFormula(formula, remaining) => formulaNavigator.get(formula, remaining)
      case Result.FoundBoxInfo(info, remaining) => boxInfoNavigator.get(info, remaining)
      case Result.Nothing => None
    }
  }
}
