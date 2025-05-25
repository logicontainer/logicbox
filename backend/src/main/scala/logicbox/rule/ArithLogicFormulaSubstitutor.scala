package logicbox.rule

import logicbox.formula.ArithLogicTerm
import logicbox.formula.ArithLogicFormula
import logicbox.formula.ArithLogicTerm._
import logicbox.formula.ArithLogicFormula._
import scala.compiletime.ops.boolean

class ArithLogicFormulaSubstitutor extends Substitutor[ArithLogicFormula, ArithLogicTerm, ArithLogicTerm.Var] {

  private def substitute(src: ArithLogicTerm, t: ArithLogicTerm, x: Var): ArithLogicTerm = src match {
    case y: Var if y == x => t
    case y: Var => y
    case Zero() | One() => src
    case Plus(s1, s2) => Plus(substitute(s1, t, x), substitute(s2, t, x))
    case Mult(s1, s2) => Mult(substitute(s1, t, x), substitute(s2, t, x))
  }

  override def substitute(f: ArithLogicFormula, t: ArithLogicTerm, x: Var): ArithLogicFormula = f match {
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
  private def hasFreeOccurance(v: ArithLogicTerm, t: ArithLogicTerm): Boolean = 
    v == t || (v match {
      case _: Var => false
      case Plus(t1, t2) => hasFreeOccurance(t1, t) || hasFreeOccurance(t2, t)
      case Mult(t1, t2) => hasFreeOccurance(t1, t) || hasFreeOccurance(t2, t)
      case Zero() => false
      case One() => false
    })

  override def hasFreeOccurance(f: ArithLogicFormula, t: ArithLogicTerm): Boolean = f match {
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

  private type Repl = Option[ArithLogicTerm | Unit]

  private def unifyReplacements(repls: Set[Repl]): Repl = {
    if repls.contains(None) then None else {
      val ts = repls.collect { 
        case Some(t: ArithLogicTerm) => t
      }

      if ts.isEmpty then 
        Some(())
      else if ts.size > 1 then 
        None 
      else 
        Some(ts.head)
    }
  }

  private def findReplacements(ts1: List[ArithLogicTerm], ts2: List[ArithLogicTerm], x: Var): Set[Repl] = {
    ts1.zip(ts2).map((t1, t2) => findReplacement(t1, t2, x)).toSet
  }

  private def findReplacement(src: ArithLogicTerm, dst: ArithLogicTerm, x: Var): Repl = {
    (src, dst) match {
      case _ if src == x => 
        Some(dst)

      case (y: Var, z: Var) if y == z => 
        Some(())

      case (Plus(t1, t2), Plus(t3, t4)) =>
        unifyReplacements(Set(
          findReplacement(t1, t3, x),
          findReplacement(t2, t4, x),
        ))

      case (Mult(t1, t2), Mult(t3, t4)) =>
        unifyReplacements(Set(
          findReplacement(t1, t3, x),
          findReplacement(t2, t4, x),
        ))

      case (Zero(), Zero()) => Some(())
      case (One(), One()) => Some(())

      case _ => None
    }
  }

  // two quantifiers Q y phi1 and Q y phi2 (with repl. var x)
  private def findReplacementInsideQuantifiers(
    y: Var, phi1: ArithLogicFormula, phi2: ArithLogicFormula,
    x: Var
  ): Repl = {
    if x != y then
      findReplacement(phi1, phi2, x)
    else if phi1 == phi2 then
      Some(())
    else None
  }

  override def findReplacement(src: ArithLogicFormula, dst: ArithLogicFormula, x: Var): Repl = {
    (src, dst) match {
      case (Equals(l1, r1), Equals(l2, r2)) => 
        unifyReplacements(Set(
          findReplacement(l1, l2, x),
          findReplacement(r1, r2, x)
        ))

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

  private def equalExcept(vs1: List[ArithLogicTerm], vs2: List[ArithLogicTerm], t1: ArithLogicTerm, t2: ArithLogicTerm): Boolean = {
    vs1.zip(vs2).forall((x, y) => equalExcept(x, y, t1, t2))
  }

  private def equalExcept(v1: ArithLogicTerm, v2: ArithLogicTerm, t1: ArithLogicTerm, t2: ArithLogicTerm): Boolean = {
    v1 == v2 || (v1 == t1 && v2 == t2) || ((v1, v2) match {
      case (Plus(s1, s2), Plus(s3, s4)) => 
        equalExcept(s1, s3, t1, t2) && equalExcept(s2, s4, t1, t2)

      case (Mult(s1, s2), Mult(s3, s4)) => 
        equalExcept(s1, s3, t1, t2) && equalExcept(s2, s4, t1, t2)

      case _ => false
    })
  }

  override def equalExcept(f1: ArithLogicFormula, f2: ArithLogicFormula, t1: ArithLogicTerm, t2: ArithLogicTerm): Boolean = {
    (f1, f2) match {
      case (Equals(l1, r1), Equals(l2, r2)) =>
        equalExcept(l1, l2, t1, t2) && equalExcept(r1, r2, t1, t2)

      case (And(phi1, psi1), And(phi2, psi2)) => 
        equalExcept(phi1, phi2, t1, t2) && equalExcept(psi1, psi2, t1, t2)

      case (Or(phi1, psi1), Or(phi2, psi2)) => 
        equalExcept(phi1, phi2, t1, t2) && equalExcept(psi1, psi2, t1, t2)

      case (Implies(phi1, psi1), Implies(phi2, psi2)) => 
        equalExcept(phi1, phi2, t1, t2) && equalExcept(psi1, psi2, t1, t2)

      case (Not(phi1), Not(phi2)) =>
        equalExcept(phi1, phi2, t1, t2)

      case (ForAll(x, phi1), ForAll(y, phi2)) if x == y =>
        if x == t1 then 
          phi1 == phi2
        else
          equalExcept(phi1, phi2, t1, t2)

      case (Exists(x, phi1), Exists(y, phi2)) if x == y =>
        if x == t1 then 
          phi1 == phi2
        else
          equalExcept(phi1, phi2, t1, t2)

      case (Contradiction(), Contradiction()) => true
      case (Tautology(), Tautology()) => true

      case _ => false
    }
  }
}
