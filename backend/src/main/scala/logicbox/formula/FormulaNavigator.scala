package logicbox.formula

import logicbox.framework.Navigator
import logicbox.framework.Location

import Formula._, Term._
class FormulaNavigator[K <: FormulaKind] extends Navigator[Formula[K], Formula[K] | Term[K]] {
  override def get(subject: Formula[K], loc: Location): Option[Formula[K] | Term[K]] = 
    import Location.Step._
    loc.steps.foldLeft(Some(subject): Option[Formula[K] | Term[K]]) {
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

        case (Plus(t1, _), Lhs) => Some(t1)
        case (Plus(_, t2), Rhs) => Some(t2)

        case (Mult(t1, _), Lhs) => Some(t1)
        case (Mult(_, t2), Rhs) => Some(t2)

        case _ => None
      }
      case _ => None
    }
}
