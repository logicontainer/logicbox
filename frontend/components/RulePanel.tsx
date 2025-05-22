import {
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { InlineMath } from "react-katex";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useRuleset } from "@/contexts/RulesetProvider";
import { useState } from "react";

export default function RulePanel() {
  const { ruleset } = useRuleset();
  const { doTransition } = useInteractionState();
  const [hoveredRule, setHoveredRule] = useState<string | null>(null);

  const hoveredRuleDetails = ruleset.rules.find(
    (rule) => rule.ruleName === hoveredRule
  );
  const hoveredRuleDetailsLatex = hoveredRuleDetails
    ? createHighlightedLatexRule(
        hoveredRuleDetails.latex.ruleName,
        hoveredRuleDetails.latex.premises,
        hoveredRuleDetails.latex.conclusion,
        [],
        false
      )
    : "";

  const handleChangeRule = (ruleName: string) => {
    if (ruleName == null) {
      return;
    }
    doTransition({
      enum: TransitionEnum.UPDATE_RULE,
      ruleName,
    });
  };
  return (
    <div className="flex flex-col gap-2">
      <h2 className="text-lg font-bold">Select a rule</h2>
      <div className="grid grid-cols-3 gap-2">
        {ruleset &&
          ruleset.rules.map((rule) => {
            return (
              <div
                key={rule.ruleName}
                className="flex items-center justify-center gap-1 p-2 border border-gray-300 rounded-md cursor-pointer hover:bg-gray-100"
                onMouseOver={() => setHoveredRule(rule.ruleName)}
                onMouseLeave={() => setHoveredRule(null)}
                onClick={() => handleChangeRule(rule.ruleName)}
              >
                <h3 className="text">
                  <InlineMath math={rule.latex.ruleName}></InlineMath>
                </h3>
                <p className="text-sm text-gray-600"></p>
              </div>
            );
          })}
      </div>
      {hoveredRule && (
        <div className="p-2 bg-white border border-gray-300 rounded-md shadow-md flex items-center gap-2 justify-center">
          <p className="text-lg text-gray-600">
            <InlineMath math={hoveredRuleDetailsLatex} />
          </p>
        </div>
      )}
    </div>
  );
}
