package logicbox.framework

sealed trait RuleViolation
object RuleViolation {

  case object MissingFormula extends RuleViolation
  case object MissingRule extends RuleViolation
  case class MissingDetailInReference(refIdx: Int, expl: String) extends RuleViolation
  case class WrongNumberOfReferences(exp: Int, actual: Int, expl: String = "") extends RuleViolation
  case class ReferenceShouldBeBox(refIdx: Int, expl: String = "") extends RuleViolation
  case class ReferenceShouldBeLine(refIdx: Int, expl: String = "") extends RuleViolation
  case class ReferenceDoesntMatchRule(refIdx: Int, expl: String = "") extends RuleViolation
  case class ReferencesMismatch(refs: List[Int], expl: String = "") extends RuleViolation
  case class FormulaDoesntMatchReference(refIdx: Int, expl: String = "") extends RuleViolation
  case class FormulaDoesntMatchRule(expl: String = "") extends RuleViolation
  case class MiscellaneousViolation(expl: String = "") extends RuleViolation
}
