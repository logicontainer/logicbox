package logicbox.formula

sealed abstract class PLFormula

object PLFormula {
  case class Contradiction() extends PLFormula
  case class Tautology() extends PLFormula
  case class Atom(c: Char) extends PLFormula
  case class And(phi: PLFormula, psi: PLFormula) extends PLFormula
  case class Or(phi: PLFormula, psi: PLFormula) extends PLFormula
  case class Implies(phi: PLFormula, psi: PLFormula) extends PLFormula
  case class Not(phi: PLFormula) extends PLFormula
}
