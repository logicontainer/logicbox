package logicbox.proof

import logicbox.framework.Navigator
import logicbox.framework.Proof
import logicbox.framework.Location
import logicbox.framework.Reference
import logicbox.framework.Reference.Box
import logicbox.framework.Reference.Line

class ReferenceNavigator[F, B, O](
  formulaNavigator: Navigator[F, ? <: O],
  boxInfoNavigator: Navigator[B, ? <: O]
) extends Navigator[Reference[F, B], O] {
  private enum Result {
    case Nothing
    case Formula(o: F, loc: Location)
    case BoxInfo(b: B, loc: Location)
  }

  private def getFormula(ref: Reference[F, B], loc: Location): Result = (ref, loc.steps) match {
    case (Line(formula), steps) => Result.Formula(formula, Location(steps))
    case (Box(_, Some(first), _), 0 :: rest) => getFormula(first, Location(rest))
    case (Box(_, _, Some(last)), 1 :: rest)  => getFormula(last, Location(rest))
    case (Box(info, _, _), 2 :: rest) => Result.BoxInfo(info, Location(rest))
    case _ => ???
  }

  override def get(subject: Reference[F, B], loc: Location): Option[O] = {
    getFormula(subject, loc) match {
      case Result.Nothing => None
      case Result.Formula(f, loc) => formulaNavigator.get(f, loc)
      case Result.BoxInfo(info, loc) => boxInfoNavigator.get(info, loc)
    }
  }
}
