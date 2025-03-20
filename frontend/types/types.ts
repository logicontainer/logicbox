export type Ruleset = {
  rulesetName: string;
  rules: Rule[];
};
export type Rule = {
  ruleName: string;
  numPremises: number;
  latex: {
    ruleName: string;
    premises: string[];
    conclusion: string;
  };
};

export type Justification = {
  rule: string | null;
  refs: string[];
};
export type LineProofStep = {
  uuid: string;
  stepType: string;
  formula: {
    userInput: string;
    unsynced?: boolean;
    latex?: string | null;
  };
  justification: Justification;
};
export type BoxProofStep = {
  uuid: string;
  stepType: string;
  proof: Proof;
};
export type ProofStep = LineProofStep | BoxProofStep;

export type Proof = ProofStep[];

export type LineNumberLine = {
  uuid: string;
  isBox: boolean;
  boxStartLine?: number;
  boxEndLine?: number;
  lineNumber?: number;
};

export type ProofStepDetails = {
  proofStep: ProofStep;
  parentBoxUuid: string | null;
  position: ProofStepPosition;
};

export type ProofStepPosition = {
  nearProofStepWithUuid: string;
  prepend: boolean;
};

type UUID = string;

// Individual violation types
type ReferencesMismatchViolation = {
  explanation: string;
  refs: UUID[];
};

type WrongNumberOfReferencesViolation = {
  explanation: string;
  expected: number;
  actual: number;
};

type ReferenceShouldBeBoxViolation = {
  explanation: string;
  ref: UUID;
};

type ReferenceShouldBeLineViolation = {
  explanation: string;
  ref: UUID;
};

type ReferenceDoesntMatchRuleViolation = {
  explanation: string;
  ref: UUID;
};

type FormulaDoesntMatchReferenceViolation = {
  explanation: string;
  ref: UUID;
};

type FormulaDoesntMatchRuleViolation = {
  explanation: string;
};

type MiscellaneousViolation = {
  explanation: string;
};

// Union type for all possible violations
type Violation =
  | {
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
    };

// Diagnostic type
export type Diagnostic = {
  uuid: UUID;
} & Violation;

// Main response type
export type ValidationResponse = {
  proof: Proof;
  diagnostics: Diagnostic[];
};

// Enum for placement options
export type Placement = "before" | "after";
