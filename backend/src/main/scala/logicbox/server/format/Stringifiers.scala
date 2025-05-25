package logicbox.server.format

import logicbox.rule.PredLogicRule
import logicbox.rule.PredLogicRule._
import logicbox.rule.ArithLogicRule
import logicbox.rule.ArithLogicRule._
import logicbox.rule.PropLogicRule
import logicbox.rule.PropLogicRule._

import logicbox.formula._

object Stringifiers {
  private def propLogicFormulaWithBracks(formula: PropLogicFormula, inner: PropLogicFormula => String, l: String, r: String): String = {
    import PropLogicFormula._
    formula match {
      // don't add brackets to atoms, simple formulas
      case Contradiction() | Tautology() | Atom(_) | Not(_) => inner(formula)
      case And(_, _) | Or(_, _) | Implies(_, _) => s"$l${inner(formula)}$r"
    }
  }

  def propLogicFormulaAsLaTeX(formula: PropLogicFormula): String = {
    import PropLogicFormula._
    def b(f: PropLogicFormula) = propLogicFormulaWithBracks(f, propLogicFormulaAsLaTeX, "(", ")")
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
    import PropLogicFormula._
    def b(f: PropLogicFormula) = propLogicFormulaWithBracks(f, propLogicFormulaAsASCII, "(", ")")
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

  private def predLogicFormulaWithBracks(formula: PredLogicFormula, inner: PredLogicFormula => String, l: String, r: String): String = {
    import logicbox.formula.PredLogicFormula._
    formula match {
      case Contradiction() | Tautology() | Not(_) | Predicate(_, _) | ForAll(_, _) | Exists(_, _) => inner(formula)
      case And(_, _) | Or(_, _) | Implies(_, _) | Equals(_, _) => s"$l${inner(formula)}$r"
    }
  }

  def predLogicTermAsString(term: PredLogicTerm): String = {
    import logicbox.formula.PredLogicTerm._
    term match {
      case FunAppl(f, ps) => s"$f(${ps.map(predLogicTermAsString).mkString(", ")})"
      case Var(x) => x.toString
    }
  }

  def predLogicFormulaAsLaTeX(formula: PredLogicFormula): String = {
    import PredLogicFormula._
    def b(f: PredLogicFormula) = predLogicFormulaWithBracks(f, predLogicFormulaAsLaTeX, "(", ")")
    formula match {
      case Contradiction() => "\\bot"
      case Tautology() => "\\top"
      case Predicate(p, ps) => s"$p(${ps.map(predLogicTermAsString).mkString(", ")})"
      case Equals(t1, t2) => s"${predLogicTermAsString(t1)} = ${predLogicTermAsString(t2)}"
      case And(phi, psi) => s"${b(phi)} \\land ${b(psi)}"
      case Or(phi, psi) => s"${b(phi)} \\lor ${b(psi)}"
      case Implies(phi, psi) => s"${b(phi)} \\rightarrow ${b(psi)}"
      case Not(phi) => s"\\lnot ${b(phi)}"
      case ForAll(x, phi) => s"\\forall ${predLogicTermAsString(x)} ${b(phi)}"
      case Exists(x, phi) => s"\\exists ${predLogicTermAsString(x)} ${b(phi)}"
    }
  }

  def predLogicFormulaAsASCII(formula: PredLogicFormula): String = {
    import PredLogicFormula._
    def b(f: PredLogicFormula) = predLogicFormulaWithBracks(f, predLogicFormulaAsASCII, "(", ")")
    formula match {
      case Contradiction() => "false"
      case Tautology() => "true"
      case Predicate(p, ps) => s"$p(${ps.map(predLogicTermAsString).mkString(", ")})"
      case Equals(t1, t2) => s"${predLogicTermAsString(t1)} = ${predLogicTermAsString(t2)}"
      case And(phi, psi) => s"${b(phi)} and ${b(psi)}"
      case Or(phi, psi) => s"${b(phi)} or ${b(psi)}"
      case Implies(phi, psi) => s"${b(phi)} -> ${b(psi)}"
      case Not(phi) => s"not ${b(phi)}"
      case ForAll(x, phi) => s"forall ${predLogicTermAsString(x)} ${b(phi)}"
      case Exists(x, phi) => s"exists ${predLogicTermAsString(x)} ${b(phi)}"
    }
  }

  def predLogicRuleAsString(rule: PredLogicRule): String = rule match {
    case ForAllElim() => "forall_elim"
    case ForAllIntro() => "forall_intro"
    case ExistsElim() => "exists_elim"
    case ExistsIntro() => "exists_intro"
    case EqualityIntro() => "equality_intro"
    case EqualityElim() => "equality_elim"
  }
  
  private def arithLogicFormulaWithBracks(formula: ArithLogicFormula, inner: ArithLogicFormula => String, l: String, r: String): String = {
    import logicbox.formula.ArithLogicFormula._
    formula match {
      case Contradiction() | Tautology() | Not(_) | ForAll(_, _) | Exists(_, _) => inner(formula)
      case And(_, _) | Or(_, _) | Implies(_, _) | Equals(_, _) => s"$l${inner(formula)}$r"
    }
  }

  private def arithLogicTermWithBracks(term: ArithLogicTerm, inner: ArithLogicTerm => String, l: String, r: String): String = {
    import logicbox.formula.ArithLogicTerm._
    term match {
      case Zero() | One() | Var(_) => inner(term)
      case Plus(_, _) | Mult(_, _) => s"$l${inner(term)}$r"
    }
  }

  def arithLogicTermAsString(term: ArithLogicTerm): String = {
    import logicbox.formula.ArithLogicTerm._
    def b(f: ArithLogicTerm) = arithLogicTermWithBracks(f, arithLogicTermAsString, "(", ")")
    term match {
      case Var(x) => x.toString
      case Zero() => "0"
      case One() => "1"
      case Plus(t1, t2) => s"${b(t1)} + ${b(t2)}"
      case Mult(t1, t2) => s"${b(t1)} * ${b(t2)}"
    }
  }


  def arithLogicFormulaAsLaTeX(formula: ArithLogicFormula): String = {
    import ArithLogicFormula._
    def b(f: ArithLogicFormula) = arithLogicFormulaWithBracks(f, arithLogicFormulaAsLaTeX, "(", ")")
    formula match {
      case Contradiction() => "\\bot"
      case Tautology() => "\\top"
      case Equals(t1, t2) => s"${arithLogicTermAsString(t1)} = ${arithLogicTermAsString(t2)}"
      case And(phi, psi) => s"${b(phi)} \\land ${b(psi)}"
      case Or(phi, psi) => s"${b(phi)} \\lor ${b(psi)}"
      case Implies(phi, psi) => s"${b(phi)} \\rightarrow ${b(psi)}"
      case Not(phi) => s"\\lnot ${b(phi)}"
      case ForAll(x, phi) => s"\\forall ${arithLogicTermAsString(x)} ${b(phi)}"
      case Exists(x, phi) => s"\\exists ${arithLogicTermAsString(x)} ${b(phi)}"
    }
  }

  def arithLogicFormulaAsASCII(formula: ArithLogicFormula): String = {
    import ArithLogicFormula._
    def b(f: ArithLogicFormula) = arithLogicFormulaWithBracks(f, arithLogicFormulaAsASCII, "(", ")")
    formula match {
      case Contradiction() => "false"
      case Tautology() => "true"
      case Equals(t1, t2) => s"${arithLogicTermAsString(t1)} = ${arithLogicTermAsString(t2)}"
      case And(phi, psi) => s"${b(phi)} and ${b(psi)}"
      case Or(phi, psi) => s"${b(phi)} or ${b(psi)}"
      case Implies(phi, psi) => s"${b(phi)} -> ${b(psi)}"
      case Not(phi) => s"not ${b(phi)}"
      case ForAll(x, phi) => s"forall ${arithLogicTermAsString(x)} ${b(phi)}"
      case Exists(x, phi) => s"exists ${arithLogicTermAsString(x)} ${b(phi)}"
    }
  }

  def arithLogicRuleAsString(rule: ArithLogicRule): String = rule match {
    case Peano1() => "peano_1"
    case Peano2() => "peano_2"
    case Peano3() => "peano_3"
    case Peano4() => "peano_4"
    case Peano5() => "peano_5"
    case Peano6() => "peano_6"
    case Induction() => "induction"
  }
}
