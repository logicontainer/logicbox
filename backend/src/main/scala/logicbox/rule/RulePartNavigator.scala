package logicbox.rule

import logicbox.framework.{Navigator, Location, RulePart}
import logicbox.framework.RulePart._

class RulePartNavigator extends Navigator[RulePart, RulePart] {
  override def get(subject: RulePart, loc: Location): Option[RulePart] =
    import Location.Step._
    loc.steps.foldLeft(Some(subject): Option[RulePart]) {
      case (Some(p), step) => (p, step) match {

        case (Not(phi), Negated) => Some(phi)

        case (And(phi, _), Lhs) => Some(phi)
        case (Or(phi, _), Lhs) => Some(phi)
        case (Implies(phi, _), Lhs) => Some(phi)

        case (And(_, psi), Rhs) => Some(psi)
        case (Or(_, psi), Rhs) => Some(psi)
        case (Implies(_, psi), Rhs) => Some(psi)

        case (ForAll(_, phi), InsideQuantifier) => Some(phi)
        case (Exists(_, phi), InsideQuantifier) => Some(phi)

        case (Equals(t1, _), Lhs) => Some(t1)
        case (Equals(_, t2), Rhs) => Some(t2)

        case (TemplateBox(ass, _, _), FirstLine) => ass
        case (TemplateBox(_, concl, _), LastLine) => concl
        case (TemplateBox(_, _, fresh), FreshVar) => fresh

        case (Plus(t1, _), Lhs) => Some(t1)
        case (Plus(_, t2), Rhs) => Some(t2)

        case (Mult(t1, _), Lhs) => Some(t1)
        case (Mult(_, t2), Rhs) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
