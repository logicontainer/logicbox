package logicbox.rule

import logicbox.formula.PredLogicTerm
import logicbox.formula.PredLogicFormula
import logicbox.formula.PredLogicTerm._
import logicbox.formula.PredLogicFormula._

class PredLogicFormulaSubstitutor extends Substitutor[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var] {
  private def substitute(src: PredLogicTerm, x: Var, t: PredLogicTerm): PredLogicTerm = src match {
    case y: Var if y == x => t
    case y: Var => y

    case FunAppl(f, ps) => 
      FunAppl(f, ps.map(substitute(_, x, t)))
  }

  override def substitute(f: PredLogicFormula, x: Var, t: PredLogicTerm): PredLogicFormula = f match {
    case Predicate(p, ps) => Predicate(p, ps.map(substitute(_, x, t)))
    case And(phi, psi) => And(substitute(phi, x, t), substitute(psi, x, t))
    case Or(phi, psi) => Or(substitute(phi, x, t), substitute(psi, x, t))
    case Implies(phi, psi) => Implies(substitute(phi, x, t), substitute(psi, x, t))
    case Not(phi) => Not(substitute(phi, x, t))
    case f @ (Contradiction() | Tautology()) => f
    case Equals(t1, t2) => Equals(substitute(t1, x, t), substitute(t2, x, t))

    case ForAll(y, phi) if y == x => ForAll(x, phi)
    case ForAll(y, phi) => ForAll(y, substitute(phi, x, t))

    case Exists(y, phi) if y == x => Exists(y, phi)
    case Exists(y, phi) => Exists(y, substitute(phi, x, t))
  }
}
