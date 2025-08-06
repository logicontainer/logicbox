package logicbox.formula

sealed trait ArithLogicTerm
object ArithLogicTerm {
  private type T = ArithLogicTerm

  case class Var(x: String) extends ArithLogicTerm

  case class Zero() extends ArithLogicTerm
  case class One() extends ArithLogicTerm
  case class Plus(t1: T, t2: T) extends ArithLogicTerm
  case class Mult(t1: T, t2: T) extends ArithLogicTerm
}

sealed trait ArithLogicFormula

object ArithLogicFormula {
  private type Form = ArithLogicFormula
  private type Term = ArithLogicTerm
  private type Var = ArithLogicTerm.Var

  case class Tautology() extends Form

  case class Equals(t1: Term, t2: Term) extends Form

  case class Contradiction() extends Form
  case class Not(phi: Form) extends Form

  sealed trait BinOp extends Form {
    def phi: Form
    def psi: Form
  }

  case class And(phi: Form, psi: Form) extends BinOp
  case class Or(phi: Form, psi: Form) extends BinOp
  case class Implies(phi: Form, psi: Form) extends BinOp

  case class Exists(x: Var, phi: Form) extends Form
  case class ForAll(x: Var, phi: Form) extends Form
}

implicit val arithLogicTermIsArithmeticTerm: ArithmeticTerm[ArithLogicTerm] = new ArithmeticTerm[ArithLogicTerm] {
  import ArithLogicTerm._

  override def unapplyZero(t: ArithLogicTerm) = t match {
    case Zero() => true
    case _ => false
  }

  override def unapplyOne(t: ArithLogicTerm) = t match {
    case One() => true
    case _ => false
  }

  override def unapplyPlus(t: ArithLogicTerm) = t match {
    case Plus(t1, t2) => Some(t1, t2)
    case _ => None
  }

  override def unapplyMult(t: ArithLogicTerm) = t match {
    case Mult(t1, t2) => Some(t1, t2)
    case _ => None
  }
}

implicit val arithLogicFormulaIsConnectiveFormula: ConnectiveFormula[ArithLogicFormula] = new ConnectiveFormula[ArithLogicFormula] {
  import ArithLogicFormula._

  override def unapplyAnd(f: ArithLogicFormula) = f match {
    case And(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyOr(f: ArithLogicFormula) = f match {
    case Or(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyImplies(f: ArithLogicFormula) = f match {
    case Implies(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyNot(f: ArithLogicFormula) = f match {
    case Not(phi) => Some(phi)
    case _ => None
  }

  override def unapplyContradiction(f: ArithLogicFormula) = f match {
    case Contradiction() => true
    case _ => false
  }
}
implicit val arithLogicFormulaIsQuantifierFormula: QuantifierFormula[ArithLogicFormula, ArithLogicTerm, ArithLogicTerm.Var] = new QuantifierFormula[ArithLogicFormula, ArithLogicTerm, ArithLogicTerm.Var] {
  import ArithLogicFormula._
  override def unapplyExists(f: ArithLogicFormula) = f match {
    case Exists(x, phi) => Some(x, phi)
    case _ => None
  }

  override def unapplyForAll(f: ArithLogicFormula) = f match {
    case ForAll(x, phi) => Some(x, phi)
    case _ => None
  }

  override def unapplyEquals(f: ArithLogicFormula) = f match {
    case Equals(t1, t2) => Some(t1, t2)
    case _ => None
  }
}
