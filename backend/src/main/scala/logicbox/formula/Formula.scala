package logicbox.formula

sealed trait FormulaKind

object FormulaKind {
  case object Prop extends FormulaKind
  case object Pred extends FormulaKind
  case object Arith extends FormulaKind

  type Prop = Prop.type
  type Pred = Pred.type
  type Arith = Arith.type
}

sealed trait Term[K <: FormulaKind]
object Term {
  import FormulaKind._

  // arithmetic + predicate logic
  final case class Var[K <: (Pred | Arith)](x: String) extends Term[K]

  // predicate logic
  final case class FunAppl(f: String, ps: List[Term[Pred]]) extends Term[Pred]

  // arithmetic
  final case class Zero() extends Term[Arith]
  final case class One() extends Term[Arith]
  final case class Plus(t1: Term[Arith], t2: Term[Arith]) extends Term[Arith]
  final case class Mult(t1: Term[Arith], t2: Term[Arith]) extends Term[Arith]
}

sealed trait Formula[K <: FormulaKind]
object Formula {
  import FormulaKind._

  // all of them
  sealed trait BinOp[K <: FormulaKind] extends Formula[K] {
    def phi: Formula[K]
    def psi: Formula[K]
  }
  final case class And[K <: FormulaKind](phi: Formula[K], psi: Formula[K]) extends BinOp[K]
  final case class Or[K <: FormulaKind](phi: Formula[K], psi: Formula[K]) extends BinOp[K]
  final case class Implies[K <: FormulaKind](phi: Formula[K], psi: Formula[K]) extends BinOp[K]
  final case class Not[K <: FormulaKind](phi: Formula[K]) extends Formula[K]
  final case class Contradiction[K <: FormulaKind]() extends Formula[K]
  final case class Tautology[K <: FormulaKind]() extends Formula[K]

  // propositional logic
  final case class Atom(c: Char) extends Formula[Prop]

  // predicate logic
  final case class Predicate(p: String, ps: List[Term[Pred]]) extends Formula[Pred]

  // predicate logic and arithmetic
  final case class Equals[K <: (Pred | Arith)](t1: Term[K], t2: Term[K]) extends Formula[K]
  final case class ForAll[K <: (Pred | Arith)](x: Term.Var[K], phi: Formula[K]) extends Formula[K]
  final case class Exists[K <: (Pred | Arith)](x: Term.Var[K], phi: Formula[K]) extends Formula[K]
}

implicit def asConnectiveFormula[K <: FormulaKind]: ConnectiveFormula[Formula[K]] = new ConnectiveFormula[Formula[K]] {
  import Formula._
  override def unapplyNot(f: Formula[K]): Option[Formula[K]] = f match {
    case Not(phi) => Some(phi)
    case _ => None
  }
  
  override def unapplyContradiction(f: Formula[K]): Boolean = f.isInstanceOf[Contradiction[K]]

  override def unapplyImplies(f: Formula[K]): Option[(Formula[K], Formula[K])] = f match {
    case Implies(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyOr(f: Formula[K]): Option[(Formula[K], Formula[K])] = f match {
    case Or(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyAnd(f: Formula[K]): Option[(Formula[K], Formula[K])] = f match {
    case And(phi, psi) => Some(phi, psi)
    case _ => None
  }
}

implicit def asQuantifierFormula[K <: (FormulaKind.Pred | FormulaKind.Arith)]: QuantifierFormula[Formula[K], Term[K], Term.Var[K]] = new QuantifierFormula[Formula[K], Term[K], Term.Var[K]] {
  import Formula._, Term._
  override def unapplyExists(f: Formula[K]): Option[(Var[K], Formula[K])] = f match {
    case Exists(x, phi) => Some(x, phi)
    case _ => None
  }

  override def unapplyForAll(f: Formula[K]): Option[(Var[K], Formula[K])] = f match {
    case ForAll(x, phi) => Some(x, phi)
    case _ => None
  }

  override def unapplyEquals(f: Formula[K]): Option[(Term[K], Term[K])] = f match {
    case Formula.Equals(t1, t2) => Some(t1, t2)
    case _ => None
  }
}

implicit val asArithmeticTerm: ArithmeticTerm[Term[FormulaKind.Arith]] = new ArithmeticTerm[Term[FormulaKind.Arith]] {
  import Formula._, Term._, FormulaKind.Arith
  override def unapplyZero(t: Term[Arith]): Boolean = t.isInstanceOf[Term.Zero]
  override def unapplyOne(t: Term[Arith]): Boolean = t.isInstanceOf[Term.One]
  override def unapplyPlus(t: Term[Arith]): Option[(Term[Arith], Term[Arith])] = t match {
    case Plus(t1, t2) => Some(t1, t2)
    case _ => None
  }
  override def unapplyMult(t: Term[Arith]): Option[(Term[Arith], Term[Arith])] = t match {
    case Mult(t1, t2) => Some(t1, t2)
    case _ => None
  }
}


// aliases
type PropLogicFormula = Formula[FormulaKind.Prop]

type PredLogicTerm = Term[FormulaKind.Pred]
type PredLogicFormula = Formula[FormulaKind.Pred]

type ArithLogicTerm = Term[FormulaKind.Arith]
type ArithLogicFormula = Formula[FormulaKind.Arith]
