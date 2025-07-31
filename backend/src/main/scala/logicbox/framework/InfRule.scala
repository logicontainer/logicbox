package logicbox.framework

case class InfRule(
  premises: List[RulePart],
  conclusion: RulePart.TemplateFormula
)
