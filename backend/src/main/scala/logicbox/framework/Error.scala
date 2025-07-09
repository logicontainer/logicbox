package logicbox.framework

import logicbox.rule.RulePart
import logicbox.rule.RulePart.*
import logicbox.framework.{RulePosition, Location}

sealed trait Error

object Error {
  case class MissingFormula() extends Error
  case class MissingRule() extends Error
  case class MissingRef(refIdx: Int) extends Error

  case class ReferenceOutOfScope(refIdx: Int) extends Error
  case class ReferenceToLaterStep(refIdx: Int) extends Error
  case class ReferenceToUnclosedBox(refIdx: Int) extends Error
  case class ReferenceBoxMissingFreshVar(refIdx: Int) extends Error
  case class ReferenceShouldBeBox(refIdx: Int) extends Error
  case class ReferenceShouldBeLine(refIdx: Int) extends Error

  case class WrongNumberOfReferences(exp: Int, actual: Int) extends Error
  case class ShapeMismatch(loc: Location) extends Error
  case class Ambiguous(what: RulePart, entries: List[Location]) extends Error
  case class Miscellaneous(pos: Location, expl: String) extends Error
}
