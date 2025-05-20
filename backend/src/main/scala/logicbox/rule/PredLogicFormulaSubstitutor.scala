package logicbox.rule

import logicbox.formula.PredLogicTerm
import logicbox.formula.PredLogicFormula
import logicbox.formula.PredLogicTerm._
import logicbox.formula.PredLogicFormula._

class PredLogicFormulaSubstitutor extends Substitutor[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var] {
  private def substitute(src: PredLogicTerm, t: PredLogicTerm, x: Var): PredLogicTerm = src match {
    case y: Var if y == x => t
    case y: Var => y

    case FunAppl(f, ps) => 
      FunAppl(f, ps.map(substitute(_, t, x)))
  }

  override def substitute(f: PredLogicFormula, t: PredLogicTerm, x: Var): PredLogicFormula = f match {
    case Predicate(p, ps) => Predicate(p, ps.map(substitute(_, t, x)))

    case And(phi, psi) =>     And(substitute(phi, t, x), substitute(psi, t, x))
    case Or(phi, psi) =>      Or(substitute(phi, t, x), substitute(psi, t, x))
    case Implies(phi, psi) => Implies(substitute(phi, t, x), substitute(psi, t, x))
    case Not(phi) =>          Not(substitute(phi, t, x))

    case f @ (Contradiction() | Tautology()) => f

    case Equals(t1, t2) => Equals(substitute(t1, t, x), substitute(t2, t, x))

    case ForAll(y, phi) if y == x => ForAll(x, phi)
    case ForAll(y, phi) => ForAll(y, substitute(phi, t, x))

    case Exists(y, phi) if y == x => Exists(y, phi)
    case Exists(y, phi) => Exists(y, substitute(phi, t, x))
  }

  // true iff v contains occurance of t
  private def hasFreeOccurance(v: PredLogicTerm, t: PredLogicTerm): Boolean = 
    v == t || (v match {
      case FunAppl(_, ps) => ps.exists(hasFreeOccurance(_, t))
      case _: Var => false
    })

  override def hasFreeOccurance(f: PredLogicFormula, t: PredLogicTerm): Boolean = f match {
    case Predicate(p, ps) => 
      ps.exists(hasFreeOccurance(_, t))

    case f: (And | Or | Implies) => 
      hasFreeOccurance(f.phi, t) || hasFreeOccurance(f.psi, t)

    case Not(phi) => 
      hasFreeOccurance(phi, t)

    case f @ (Contradiction() | Tautology()) => 
      false

    case Equals(t1, t2) => hasFreeOccurance(t1, t) || hasFreeOccurance(t2, t)

    case ForAll(x, phi) if x == t => false
    case ForAll(_, phi) => hasFreeOccurance(phi, t)

    case Exists(x, phi) if x == t => false
    case Exists(_, phi) => hasFreeOccurance(phi, t)
  }
}
