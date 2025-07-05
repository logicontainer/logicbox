package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location
import PropLogicFormula._

class PropLogicFormulaNavigator extends Navigator[PropLogicFormula, PropLogicFormula] {
  override def get(subject: PropLogicFormula, loc: Location): Option[PropLogicFormula] = 
    loc.steps.foldLeft(Some(subject): Option[PropLogicFormula]) {
      case (Some(f), step) => (f, step) match {
        case (Not(phi), 0) => Some(phi)

        case (And(phi, _), 0) => Some(phi)
        case (Or(phi, _), 0) => Some(phi)
        case (Implies(phi, _), 0) => Some(phi)

        case (And(_, psi), 1) => Some(psi)
        case (Or(_, psi), 1) => Some(psi)
        case (Implies(_, psi), 1) => Some(psi)

        case _ => None
      }
      case _ => None
    }
}
