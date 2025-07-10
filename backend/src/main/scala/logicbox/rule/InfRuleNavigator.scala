package logicbox.rule

import logicbox.framework.{InfRule, RulePart, Navigator, Location}

class InfRuleNavigator(
  rulePartNavigator: Navigator[RulePart, RulePart]
) extends Navigator[InfRule, RulePart] {
  import Location.Step._
  override def get(subject: InfRule, loc: Location): Option[RulePart] = loc.steps match {
    case Conclusion :: rest => 
      rulePartNavigator.get(subject.conclusion, Location(rest))

    case Premise(idx) :: rest => 
      subject.premises.lift(idx)
        .flatMap(rulePartNavigator.get(_, Location(rest)))

    case Nil => None
  }
  
}
