package logicbox.marshal

import logicbox.formula.PropLogicFormula
import logicbox.formula.PropLogicFormula.Contradiction
import logicbox.formula.PropLogicFormula.Tautology
import logicbox.formula.PropLogicFormula.Atom
import logicbox.formula.PropLogicFormula.And
import logicbox.formula.PropLogicFormula.Or
import logicbox.formula.PropLogicFormula.Implies
import logicbox.formula.PropLogicFormula.Not

object PrettyPLFormula {
  private def withBracks(formula: PropLogicFormula, inner: PropLogicFormula => String, l: String, r: String): String = {
    formula match {
      // don't add brackets to atoms, simple formulas
      case Contradiction() | Tautology() | Atom(_) | Not(_) => inner(formula)
      case And(_, _) | Or(_, _) | Implies(_, _) => s"$l${inner(formula)}$r"
    }
  }

  def asLaTeX(formula: PropLogicFormula): String = {
    def b(f: PropLogicFormula) = withBracks(f, asLaTeX, "(", ")")
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

  def asASCII(formula: PropLogicFormula): String = {
    def b(f: PropLogicFormula) = withBracks(f, asASCII, "(", ")")
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
