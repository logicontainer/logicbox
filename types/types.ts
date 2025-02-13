export type Ruleset = {
  rulesetName: string;
  rules: Rule[];
}
export type Rule = {
  name: string,
  numPremises: number,
  latex: {
    name: string,
    premises: string[],
    conclusion: ""
  }
}

export type Justification = {
  name: string,
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

export type LineNumberLine = { uuid: string, isBox: boolean, boxStartLine?: number, boxEndLine?: number, lineNumber?: number };
