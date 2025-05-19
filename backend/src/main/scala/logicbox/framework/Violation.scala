package logicbox.framework

sealed trait Violation
object Violation {

  case object MissingFormula extends Violation
  case object MissingRule extends Violation
  case class MissingDetailInReference(refIdx: Int, expl: String) extends Violation

  case class WrongNumberOfReferences(exp: Int, actual: Int, expl: String = "") extends Violation
  case class ReferenceShouldBeBox(ref: Int, expl: String = "") extends Violation
  case class ReferenceShouldBeLine(ref: Int, expl: String = "") extends Violation
  case class ReferenceDoesntMatchRule(ref: Int, expl: String = "") extends Violation
  case class ReferencesMismatch(refs: List[Int], expl: String = "") extends Violation
  case class FormulaDoesntMatchReference(refs: Int, expl: String = "") extends Violation
  case class FormulaDoesntMatchRule(expl: String = "") extends Violation
  case class MiscellaneousViolation(expl: String = "") extends Violation
}
