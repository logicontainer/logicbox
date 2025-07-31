package logicbox.formula

sealed trait ArithLogicTerm extends ArithmeticTerm[ArithLogicTerm]
object ArithLogicTerm {
  private type T = ArithLogicTerm

  case class Var(x: String) extends ArithLogicTerm

  case class Zero() extends ArithLogicTerm, ArithmeticTerm.Zero[T]
  case class One() extends ArithLogicTerm, ArithmeticTerm.One[T]
  case class Plus(t1: T, t2: T) extends ArithLogicTerm, ArithmeticTerm.Plus[T]
  case class Mult(t1: T, t2: T) extends ArithLogicTerm, ArithmeticTerm.Mult[T]
}

sealed trait ArithLogicFormula extends 
  ConnectiveFormula[ArithLogicFormula],
  QuantifierFormula[ArithLogicFormula, ArithLogicTerm, ArithLogicTerm.Var]

object ArithLogicFormula {
  private type Form = ArithLogicFormula
  private type Term = ArithLogicTerm
  private type Var = ArithLogicTerm.Var

  case class Tautology() extends Form

  case class Equals(t1: Term, t2: Term) extends Form with QuantifierFormula.Equals[Form, Term, ArithLogicTerm.Var]

  case class Contradiction() extends Form with ConnectiveFormula.Contradiction[Form]
  case class Not(phi: Form) extends Form with ConnectiveFormula.Not[Form]
  case class And(phi: Form, psi: Form) extends Form with ConnectiveFormula.And[Form]
  case class Or(phi: Form, psi: Form) extends Form with ConnectiveFormula.Or[Form]
  case class Implies(phi: Form, psi: Form) extends Form with ConnectiveFormula.Implies[Form]

  case class Exists(x: Var, phi: Form) extends Form with QuantifierFormula.Exists[Form, Term, Var]
  case class ForAll(x: Var, phi: Form) extends Form with QuantifierFormula.ForAll[Form, Term, Var]
}
