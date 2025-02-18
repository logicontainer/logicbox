export type Ruleset = {
  rulesetName: string;
  rules: Rule[];
}
export type Rule = {
  ruleName: string,
  numPremises: number,
  latex: {
    ruleName: string,
    premises: string[],
    conclusion: string
  }
}

export type Justification = {
  ruleName: string,
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
  proof: Proof
}
export type ProofStep = LineProofStep | BoxProofStep;

export type Proof = ProofStep[]

export type LineNumberLine = { uuid: string, isBox: boolean, boxStartLine?: number, boxEndLine?: number, lineNumber?: number };

export type ProofStepDetails = {
  proofStep: ProofStep,
  parentBoxUuid: string | null,
  position: ProofStepPosition
}

export type ProofStepPosition = {
  nearProofStepWithUuid: string,
  prepend: boolean
}