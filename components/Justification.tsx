"use client";

import { LineNumberLine, Justification as TJustification } from "@/types/types";

import { InlineMath } from "react-katex";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useRuleset } from "@/contexts/RulesetProvider";

export function Justification ({
  justification,
  lines,
  onHover,
}: {
  justification: TJustification;
  lines: LineNumberLine[];
  onHover: (highlightedLatex: string) => void;
}) {
  const { ruleset } = useRuleset();
  const rule = ruleset.rules.find((rule) => rule.name == justification.name);
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
              rule.latex.name,
              rule.latex.premises,
              rule.latex.conclusion,
              [],
              false
            )
          )
        }
      >
        <InlineMath math={rule.latex.name}></InlineMath>
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
                onMouseOver={() =>
                  onHover(
                    createHighlightedLatexRule(
                      rule.latex.name,
                      rule.latex.premises,
                      rule.latex.conclusion,
                      [i],
                      false
                    )
                  )
                }
              >
                <InlineMath math={`${ref}${comma}`} />
              </span>
            )
          })}
        </span>)}
    </span>
  );
}