package logicbox.rule

import logicbox.framework.Navigator
import logicbox.framework.Location
import RulePart._

class RulePartNavigator extends Navigator[RulePart, RulePart] {
  override def get(subject: RulePart, loc: Location): Option[RulePart] =
    loc.steps.foldLeft(Some(subject): Option[RulePart]) {
      case (Some(p), step) => (p, step) match {

        case (Not(phi), 0) => Some(phi)

        case (And(phi, _), 0) => Some(phi)
        case (Or(phi, _), 0) => Some(phi)
        case (Implies(phi, _), 0) => Some(phi)

        case (And(_, psi), 1) => Some(psi)
        case (Or(_, psi), 1) => Some(psi)
        case (Implies(_, psi), 1) => Some(psi)

        case (ForAll(_, phi), 0) => Some(phi)
        case (Exists(_, phi), 0) => Some(phi)

        case (Equals(t1, _), 0) => Some(t1)
        case (Equals(_, t2), 1) => Some(t2)

        case (TemplateBox(ass, _, _), 0) => ass
        case (TemplateBox(_, concl, _), 1) => concl
        case (TemplateBox(_, _, fresh), 2) => fresh

        case (Plus(t1, _), 0) => Some(t1)
        case (Plus(_, t2), 1) => Some(t2)

        case (Mult(t1, _), 0) => Some(t1)
        case (Mult(_, t2), 1) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
