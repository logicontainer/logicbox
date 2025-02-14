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
  const tooptipExample = createHighlightedLatexRule(rule.latex.name, rule.latex.premises, rule.latex.conclusion, [], false)
  return (<div>
    <InlineMath math={tooptipExample} />
  </div>)
}