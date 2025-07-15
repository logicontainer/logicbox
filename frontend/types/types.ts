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
  createdAt: string;
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

export type RulePosition = "conclusion" | "premise 0" | "premise 1" | "premise 2" | "premise 3" | "premise 4" | "premise 5"

export type Diagnostic = { uuid: UUID } & (
  | { errorType: "MissingFormula" }
  | { errorType: "MissingRule" }
  | { errorType: "MissingRef", refIdx: number }

  | { errorType: "ReferenceOutOfScope", refIdx: number }
  | { errorType: "ReferenceToLaterStep", refIdx: number }
  | { errorType: "ReferenceToUnclosedBox", refIdx: number  }
  | { errorType: "ReferenceBoxMissingFreshVar", refIdx: number  }
  | { errorType: "ReferenceShouldBeBox", refIdx: number  }
  | { errorType: "ReferenceShouldBeLine", refIdx: number  }

  | { errorType: "WrongNumberOfReferences", expected: number, actual: number }
  | { errorType: "ShapeMismatch", rulePosition: RulePosition, expected: string, actual: string }
  | { errorType: "Ambiguous", subject: string, entries: { rulePosition: RulePosition, meta: string, actual: string }[] }
  | { errorType: "Miscellaneous", rulePosition: RulePosition, explanation: string }
)

export type ErrorType = Diagnostic["errorType"]

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
