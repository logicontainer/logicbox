package logicbox.server.format

import logicbox.formula.PropLogicFormula
import logicbox.formula.PropLogicFormula._
import logicbox.rule.PropLogicRule
import logicbox.rule.PropLogicRule._

object Stringifiers {
  private def withBracks(formula: PropLogicFormula, inner: PropLogicFormula => String, l: String, r: String): String = {
    formula match {
      // don't add brackets to atoms, simple formulas
      case Contradiction() | Tautology() | Atom(_) | Not(_) => inner(formula)
      case And(_, _) | Or(_, _) | Implies(_, _) => s"$l${inner(formula)}$r"
    }
  }

  def propLogicFormulaAsLaTeX(formula: PropLogicFormula): String = {
    def b(f: PropLogicFormula) = withBracks(f, propLogicFormulaAsLaTeX, "(", ")")
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

  def propLogicFormulaAsASCII(formula: PropLogicFormula): String = {
    def b(f: PropLogicFormula) = withBracks(f, propLogicFormulaAsASCII, "(", ")")
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

  def propLogicRuleAsString(rule: PropLogicRule): String = rule match {
    case Assumption() => "assumption"
    case Premise() => "premise"
    case AndElim(side) => s"and_elim_${if (side == PropLogicRule.Side.Left) then 1 else 2}"
    case AndIntro() => "and_intro"
    case OrIntro(side) => s"or_intro_${if (side == PropLogicRule.Side.Left) then 1 else 2}"
    case OrElim() => "or_elim"
    case ImplicationIntro() => "implies_intro"
    case ImplicationElim() => "implies_elim"
    case NotIntro() => "not_intro"
    case NotElim() => "not_elim"
    case ContradictionElim() => "bot_elim"
    case NotNotElim() => "not_not_elim"
    case ModusTollens() => "modus_tollens"
    case NotNotIntro() => "not_not_intro"
    case ProofByContradiction() => "proof_by_contradiction"
    case LawOfExcludedMiddle() => "law_of_excluded_middle"
    case Copy() => "copy"
  }
}
