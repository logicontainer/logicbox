package logicbox.rule

object ArithLogicRuleParser {
  import ArithLogicRule._
  def parse(rule: String): Option[ArithLogicRule] = rule match {
    case "peano_1" => Some(Peano1())
    case "peano_2" => Some(Peano2())
    case "peano_3" => Some(Peano3())
    case "peano_4" => Some(Peano4())
    case "peano_5" => Some(Peano5())
    case "peano_6" => Some(Peano6())
    case "induction" => Some(Induction())
    case _ => None
  }
}
