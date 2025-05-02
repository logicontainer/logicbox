"use client";

import { Justification as TJustification, TLineNumber } from "@/types/types";

import { InlineMath } from "react-katex";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useRuleset } from "@/contexts/RulesetProvider";

export function Justification({
  justification,
  lines,
  onHover,
  onClickRule,
  onClickRef,
}: {
  justification: TJustification;
  lines: TLineNumber[];
  onHover: (highlightedLatex: string) => void;
  onClickRule: () => void;
  onClickRef: (idx: number) => void;
}) {
  const { ruleset } = useRuleset();
  const rule = ruleset.rules.find(
    (rule) => rule.ruleName == justification.rule
  );
  if (!rule) return;
  const refs = justification.refs.map((ref) => {
    const referencedLine = lines.find((line) => line.uuid == ref);
    if (referencedLine?.isBox) {
      return `${referencedLine.boxStartLine}\\text{-}${referencedLine.boxEndLine}`;
    } else {
      return referencedLine?.lineNumber;
    }
  });
  return (
    <span>
      <span
        className="hover:text-red-500"
        onMouseOver={() =>
          onHover(
            createHighlightedLatexRule(
              rule.latex.ruleName,
              rule.latex.premises,
              rule.latex.conclusion,
              [],
              false
            )
          )
        }
        onClick={e => {
          onClickRule()
          e.stopPropagation()
        }}
      >
        <InlineMath math={rule.latex.ruleName}></InlineMath>
      </span>
      {refs && (
        <span>
          <InlineMath math={`\\,`} />
          {refs.map((ref, i) => {
            const comma = i < refs.length - 1 ? "," : "";
            return (
              <span
                key={i}
                className="hover:text-red-500"
                onClick={e => {
                  onClickRef(i)
                  e.stopPropagation()
                }}
                onMouseOver={() =>
                  onHover(
                    createHighlightedLatexRule(
                      rule.latex.ruleName,
                      rule.latex.premises,
                      rule.latex.conclusion,
                      [i],
                      false
                    )
                  )
                }
              >
                <InlineMath math={`${ref || "?"}${comma}`} />
              </span>
            );
          })}
        </span>
      )}
    </span>
  );
}
