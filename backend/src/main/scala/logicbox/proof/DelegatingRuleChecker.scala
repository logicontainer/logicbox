package logicbox

import logicbox.framework.{CheckableRule, RuleChecker, Reference}

// rule checker that simply delegates to each rules' own `check` method (given that R is checkable)
class DelegatingRuleChecker[Formula, Rule <: CheckableRule[Formula, BoxInfo, Viol], BoxInfo, Viol]
  extends RuleChecker[Formula, Rule, BoxInfo, Viol]
{
  override def check(rule: Rule, formula: Formula, refs: List[Reference[Formula, BoxInfo]]): List[Viol] =
    rule.check(formula, refs)
}
