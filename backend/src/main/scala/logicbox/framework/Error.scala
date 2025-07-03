package logicbox.framework

import logicbox.rule.RulePart
import logicbox.rule.RulePart.*
import logicbox.framework.{RulePosition, Location}

sealed trait Error

object Error {
  case object MissingFormula extends Error
  case object MissingRule extends Error
  case class MissingRef(refIdx: Int) extends Error

  case class ReferenceOutOfScope(refIdx: Int) extends Error
  case class ReferenceToLaterStep(refIdx: Int) extends Error
  case class ReferenceToUnclosedBox(refIdx: Int) extends Error

  case class ReferenceShouldBeBox(refIdx: Int) extends Error
  case class ReferenceShouldBeLine(refIdx: Int) extends Error

  case class WrongNumberOfReferences(exp: Int, actual: Int) extends Error

  case class ShapeMismatch(pos: RulePosition, expected: TemplateFormula) extends Error
  case class Ambiguous(what: RulePart, entries: List[(RulePosition, Location)])

  case class Miscellaneous(pos: RulePosition, expl: String) extends Error
}
