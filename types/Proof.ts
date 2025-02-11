export type Justification = {
  rule: "premise" | "and_e_1" | "and_e_2"
  refs: string[]
}
export type LineProofStep = {
  uuid: string,
  stepType: string,
  formula: string,
  latexFormula: string,
  justification: Justification

}
export type BoxProofStep = {
  uuid: string,
  stepType: string,
  proof: ProofStep[]
}
export type ProofStep = LineProofStep | BoxProofStep;

