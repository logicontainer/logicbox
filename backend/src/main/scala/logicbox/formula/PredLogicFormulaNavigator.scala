package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location
import PredLogicFormula._
import PredLogicTerm._

class PredLogicFormulaNavigator extends Navigator[PredLogicFormula, PredLogicFormula | PredLogicTerm] {
  override def get(subject: PredLogicFormula, loc: Location): Option[PredLogicFormula | PredLogicTerm] = 
    import Location.Step._
    loc.steps.foldLeft(Some(subject): Option[PredLogicFormula | PredLogicTerm]) {
      case (Some(f), step) => (f, step) match {
        case (Not(phi), Negated) => Some(phi)

        case (And(phi, _), Lhs) => Some(phi)
        case (Or(phi, _), Lhs) => Some(phi)
        case (Implies(phi, _), Lhs) => Some(phi)

        case (And(_, psi), Rhs) => Some(psi)
        case (Or(_, psi), Rhs) => Some(psi)
        case (Implies(_, psi), Rhs) => Some(psi)

        case (ForAll(_, phi), InsideQuantifier) => Some(phi)
        case (Exists(_, phi), InsideQuantifier) => Some(phi)
        
        case (Predicate(_, ts), Operand(idx)) => ts.lift(idx)
        case (FunAppl(_, ts), Operand(idx)) => ts.lift(idx)

        case (Equals(t1, _), Lhs) => Some(t1)
        case (Equals(_, t2), Rhs) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
