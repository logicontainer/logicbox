export type Justification = {
  rule: string,
  refs: string[]
}
export type JustificationConfig = {
  rule: string,
  latexRule: string,
  numRefs: number
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

export type LineNumberLine = { uuid: string, lineNumber: number };