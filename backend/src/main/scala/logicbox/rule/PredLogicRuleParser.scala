package logicbox.rule

object PredLogicRuleParser {
  import PredLogicRule._
  def parse(rule: String): Option[PredLogicRule] = rule match { 
    case "forall_elim" => Some(ForAllElim())
    case "forall_intro" => Some(ForAllIntro())
    case "exists_elim" => Some(ExistsElim())
    case "exists_intro" => Some(ExistsIntro())
    case "equality_intro" => Some(EqualityIntro())
    case "equality_elim" => Some(EqualityElim())
    case _ => None
  }
}
