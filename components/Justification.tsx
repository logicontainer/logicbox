import { JustificationConfig, LineNumberLine, Justification as TJustification } from "@/types/types";

import { InlineMath } from "react-katex";

const justificationConfig = [
  {
    rule: "premise",
    latexRule: "\\text{premise}",
    numRefs: 0
  },
  {
    rule: "assumption",
    latexRule: "\\text{ass.}",
    numRefs: 0
  },
  {
    rule: "and_intro",
    latexRule: "\\land i",
    numRefs: 2
  },
  {
    rule: "and_elim_1",
    latexRule: "\\land e_1",
    numRefs: 1
  },
  {
    rule: "and_elim_2",
    latexRule: "\\land e_2",
    numRefs: 1
  },
  {
    rule: "or_intro_1",
    latexRule: "\\lor i_1",
    numRefs: 1
  },
  {
    rule: "or_intro_2",
    latexRule: "\\lor i_2",
    numRefs: 1
  },
  {
    rule: "or_elim",
    latexRule: "\\lor e",
    numRefs: 3
  },
  {
    rule: "implies_intro",
    latexRule: "\\rightarrow i",
    numRefs: 1
  },
  {
    rule: "implies_elim",
    latexRule: "\\rightarrow e",
    numRefs: 2
  },
  {
    rule: "not_intro",
    latexRule: "\\lnot i",
    numRefs: 1
  },
  {
    rule: "not_elim",
    latexRule: "\\lnot e",
    numRefs: 2
  },
  {
    rule: "bot_elim",
    latexRule: "\\bot e",
    numRefs: 1
  },
  {
    rule: "not_not_elim",
    latexRule: "\\not\\not e",
    numRefs: 1
  },
  {
    rule: "modus_tollens",
    latexRule: "\\text{MT}",
    numRefs: 2
  },
  {
    rule: "not_not_intro",
    latexRule: "\\not\\not i",
    numRefs: 1
  },
  {
    rule: "proof_by_contradiction",
    latexRule: "\\text{PBC}",
    numRefs: 1
  },
  {
    rule: "law_of_excluded_middle",
    latexRule: "\\text{LEM}",
    numRefs: 0
  }
] as JustificationConfig[];

export function Justification ({ justification, lines }: { justification: TJustification, lines: LineNumberLine[] }) {
  const config = justificationConfig.find((config) => config.rule == justification.rule)
  if (!config) return;
  const refs = justification.refs.map((ref) => {
    const referencedLine = lines.find((line) => line.uuid == ref);
    if (referencedLine?.isBox) {
      return `${referencedLine.boxStartLine}\\text{-}${referencedLine.boxEndLine}`
    } else {
      return referencedLine?.lineNumber;
    }
  })
  const mathString = `${config.latexRule}${refs ? `\\,${refs.join(",")}` : ""}`
  return (
    <InlineMath math={mathString}></InlineMath>
  )
}