package logicbox.proof

sealed trait PLViolation

object PLViolation {
  case class WrongNumberOfReferences(exp: Int, actual: Int, expl: String = "") extends PLViolation
  case class ReferenceShouldBeBox(ref: Int, expl: String = "") extends PLViolation
  case class ReferenceShouldBeLine(ref: Int, expl: String = "") extends PLViolation
  case class ReferenceDoesntMatchRule(ref: Int, expl: String = "") extends PLViolation
  case class ReferencesMismatch(refs: List[Int], expl: String = "") extends PLViolation
  case class FormulaDoesntMatchReference(refs: Int, expl: String = "") extends PLViolation
  case class FormulaDoesntMatchRule(expl: String = "") extends PLViolation
  case class MiscellaneousViolation(expl: String = "") extends PLViolation
}
