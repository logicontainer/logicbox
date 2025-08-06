package logicbox.formula

import logicbox.framework.RuleChecker
import logicbox.framework.Reference

sealed trait PredLogicTerm

object PredLogicTerm {
  case class Var(x: String) extends PredLogicTerm
  case class FunAppl(f: String, ps: List[PredLogicTerm]) extends PredLogicTerm
}

sealed trait PredLogicFormula

object PredLogicFormula {
  private type Term = PredLogicTerm
  private type Form = PredLogicFormula

  final case class Tautology() extends Form

  final case class Predicate(p: String, ps: List[Term]) extends Form
  final case class Equals(t1: Term, t2: Term) extends Form

  final case class Contradiction() extends Form
  final case class Not(phi: Form) extends Form

  sealed trait BinOp extends Form {
    def phi: Form
    def psi: Form
  }
  
  final case class And(phi: Form, psi: Form) extends BinOp
  final case class Or(phi: Form, psi: Form) extends BinOp
  final case class Implies(phi: Form, psi: Form) extends BinOp

  final case class Exists(x: PredLogicTerm.Var, phi: Form) extends Form
  final case class ForAll(x: PredLogicTerm.Var, phi: Form) extends Form
}

implicit val predLogicFormulaAsQuantifierFormula: QuantifierFormula[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var] = new QuantifierFormula[PredLogicFormula, PredLogicTerm, PredLogicTerm.Var] {
  import PredLogicFormula._

  override def unapplyExists(f: PredLogicFormula) = f match {
    case Exists(x, phi) => Some(x, phi)
    case _ => None
  }

  override def unapplyForAll(f: PredLogicFormula) = f match {
    case ForAll(x, phi) => Some(x, phi)
    case _ => None
  }

  override def unapplyEquals(f: PredLogicFormula) = f match {
    case Equals(t1, t2) => Some(t1, t2)
    case _ => None
  }
}

implicit val predLogicFormulaAsConnectiveFormula: ConnectiveFormula[PredLogicFormula] = new ConnectiveFormula[PredLogicFormula] {
  import PredLogicFormula._

  override def unapplyAnd(f: PredLogicFormula) = f match {
    case And(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyOr(f: PredLogicFormula) = f match {
    case Or(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyImplies(f: PredLogicFormula) = f match {
    case Implies(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyNot(f: PredLogicFormula) = f match {
    case Not(phi) => Some(phi)
    case _ => None
  }

  override def unapplyContradiction(f: PredLogicFormula) = f match {
    case Contradiction() => true
    case _ => false
  }
}
