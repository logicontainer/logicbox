package logicbox.formula

sealed abstract class PropLogicFormula

object PropLogicFormula {
  case class Tautology() extends PropLogicFormula
  case class Atom(c: Char) extends PropLogicFormula

  case class And(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula 
  case class Or(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula 
  case class Implies(phi: PropLogicFormula, psi: PropLogicFormula) extends PropLogicFormula 
  case class Not(phi: PropLogicFormula) extends PropLogicFormula 
  case class Contradiction() extends PropLogicFormula 
}

implicit val propLogicFormulaAsConnectiveFormula: ConnectiveFormula[PropLogicFormula] = new ConnectiveFormula[PropLogicFormula] {
  import PropLogicFormula._

  override def unapplyAnd(f: PropLogicFormula) = f match {
    case And(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyOr(f: PropLogicFormula) = f match {
    case Or(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyImplies(f: PropLogicFormula) = f match {
    case Implies(phi, psi) => Some(phi, psi)
    case _ => None
  }

  override def unapplyNot(f: PropLogicFormula) = f match {
    case Not(phi) => Some(phi)
    case _ => None
  }

  override def unapplyContradiction(f: PropLogicFormula) = f match {
    case Contradiction() => true
    case _ => false
  }
}
