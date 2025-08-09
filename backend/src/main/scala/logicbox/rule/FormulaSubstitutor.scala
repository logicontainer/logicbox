package logicbox.rule

import logicbox.formula._, Term._, Formula._

class FormulaSubstitutor[K <: (FormulaKind.Pred | FormulaKind.Arith)] extends Substitutor[Formula[K], Term[K], Term.Var[K]] {
  type Var = Term.Var[K]

  private def substitute(src: Term[K], t: Term[K], x: Var): Term[K] = src match {
    case y: Var if y == x => t
    case y: Var => y

    case Zero() | One() => src

    case FunAppl(f, ps) => 
      FunAppl(f, ps.map(substitute(_, t, x)))

    case Plus(t1, t2) => Plus(substitute(t1, t, x), substitute(t2, t, x))
    case Mult(t1, t2) => Mult(substitute(t1, t, x), substitute(t2, t, x))
  }

  override def substitute(f: Formula[K], t: Term[K], x: Var): Formula[K] = f match {
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

  override def isFreeFor(f: Formula[K], t: Term[K], x: Var): Boolean = {
    def visitT(tt: Term[K], boundVars: Set[Var]): Boolean = tt match {
      case xx: Var if xx == x =>
        // no bound var must occur in t
        boundVars.forall(!hasFreeOccurance(t, _))

      case FunAppl(_, ps) => ps.forall(visitT(_, boundVars))
      case tt @ (Plus(_, _) | Mult(_, _)) => visitT(tt.t1, boundVars) && visitT(tt.t2, boundVars)

      case Zero() | One() => true
      case y: Var => true
    }

    def visitF(ff: Formula[K], boundVars: Set[Var] = Set()): Boolean = ff match {
      case ff: Quantifier[K] => 
        // if x becomes bound, no need to look further...
        ff.x == x || visitF(ff.phi, boundVars + ff.x)

      case Predicate(_, ts) => ts.forall(t => visitT(t, boundVars))
      case And(phi, psi) => visitF(phi, boundVars) && visitF(psi, boundVars)
      case Or(phi, psi) => visitF(phi, boundVars) && visitF(psi, boundVars)
      case Implies(phi, psi) => visitF(phi, boundVars) && visitF(psi, boundVars)
      case Not(phi) => visitF(phi, boundVars)
      case Contradiction() | Tautology() => true
      case Equals(t1, t2) => visitT(t1, boundVars) && visitT(t2, boundVars)
    }

    visitF(f)
  }
  
  // true iff v contains occurance of t
  private def hasFreeOccurance(v: Term[K], t: Term[K]): Boolean = 
    v == t || (v match {
      case FunAppl(_, ps) => ps.exists(hasFreeOccurance(_, t))
      case Plus(t1, t2) => hasFreeOccurance(t1, t) || hasFreeOccurance(t2, t)
      case Mult(t1, t2) => hasFreeOccurance(t1, t) || hasFreeOccurance(t2, t)
      case Var(_) | One() | Zero() => false
    })

  override def hasFreeOccurance(f: Formula[K], t: Term[K]): Boolean = f match {
    case Predicate(p, ps) => 
      ps.exists(hasFreeOccurance(_, t))

    case f @ (And(_, _) | Or(_, _) | Implies(_, _)) => 
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

  private type Repl = Option[Either[Unit, Term[K]]]

  private def unifyReplacements(repls: Set[Repl]): Repl = {
    if repls.contains(None) then None else {
      val ts = repls.collect { 
        case Some(Right(t)) => t
      }

      if ts.isEmpty then 
        Some(Left(()))
      else if ts.size > 1 then 
        None 
      else 
        Some(Right(ts.head))
    }
  }

  private def findReplacements(ts1: List[Term[K]], ts2: List[Term[K]], x: Var): Set[Repl] = {
    ts1.zip(ts2).map((t1, t2) => findReplacement(t1, t2, x)).toSet
  }

  private def findReplacement(src: Term[K], dst: Term[K], x: Var): Repl = {
    (src, dst) match {
      case _ if src == x => 
        Some(Right(dst))

      case (FunAppl(f, ys), FunAppl(g, zs)) if f == g =>
        unifyReplacements(findReplacements(ys, zs, x))

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

      case (y: Var, z: Var) if y == z => 
        Some(Left(()))

      case (Zero(), Zero()) => Some(Left(()))
      case (One(), One()) => Some(Left(()))

      case _ => None
    }
  }

  // two quantifiers Q y phi1 and Q y phi2 (with repl. var x)
  private def findReplacementInsideQuantifiers(
    y: Var, phi1: Formula[K], phi2: Formula[K],
    x: Var
  ): Repl = {
    if x != y then
      findReplacement(phi1, phi2, x)
    else if phi1 == phi2 then
      Some(Left(()))
    else None
  }

  override def findReplacement(src: Formula[K], dst: Formula[K], x: Var): Repl = {
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

      case (Contradiction(), Contradiction()) => Some(Left(()))
      case (Tautology(), Tautology()) => Some(Left(()))

      case _ => None
    }
  }

  private def equalExcept(vs1: List[Term[K]], vs2: List[Term[K]], t1: Term[K], t2: Term[K]): Boolean = {
    vs1.zip(vs2).forall((x, y) => equalExcept(x, y, t1, t2))
  }

  private def equalExcept(v1: Term[K], v2: Term[K], t1: Term[K], t2: Term[K]): Boolean = {
    v1 == v2 || (v1 == t1 && v2 == t2) || ((v1, v2) match {
      case (FunAppl(f, xs), FunAppl(g, ys)) if f == g =>
        equalExcept(xs, ys, t1, t2)

      case (Plus(s1, s2), Plus(s3, s4)) => 
        equalExcept(s1, s3, t1, t2) && equalExcept(s2, s4, t1, t2)

      case (Mult(s1, s2), Mult(s3, s4)) => 
        equalExcept(s1, s3, t1, t2) && equalExcept(s2, s4, t1, t2)

      case _ => false
    })
  }

  override def equalExcept(f1: Formula[K], f2: Formula[K], t1: Term[K], t2: Term[K]): Boolean = {
    (f1, f2) match {
      case (Predicate(p, xs), Predicate(q, ys)) if p == q =>
        equalExcept(xs, ys, t1, t2)

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
