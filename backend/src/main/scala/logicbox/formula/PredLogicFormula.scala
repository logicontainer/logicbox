package logicbox.formula

import logicbox.framework.RuleChecker
import logicbox.framework.Reference

sealed trait PredLogicTerm

object PredLogicTerm {
  case class Var(x: Char) extends PredLogicTerm
  case class FunAppl(f: Char, ps: List[PredLogicTerm]) extends PredLogicTerm
}

sealed trait PredLogicFormula extends ConnectiveFormula[PredLogicFormula] with QuantifierFormula[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var]

object PredLogicFormula {
  private type Term = PredLogicTerm
  private type Form = PredLogicFormula

  case class Tautology() extends Form

  case class Predicate(p: Char, ps: List[Term]) extends Form
  case class Equals(t1: Term, t2: Term) extends Form with QuantifierFormula.Equals[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var]

  case class Contradiction() extends Form with ConnectiveFormula.Contradiction[Form]
  case class Not(phi: Form) extends Form with ConnectiveFormula.Not[Form]
  case class And(phi: Form, psi: Form) extends Form with ConnectiveFormula.And[Form]
  case class Or(phi: Form, psi: Form) extends Form with ConnectiveFormula.Or[Form]
  case class Implies(phi: Form, psi: Form) extends Form with ConnectiveFormula.Implies[Form]

  case class Exists(x: PredLogicTerm.Var, phi: Form) extends Form with QuantifierFormula.Exists[Form, PredLogicTerm, PredLogicTerm.Var]
  case class ForAll(x: PredLogicTerm.Var, phi: Form) extends Form with QuantifierFormula.ForAll[Form, PredLogicTerm, PredLogicTerm.Var]
}
