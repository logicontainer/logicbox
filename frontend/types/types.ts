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
  boxInfo: {
    freshVar: string | null;
  };
  proof: Proof;
};

export type ProofStep = LineProofStep | BoxProofStep;

export type Proof = ProofStep[];

export type ProofMetadata = {
  id: string;
  title: string;
  logicName: LogicName;
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
  | { violationType: "wrongNumberOfReferences"; exp: number; actual: number; }
  | { violationType: "referenceShouldBeBox"; refIdx: number; }
  | { violationType: "referenceShouldBeLine"; refIdx: number; }
  | { violationType: "referenceDoesntMatchRule"; refIdx: number; expl: string }
  | { violationType: "referencesMismatch"; refs: number[]; expl: string }
  | { violationType: "formulaDoesntMatchReference"; refIdx: number; expl: string }
  | { violationType: "formulaDoesntMatchRule"; expl: string }
  | { violationType: "miscellaneousViolation"; expl: string }
  | { violationType: "stepNotFound"; stepId: string; expl: string }
  | { violationType: "referenceIdNotFound"; stepId: string; refIdx: number; refId: string; expl: string }
  | { violationType: "malformedReference"; stepId: string; refIdx: number; refId: string; expl: string }
  | { violationType: "referenceToLaterStep"; stepId: string; refIdx: number; refId: string }
  | { violationType: "scopeViolation"; stepId: string; stepScope: string; refIdx: number; refId: string; refScope: string }
  | { violationType: "referenceToUnclosedBox"; stepId: string; refIdx: number; boxId: string };

export type ViolationType = Violation["violationType"]

export type Diagnostic = {
  uuid: UUID;
  violationType: ViolationType;
  violation: Violation;
}

export type LogicName = 'propositionalLogic' | 'predicateLogic' | 'arithmetic'

export type ValidationRequest = {
  proof: Proof
  logicName: LogicName
}

export type ValidationResponse = {
  proof: Proof;
  diagnostics: Diagnostic[];
};

// Enum for placement options
export type Placement = "before" | "after";
