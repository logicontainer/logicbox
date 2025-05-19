package logicbox.formula

sealed abstract class PropLogicFormula extends ConnectiveFormula[PropLogicFormula]

object PropLogicFormula {
  case class Tautology() extends PropLogicFormula
  case class Atom(c: Char) extends PropLogicFormula

  case class And(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula with ConnectiveFormula.And[PropLogicFormula]
  case class Or(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula with ConnectiveFormula.Or[PropLogicFormula]
  case class Implies(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula with ConnectiveFormula.Implies[PropLogicFormula]
  case class Not(phi: PropLogicFormula) extends PropLogicFormula with ConnectiveFormula.Not[PropLogicFormula]
  case class Contradiction() extends PropLogicFormula with ConnectiveFormula.Contradiction[PropLogicFormula]
}
