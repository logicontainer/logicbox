"use client";

import { LineNumberLine, Justification as TJustification } from "@/types/types";

import { InlineMath } from "react-katex";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useRuleset } from "@/contexts/RulesetProvider";

export function Justification ({ justification, lines, uuid }: { justification: TJustification, lines: LineNumberLine[], uuid: string }) {
  const { ruleset } = useRuleset();
  const rule = ruleset.rules.find((rule) => rule.name == justification.name)
  if (!rule) return;
  const refs = justification.refs.map((ref) => {
    const referencedLine = lines.find((line) => line.uuid == ref);
    if (referencedLine?.isBox) {
      return `${referencedLine.boxStartLine}\\text{-}${referencedLine.boxEndLine}`
    } else {
      return referencedLine?.lineNumber;
    }
  })
  const mathString = `${rule.latex.name}${refs ? `\\,${refs.join(",")}` : ""}`
  const tooltipId = `tooltip-id-${uuid}`
  return (<>
    <div data-tooltip-id={tooltipId} data-tooltip-content={createHighlightedLatexRule(rule.latex.name, rule.latex.premises, rule.latex.conclusion, [], false)}><InlineMath math={mathString}></InlineMath></div>
  </>);
}