package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location
import PropLogicFormula._

class PropLogicFormulaNavigator extends Navigator[PropLogicFormula, PropLogicFormula] {
  override def get(subject: PropLogicFormula, loc: Location): Option[PropLogicFormula] = 
    import Location.Step._
    loc.steps.foldLeft(Some(subject): Option[PropLogicFormula]) {
      case (Some(f), step) => (f, step) match {
        case (Not(phi), Negated) => Some(phi)

        case (And(phi, _), Lhs) => Some(phi)
        case (Or(phi, _), Lhs) => Some(phi)
        case (Implies(phi, _), Lhs) => Some(phi)

        case (And(_, psi), Rhs) => Some(psi)
        case (Or(_, psi), Rhs) => Some(psi)
        case (Implies(_, psi), Rhs) => Some(psi)

        case _ => None
      }
      case _ => None
    }
}
