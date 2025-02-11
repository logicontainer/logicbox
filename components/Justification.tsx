import { JustificationConfig, Justification as TJustification } from "@/types/types";

import { InlineMath } from "react-katex";

const justificationsConfig = [
  {
    rule: "premise",
    latexRule: "\\text{premise}",
    numRefs: 0
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
    rule: "assumption",
    latexRule: "\\text{ass.}",
    numRefs: 0
  },
  {
    rule: "implies-intro",
    latexRule: "\\rightarrow_i",
    numRefs: 1
  }

] as JustificationConfig[];

export function Justification ({ justification }: { justification: TJustification }) {
  const config = justificationsConfig.find((config) => config.rule == justification.rule)
  if (!config) return;
  const mathString = `${config.latexRule}${config.numRefs ? "\\,1,2" : ""}`
  return (
    <InlineMath math={mathString}></InlineMath>
  )
}