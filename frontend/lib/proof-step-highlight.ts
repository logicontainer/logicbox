import { InteractionState, InteractionStateEnum } from "@/contexts/InteractionStateProvider";
import { getSelectedStep, stepIsReferee } from "./state-helpers";
import { ProofContextProps } from "@/contexts/ProofProvider";
import { DiagnosticsContextProps } from "@/contexts/DiagnosticsProvider";
import { Violation, ViolationType } from "@/types/types";

export enum StepHighlight {
  NOTHING,
  HOVERED,
  SELECTED,
  HOVERED_AND_OTHER_IS_SELECTING_REF,
  REFERRED,
}

export function getStepHighlight(stepUuid: string, currentlyHoveredUuid: string | null, interactionState: InteractionState, proofContext: ProofContextProps) {
  const currentlyBeingHovered = currentlyHoveredUuid === stepUuid
  const currentlySelected = getSelectedStep(interactionState) == stepUuid;
  const refBeingEdited = interactionState.enum === InteractionStateEnum.EDITING_REF && interactionState.lineUuid === stepUuid
  const otherIsEditingRef = interactionState.enum === InteractionStateEnum.EDITING_REF && interactionState.lineUuid !== stepUuid

  if (refBeingEdited) {
    return StepHighlight.NOTHING;
  } else if (currentlyBeingHovered && otherIsEditingRef) {
    return StepHighlight.HOVERED_AND_OTHER_IS_SELECTING_REF;
  } else if (stepIsReferee(stepUuid, interactionState, proofContext)) {
    return StepHighlight.REFERRED;
  } else if (currentlySelected) {
    return StepHighlight.SELECTED;
  } else if (currentlyBeingHovered) {
    return StepHighlight.HOVERED;
  }

  return StepHighlight.NOTHING
}

export enum DiagnosticHighlight {
  NO,
  YES,
}

export function getDiagnosticHighlightForFormula(stepUuid: string, diagnosticContext: DiagnosticsContextProps) {
  const { diagnostics } = diagnosticContext

  const formulaViolationTypes: string[] = [
    "missingFormula",
    "formulaDoesntMatchRule",
    "formulaDoesntMatchReference",
    "miscellaneousViolation",
  ] satisfies ViolationType[]

  const ds = diagnostics
    .filter(d => d.uuid === stepUuid)
    .filter(d => formulaViolationTypes.includes(d.violationType))

  return ds.length > 0 ?
    DiagnosticHighlight.YES : 
    DiagnosticHighlight.NO
}

export function getDiagnosticHighlightForReference(stepUuid: string, refIdx: number, diagnosticContext: DiagnosticsContextProps) {
  const { diagnostics } = diagnosticContext

  const referenceViolationTypes: ViolationType[] = [
    "wrongNumberOfReferences",
    "referenceShouldBeBox",
    "referenceShouldBeLine",
    "referenceDoesntMatchRule",
    "referencesMismatch",
    "referenceIdNotFound",
    "referenceToLaterStep",
    "referenceToUnclosedBox",
    "malformedReference",
    "formulaDoesntMatchReference",
    "missingDetailInReference",
  ] as const

  const ds = diagnostics
    .filter(d => d.uuid === stepUuid)
    .filter(d => referenceViolationTypes.includes(d.violationType))
    .filter(d => {
      const v = (d as { refIdx: number })
      if (!v)
        return false;
      return d.violation.refIdx === refIdx
    })

  console.log(diagnostics)

  return ds.length > 0 ?
    DiagnosticHighlight.YES : 
    DiagnosticHighlight.NO
}

export function getDiagnosticHighlightForRule(stepUuid: string, diagnosticContext: DiagnosticsContextProps) {
  const { diagnostics } = diagnosticContext

  const ruleViolationTypes: ViolationType[] = [
    "missingRule"
  ] as const
  
  const ds = diagnostics
    .filter(d => d.uuid === stepUuid)
    .filter(d => ruleViolationTypes.includes(d.violationType))

  return ds.length > 0 ?
    DiagnosticHighlight.YES : 
    DiagnosticHighlight.NO
}
