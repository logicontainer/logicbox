package logicbox.framework

sealed trait RulePosition
object RulePosition {
  case object Conclusion extends RulePosition
  case class Premise(idx: Int) extends RulePosition
}
