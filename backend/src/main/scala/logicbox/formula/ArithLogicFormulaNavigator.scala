package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location
import ArithLogicFormula._
import ArithLogicTerm._

class ArithLogicFormulaNavigator extends Navigator[ArithLogicFormula, ArithLogicFormula | ArithLogicTerm] {
  override def get(subject: ArithLogicFormula, loc: Location): Option[ArithLogicFormula | ArithLogicTerm] = 
    import Location.Step._
    loc.steps.foldLeft(Some(subject): Option[ArithLogicFormula | ArithLogicTerm]) {
      case (Some(f), step) => (f, step) match {
        case (Not(phi), Negated) => Some(phi)

        case (ForAll(_, phi), InsideQuantifier) => Some(phi)
        case (Exists(_, phi), InsideQuantifier) => Some(phi)

        case (Equals(t1, _), Lhs) => Some(t1)
        case (Equals(_, t2), Rhs) => Some(t2)

        case (And(phi, _), Lhs) => Some(phi)
        case (Or(phi, _), Lhs) => Some(phi)
        case (Implies(phi, _), Lhs) => Some(phi)

        case (And(_, psi), Rhs) => Some(psi)
        case (Or(_, psi), Rhs) => Some(psi)
        case (Implies(_, psi), Rhs) => Some(psi)

        case (Plus(t1, _), Lhs) => Some(t1)
        case (Plus(_, t2), Rhs) => Some(t2)

        case (Mult(t1, _), Lhs) => Some(t1)
        case (Mult(_, t2), Rhs) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
