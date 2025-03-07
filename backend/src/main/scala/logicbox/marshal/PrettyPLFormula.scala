package logicbox.marshal

import logicbox.formula.PLFormula
import logicbox.formula.PLFormula.Contradiction
import logicbox.formula.PLFormula.Tautology
import logicbox.formula.PLFormula.Atom
import logicbox.formula.PLFormula.And
import logicbox.formula.PLFormula.Or
import logicbox.formula.PLFormula.Implies
import logicbox.formula.PLFormula.Not

object PrettyPLFormula {
  private def withBracks(formula: PLFormula, inner: PLFormula => String, l: String, r: String): String = {
    formula match {
      // don't add brackets to atoms, simple formulas
      case Contradiction() | Tautology() | Atom(_) | Not(_) => inner(formula)
      case And(_, _) | Or(_, _) | Implies(_, _) => s"$l${inner(formula)}$r"
    }
  }

  def asLaTeX(formula: PLFormula): String = {
    def b(f: PLFormula) = withBracks(f, asLaTeX, "(", ")")
    formula match {
      case Contradiction() => "\\bot"
      case Tautology() => "\\top"
      case Atom(c) => c.toString
      case And(phi, psi) => s"${b(phi)} \\land ${b(psi)}"
      case Or(phi, psi) => s"${b(phi)} \\lor ${b(psi)}"
      case Implies(phi, psi) => s"${b(phi)} \\rightarrow ${b(psi)}"
      case Not(phi) => s"\\lnot ${b(phi)}"
    }
  }

  def asASCII(formula: PLFormula): String = {
    def b(f: PLFormula) = withBracks(f, asASCII, "(", ")")
    formula match {
      case Contradiction() => "false"
      case Tautology() => "true"
      case Atom(c) => c.toString
      case And(phi, psi) => s"${b(phi)} and ${b(psi)}"
      case Or(phi, psi) => s"${b(phi)} or ${b(psi)}"
      case Implies(phi, psi) => s"${b(phi)} -> ${b(psi)}"
      case Not(phi) => s"not ${b(phi)}"
    }
  }
}
