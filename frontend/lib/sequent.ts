import { Proof } from "@/types/types";


export function createSequentLaTeX(proof: Proof): string | null {
  const premises = proof
  .filter(step => step.stepType === "line")
  .filter(step => step.justification.rule === "premise" && step.formula.latex !== null)
  .map(step => step.formula.latex)

  const conclusion = proof.findLast(step => step.stepType === "line")

  return conclusion === undefined ? null : 
    `${premises.join(", ")} \\vdash ${conclusion.formula.latex ?? "???"}`
}
