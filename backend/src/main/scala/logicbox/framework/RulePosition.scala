package logicbox.framework

sealed trait RulePosition
object RulePosition {
  case object Formula extends RulePosition
  case class Ref(idx: Int) extends RulePosition
}
