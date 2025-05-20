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

  private type Repl = Option[PredLogicTerm | Unit]

  private def unifyReplacements(repls: Set[Repl]): Repl = {
    if repls.contains(None) then None else {
      val ts = repls.collect { 
        case Some(t: PredLogicTerm) => t
      }

      if ts.isEmpty then 
        Some(())
      else if ts.size > 1 then 
        None 
      else 
        Some(ts.head)
    }
  }

  private def findReplacements(ts1: List[PredLogicTerm], ts2: List[PredLogicTerm], x: Var): Set[Repl] = {
    ts1.zip(ts2).map((t1, t2) => findReplacement(t1, t2, x)).toSet
  }

  private def findReplacement(src: PredLogicTerm, dst: PredLogicTerm, x: Var): Repl = {
    (src, dst) match {
      case _ if src == x => 
        Some(dst)

      case (FunAppl(f, ys), FunAppl(g, zs)) if f == g =>
        unifyReplacements(findReplacements(ys, zs, x))

      case (y: Var, z: Var) if y == z => 
        Some(())

      case _ => None
    }
  }

  // two quantifiers Q y phi1 and Q y phi2 (with repl. var x)
  private def findReplacementInsideQuantifiers(
    y: Var, phi1: PredLogicFormula, phi2: PredLogicFormula,
    x: Var
  ): Repl = {
    if x != y then
      findReplacement(phi1, phi2, x)
    else if phi1 == phi2 then
      Some(())
    else None
  }

  override def findReplacement(src: PredLogicFormula, dst: PredLogicFormula, x: Var): Repl = {
    (src, dst) match {
      case (Equals(l1, r1), Equals(l2, r2)) => 
        unifyReplacements(Set(
          findReplacement(l1, l2, x),
          findReplacement(r1, r2, x)
        ))

      case (Predicate(p, ps), Predicate(q, qs)) if p == q => 
        unifyReplacements(findReplacements(ps, qs, x))

      case (And(phi1, psi1), And(phi2, psi2)) => 
        unifyReplacements(Set(
          findReplacement(phi1, phi2, x),
          findReplacement(psi1, psi2, x),
        ))

      case (Or(phi1, psi1), Or(phi2, psi2)) => 
        unifyReplacements(Set(
          findReplacement(phi1, phi2, x),
          findReplacement(psi1, psi2, x),
        ))

      case (Implies(phi1, psi1), Implies(phi2, psi2)) => 
        unifyReplacements(Set(
          findReplacement(phi1, phi2, x),
          findReplacement(psi1, psi2, x),
        ))

      case (Not(phi1), Not(phi2)) => findReplacement(phi1, phi2, x)

      case (ForAll(y, phi1), ForAll(z, phi2)) if y == z =>
        findReplacementInsideQuantifiers(y, phi1, phi2, x)

      case (Exists(y, phi1), Exists(z, phi2)) if y == z => 
        findReplacementInsideQuantifiers(y, phi1, phi2, x)

      case (Contradiction(), Contradiction()) => Some(())
      case (Tautology(), Tautology()) => Some(())

      case _ => None
    }
  }
}
