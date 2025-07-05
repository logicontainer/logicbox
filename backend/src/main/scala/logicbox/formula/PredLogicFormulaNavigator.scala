package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location
import PredLogicFormula._
import PredLogicTerm._

class PredLogicFormulaNavigator extends Navigator[PredLogicFormula, PredLogicFormula | PredLogicTerm] {
  override def get(subject: PredLogicFormula, loc: Location): Option[PredLogicFormula | PredLogicTerm] = 
    loc.steps.foldLeft(Some(subject): Option[PredLogicFormula | PredLogicTerm]) {
      case (Some(f), step) => (f, step) match {
        case (Not(phi), 0) => Some(phi)

        case (And(phi, _), 0) => Some(phi)
        case (Or(phi, _), 0) => Some(phi)
        case (Implies(phi, _), 0) => Some(phi)

        case (And(_, psi), 1) => Some(psi)
        case (Or(_, psi), 1) => Some(psi)
        case (Implies(_, psi), 1) => Some(psi)

        case (ForAll(_, phi), 0) => Some(phi)
        case (Exists(_, phi), 0) => Some(phi)
        
        case (Predicate(_, ts), idx) => ts.lift(idx)
        case (FunAppl(_, ts), idx) => ts.lift(idx)

        case (Equals(t1, _), 0) => Some(t1)
        case (Equals(_, t2), 1) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
