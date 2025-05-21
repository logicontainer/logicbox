package logicbox.framework

sealed trait Diagnostic[+Id] {
  def stepId: Id
}

object Diagnostic {
  case class RuleViolationAtStep[Id](stepId: Id, violation: RuleViolation) extends Diagnostic[Id]
  case class StepNotFound[Id](stepId: Id, expl: String) extends Diagnostic[Id]
  case class ReferenceIdNotFound[Id](stepId: Id, whichRef: Int, refId: Id, expl: String) extends Diagnostic[Id]
  case class MalformedReference[Id](stepId: Id, whichRef: Int, refId: Id, expl: String) extends Diagnostic[Id]

  case class ReferenceToLaterStep[+Id](stepId: Id, refIdx: Int, refId: Id) extends Diagnostic[Id]
  case class ScopeViolation[+Id](stepId: Id, stepScope: Scope[Id], refIdx: Int, refId: Id, refScope: Scope[Id]) extends Diagnostic[Id]
  case class ReferenceToUnclosedBox[+Id](stepId: Id, refIdx: Int, boxId: Id) extends Diagnostic[Id]
}
