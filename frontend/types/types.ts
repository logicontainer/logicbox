import { z } from "zod";

// ---------- Core ----------

export const UUIDSchema = z.string();

// ---------- Rule & Ruleset ----------

export const LatexSchema = z.object({
  ruleName: z.string(),
  premises: z.array(z.string()),
  conclusion: z.string(),
});

export const RuleSchema = z.object({
  ruleName: z.string(),
  numPremises: z.number(),
  latex: LatexSchema,
});

export const RulesetSchema = z.object({
  rulesetName: z.string(),
  rules: z.array(RuleSchema),
});

// ---------- Justification ----------

export const JustificationSchema = z.object({
  rule: z.string().nullable(),
  refs: z.array(z.string()),
});

// ---------- Formula ----------

export const FormulaSchema = z.object({
  userInput: z.string(),
  unsynced: z.boolean().optional(),
  ascii: z.string().nullable(),
  latex: z.string().nullable(),
});

// ---------- Proof Steps ----------

export const LineProofStepSchema = z.object({
  uuid: UUIDSchema,
  stepType: z.literal("line"),
  formula: FormulaSchema,
  justification: JustificationSchema,
});

// Recursive schemas: Proof <-> ProofStep
export type ProofStep = z.infer<typeof LineProofStepSchema> | z.infer<typeof BoxProofStepSchema>;

// Lazy declarations
export const ProofStepSchema: z.ZodType<ProofStep> = z.lazy(() =>
  z.union([LineProofStepSchema, BoxProofStepSchema])
);

export type Proof = ProofStep[];
export const ProofSchema: z.ZodType<Proof> = z.lazy(() => z.array(ProofStepSchema));

export const BoxProofStepSchema: z.ZodType<{
  uuid: string;
  stepType: "box";
  boxInfo: { freshVar: string | null };
  proof: Proof;
}> = z.lazy(() =>
  z.object({
    uuid: UUIDSchema,
    stepType: z.literal("box"),
    boxInfo: z.object({
      freshVar: z.string().nullable(),
    }),
    proof: ProofSchema,
  })
);

// ---------- Metadata ----------

export const LogicNameSchema = z.enum([
  "propositionalLogic",
  "predicateLogic",
  "arithmetic",
]);

export const ProofMetadataSchema = z.object({
  id: z.string(),
  title: z.string(),
  createdAt: z.string(),
  logicName: LogicNameSchema,
});

export const ProofWithMetadataSchema = ProofMetadataSchema.extend({
  proof: ProofSchema,
});

// ---------- Line Numbers ----------

export const TLineNumberSchema = z.union([
  z.object({
    uuid: UUIDSchema,
    stepType: z.literal("line"),
    lineNumber: z.number(),
  }),
  z.object({
    uuid: UUIDSchema,
    stepType: z.literal("box"),
    boxStartLine: z.number(),
    boxEndLine: z.number(),
  }),
]);

// ---------- Proof Step Details ----------

export const ProofStepPositionSchema = z.object({
  nearProofStepWithUuid: UUIDSchema,
  prepend: z.boolean(),
});

export const ProofStepDetailsSchema = z.object({
  proofStep: ProofStepSchema,
  parentBoxUuid: UUIDSchema.nullable(),
  position: ProofStepPositionSchema,
});

// ---------- Rule Position ----------

export const RulePositionSchema = z.enum([
  "conclusion",
  "premise 0",
  "premise 1",
  "premise 2",
  "premise 3",
  "premise 4",
  "premise 5",
]);

// ---------- Diagnostics ----------

