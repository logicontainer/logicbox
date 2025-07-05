package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location
import ArithLogicFormula._
import ArithLogicTerm._

class ArithLogicFormulaNavigator extends Navigator[ArithLogicFormula, ArithLogicFormula | ArithLogicTerm] {
  override def get(subject: ArithLogicFormula, loc: Location): Option[ArithLogicFormula | ArithLogicTerm] = 
    loc.steps.foldLeft(Some(subject): Option[ArithLogicFormula | ArithLogicTerm]) {
      case (Some(f), step) => (f, step) match {
        case (Not(phi), 0) => Some(phi)

        case (ForAll(_, phi), 0) => Some(phi)
        case (Exists(_, phi), 0) => Some(phi)

        case (Equals(t1, _), 0) => Some(t1)
        case (Equals(_, t2), 1) => Some(t2)

        case (And(phi, _), 0) => Some(phi)
        case (Or(phi, _), 0) => Some(phi)
        case (Implies(phi, _), 0) => Some(phi)

        case (And(_, psi), 1) => Some(psi)
        case (Or(_, psi), 1) => Some(psi)
        case (Implies(_, psi), 1) => Some(psi)

        case (Plus(t1, _), 0) => Some(t1)
        case (Plus(_, t2), 1) => Some(t2)

        case (Mult(t1, _), 0) => Some(t1)
        case (Mult(_, t2), 1) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
