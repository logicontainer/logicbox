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
  stepType: 'line';
  formula: {
    userInput: string;
    unsynced?: boolean;
    ascii: string | null;
    latex: string | null;
  };
  justification: Justification;
};

export type BoxProofStep = {
  uuid: string;
  stepType: 'box';
  proof: Proof;
};

export type ProofStep = LineProofStep | BoxProofStep;

export type Proof = ProofStep[];

export type ProofMetadata = {
  id: string;
  title: string;
};
export type ProofWithMetadata = {
  proof: Proof;
} & ProofMetadata;

export type TLineNumber = {
  uuid: string;
  stepType: "line" | "box";
} & (
  | { stepType: "line"; lineNumber: number }
  | { stepType: "box"; boxStartLine: number; boxEndLine: number }
);

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


export type Violation =
  | { violationType: "missingFormula" }
  | { violationType: "missingRule" }
  | { violationType: "missingDetailInReference"; refIdx: number; expl: string }
  | { violationType: "propositionalLogic:wrongNumberOfReferences"; exp: number; actual: number; }
  | { violationType: "propositionalLogic:referenceShouldBeBox"; ref: number; }
  | { violationType: "propositionalLogic:referenceShouldBeLine"; ref: number; }
  | { violationType: "propositionalLogic:referenceDoesntMatchRule"; ref: number; expl: string }
  | { violationType: "propositionalLogic:referencesMismatch"; refs: number[]; expl: string }
  | { violationType: "propositionalLogic:formulaDoesntMatchReference"; refs: number; expl: string }
  | { violationType: "propositionalLogic:formulaDoesntMatchRule"; expl: string }
  | { violationType: "propositionalLogic:miscellaneousViolation"; expl: string }
  | { violationType: "stepNotFound"; stepId: string; expl: string }
  | { violationType: "referenceIdNotFound"; stepId: string; whichRef: number; refId: string; expl: string }
  | { violationType: "malformedReference"; stepId: string; whichRef: number; refId: string; expl: string }
  | { violationType: "referenceToLaterStep"; stepId: string; refIdx: number; refId: string }
  | { violationType: "scopeViolation"; stepId: string; stepScope: string; refIdx: number; refId: string; refScope: string }
  | { violationType: "referenceToUnclosedBox"; stepId: string; refIdx: number; boxId: string };

export type ViolationType = Violation["violationType"]

export type Diagnostic = {
  uuid: UUID;
  violationType: string;
  violation: any;
}

export type ValidationResponse = {
  proof: Proof;
  diagnostics: Diagnostic[];
};

// Enum for placement options
export type Placement = "before" | "after";
