package logicbox.rule

sealed trait PredLogicRule

object PredLogicRule {
  case class ForAllElim() extends PredLogicRule
  case class ForAllIntro() extends PredLogicRule
}
