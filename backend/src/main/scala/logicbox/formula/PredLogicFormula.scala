package logicbox.formula

sealed trait PredLogicTerm

object PredLogicTerm {
  case class Var(x: Char) extends PredLogicTerm
  case class FunAppl(f: Char, ps: List[PredLogicTerm]) extends PredLogicTerm
}

sealed trait PredLogicFormula

object PredLogicFormula {
  private type Term = PredLogicTerm
  private type Form = PredLogicFormula

  case class Contradiction() extends Form
  case class Tautology() extends Form

  case class Predicate(p: Char, ps: List[Term]) extends Form
  case class Equals(t1: Term, t2: Term)

  case class Not(phi: Form) extends Form

  case class And(phi: Form, psi: Form) extends Form
  case class Or(phi: Form, psi: Form) extends Form
  case class Implies(phi: Form, psi: Form) extends Form

  case class Exists(x: PredLogicTerm.Var, phi: Form) extends Form
  case class ForAll(x: PredLogicTerm.Var, phi: Form) extends Form
}
