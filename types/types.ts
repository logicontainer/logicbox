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
  formulaUnsynced?: boolean
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


type ReferencesMismatchViolation = {
  explanation: string;
  refs: string[];
};

type WrongNumberOfReferencesViolation = {
  explanation: string;
  expected: number;
  actual: number;
};

type ReferenceShouldBeBoxViolation = {
  explanation: string;
  ref: string;
};

type ReferenceShouldBeLineViolation = {
  explanation: string;
  ref: string;
};

type ReferenceDoesntMatchRuleViolation = {
  explanation: string;
  ref: string;
};

type FormulaDoesntMatchReferenceViolation = {
  explanation: string;
  ref: string;
};

type FormulaDoesntMatchRuleViolation = {
  explanation: string;
};

type MiscellaneousViolation = {
  explanation: string;
};

export type ProofStepDiagnostics =
  ({
    violationType: "references_mismatch";
    violation: ReferencesMismatchViolation;
  }
    | {
      violationType: "wrong_number_of_references";
      violation: WrongNumberOfReferencesViolation;
    }
    | {
      violationType: "reference_should_be_box";
      violation: ReferenceShouldBeBoxViolation;
    }
    | {
      violationType: "reference_should_be_line";
      violation: ReferenceShouldBeLineViolation;
    }
    | {
      violationType: "reference_doesnt_match_rule";
      violation: ReferenceDoesntMatchRuleViolation;
    }
    | {
      violationType: "formula_doesnt_match_reference";
      violation: FormulaDoesntMatchReferenceViolation;
    }
    | {
      violationType: "formula_doesnt_match_rule";
      violation: FormulaDoesntMatchRuleViolation;
    }
    | {
      violationType: "miscellaneousViolation";
      violation: MiscellaneousViolation;
    }) & { uuid: string };

export type ProofDiagnostics = {
  isValid: boolean;
  diagnostics: ProofStepDiagnostics[];
};