import { InlineMath } from "react-katex"
import { Rule } from "@/types/types";
import { createHighlightedLatexRule } from "@/lib/rules"
import { useRuleset } from "@/contexts/RulesetProvider"

export function RulesDropdown () {
  const rulesetContext = useRuleset();
  return (
    <ul>
      {rulesetContext.ruleset.rules.map((rule) => {
        return (
          <RulesDropdownItem key={rule.name} rule={rule} />
        )
      })}
    </ul>
  )
}


export function RulesDropdownItem ({ rule }: { rule: Rule }) {
  const highlight = [0]
  const tooptipExample = createHighlightedLatexRule(rule.latex.name, rule.latex.premises, rule.latex.conclusion, highlight, false)
  return (<div>
    <InlineMath math={tooptipExample} />
  </div>)
}