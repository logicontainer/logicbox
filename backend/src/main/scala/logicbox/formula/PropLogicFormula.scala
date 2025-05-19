package logicbox.formula

sealed abstract class PropLogicFormula extends FormulaBase[PropLogicFormula]

object PropLogicFormula {
  case class Tautology() extends PropLogicFormula
  case class Atom(c: Char) extends PropLogicFormula

  case class And(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula with FormulaBase.And[PropLogicFormula]
  case class Or(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula with FormulaBase.Or[PropLogicFormula]
  case class Implies(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula with FormulaBase.Implies[PropLogicFormula]
  case class Not(phi: PropLogicFormula) extends PropLogicFormula with FormulaBase.Not[PropLogicFormula]
  case class Contradiction() extends PropLogicFormula with FormulaBase.Contradiction[PropLogicFormula]
}