export const DiagnosticSchema = z.union([
  z.object({ uuid: UUIDSchema, errorType: z.literal("MissingFormula") }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("MissingRule") }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("MissingRef"), refIdx: z.number() }),

  z.object({ uuid: UUIDSchema, errorType: z.literal("PremiseInsideBox") }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("InvalidAssumption") }),
  z.object({
    uuid: UUIDSchema,
    errorType: z.literal("FreshVarEscaped"),
    boxId: z.string(),
    freshVar: z.string(),
  }),
  z.object({
    uuid: UUIDSchema,
    errorType: z.literal("RedefinitionOfFreshVar"),
    originalUuid: z.string(),
    freshVar: z.string(),
  }),

  z.object({ uuid: UUIDSchema, errorType: z.literal("ReferenceOutOfScope"), refIdx: z.number() }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("ReferenceToLaterStep"), refIdx: z.number() }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("ReferenceToUnclosedBox"), refIdx: z.number() }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("ReferenceBoxMissingFreshVar"), refIdx: z.number() }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("ReferenceShouldBeBox"), refIdx: z.number() }),
  z.object({ uuid: UUIDSchema, errorType: z.literal("ReferenceShouldBeLine"), refIdx: z.number() }),

  z.object({
    uuid: UUIDSchema,
    errorType: z.literal("WrongNumberOfReferences"),
    expected: z.number(),
    actual: z.number(),
  }),
  z.object({
    uuid: UUIDSchema,
    errorType: z.literal("ShapeMismatch"),
    rulePosition: RulePositionSchema,
    expected: z.string(),
    actual: z.string(),
  }),
  z.object({
    uuid: UUIDSchema,
    errorType: z.literal("Ambiguous"),
    subject: z.string(),
    entries: z.array(
      z.object({
        rulePosition: RulePositionSchema,
        meta: z.string(),
        actual: z.string(),
      })
    ),
  }),
  z.object({
    uuid: UUIDSchema,
    errorType: z.literal("Miscellaneous"),
    rulePosition: RulePositionSchema,
    explanation: z.string(),
  }),
]);

export const ErrorTypeSchema = z.enum([
  "MissingFormula",
  "MissingRule",
  "MissingRef",

  "PremiseInsideBox",
  "InvalidAssumption",
  "FreshVarEscaped",
  "RedefinitionOfFreshVar",

  "ReferenceOutOfScope",
  "ReferenceToLaterStep",
  "ReferenceToUnclosedBox",
  "ReferenceBoxMissingFreshVar",
  "ReferenceShouldBeBox",
  "ReferenceShouldBeLine",

  "WrongNumberOfReferences",
  "ShapeMismatch",
  "Ambiguous",
  "Miscellaneous",
]);

// ---------- Validation ----------

export const ValidationRequestSchema = z.object({
  proof: ProofSchema,
  logicName: LogicNameSchema,
});

export const ValidationResponseSchema = z.object({
  proof: ProofSchema,
  diagnostics: z.array(DiagnosticSchema),
});

// ---------- Placement ----------

export const PlacementSchema = z.enum(["before", "after"]);

// ---------- Types inferred from schemas ----------

export type Rule = z.infer<typeof RuleSchema>;
export type Ruleset = z.infer<typeof RulesetSchema>;
export type Justification = z.infer<typeof JustificationSchema>;
export type LineProofStep = z.infer<typeof LineProofStepSchema>;
export type BoxProofStep = z.infer<typeof BoxProofStepSchema>;
export type ProofStepDetails = z.infer<typeof ProofStepDetailsSchema>;
export type ProofStepPosition = z.infer<typeof ProofStepPositionSchema>;
export type RulePosition = z.infer<typeof RulePositionSchema>;
export type Diagnostic = z.infer<typeof DiagnosticSchema>;
export type ErrorType = z.infer<typeof ErrorTypeSchema>;
export type LogicName = z.infer<typeof LogicNameSchema>;
export type ValidationRequest = z.infer<typeof ValidationRequestSchema>;
export type ValidationResponse = z.infer<typeof ValidationResponseSchema>;
export type Placement = z.infer<typeof PlacementSchema>;
export type ProofWithMetadata = z.infer<typeof ProofWithMetadataSchema>;
export type TLineNumber = z.infer<typeof TLineNumberSchema>;
