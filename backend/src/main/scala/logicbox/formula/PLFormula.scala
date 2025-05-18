package logicbox.formula

sealed abstract class PLFormula extends FormulaBase[PLFormula]

object PLFormula {
  case class Tautology() extends PLFormula
  case class Atom(c: Char) extends PLFormula

  case class And(phi: PLFormula, psi: PLFormula) extends PLFormula with FormulaBase.And[PLFormula]
  case class Or(phi: PLFormula, psi: PLFormula) extends PLFormula with FormulaBase.Or[PLFormula]
  case class Implies(phi: PLFormula, psi: PLFormula) extends PLFormula with FormulaBase.Implies[PLFormula]
  case class Not(phi: PLFormula) extends PLFormula with FormulaBase.Not[PLFormula]
  case class Contradiction() extends PLFormula with FormulaBase.Contradiction[PLFormula]
}
