import { HoveringState, InteractionState, InteractionStateEnum } from "@/contexts/InteractionStateProvider";
import { getSelectedStep, stepIsReferee } from "./state-helpers";
import { ProofContextProps } from "@/contexts/ProofProvider";
import { DiagnosticsContextProps } from "@/contexts/DiagnosticsProvider";
import { Diagnostic, ErrorType } from "@/types/types";

export enum StepHighlight {
  NOTHING,
  HOVERED,
  SELECTED,
  HOVERED_AND_OTHER_IS_SELECTING_REF,
  REFERRED,
}

export function getStepHighlight(stepUuid: string, interactionState: InteractionState, hoveringState: HoveringState | null, proofContext: ProofContextProps) {
  const currentlyBeingHovered = hoveringState?.stepUuid === stepUuid
  const currentlySelected = getSelectedStep(interactionState) == stepUuid;
  const refBeingEdited = interactionState.enum === InteractionStateEnum.EDITING_REF && interactionState.lineUuid === stepUuid
  const otherIsEditingRef = interactionState.enum === InteractionStateEnum.EDITING_REF && interactionState.lineUuid !== stepUuid

  if (refBeingEdited) {
    return StepHighlight.NOTHING;
  } else if (currentlyBeingHovered && otherIsEditingRef) {
    return StepHighlight.HOVERED_AND_OTHER_IS_SELECTING_REF;
  } else if (stepIsReferee(stepUuid, hoveringState, proofContext)) {
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

  const ds = diagnostics
    .filter(d => d.uuid === stepUuid)
    .filter(d => {
      switch (d.errorType) {
        case "MissingFormula": 
          return true

        case "Ambiguous": 
          return d.entries.some(e => e.rulePosition === "conclusion")

        case "ShapeMismatch": case "Miscellaneous":
          return d.rulePosition === "conclusion"
        
        default: 
          return false
      }
    })

  return ds.length > 0 ?
    DiagnosticHighlight.YES : 
    DiagnosticHighlight.NO
}

function referenceIdxIsInDiagnostic(d: Diagnostic, refIdx: number): boolean {
  switch (d.errorType) {
    case "MissingRef": case "ReferenceOutOfScope": case "ReferenceToLaterStep":
    case "ReferenceToUnclosedBox": case "ReferenceBoxMissingFreshVar": case "ReferenceShouldBeBox":
    case "ReferenceShouldBeLine":
      return refIdx === d.refIdx

    case "Ambiguous":
      return d.entries.some(e => e.rulePosition === `premise ${refIdx}`)

    default: 
      return false
  }
}

export function getDiagnosticHighlightForReference(stepUuid: string, refIdx: number, diagnosticContext: DiagnosticsContextProps) {
  const { diagnostics } = diagnosticContext

  const ds = diagnostics
    .filter(d => d.uuid === stepUuid)
    .filter(d => referenceIdxIsInDiagnostic(d, refIdx))

  return ds.length > 0 ?
    DiagnosticHighlight.YES : 
    DiagnosticHighlight.NO
}

export function getDiagnosticHighlightForRule(stepUuid: string, diagnosticContext: DiagnosticsContextProps) {
  const { diagnostics } = diagnosticContext

  const ruleViolationTypes: ErrorType[] = [
    "MissingRule"
  ] as const
  
  const ds = diagnostics
    .filter(d => d.uuid === stepUuid)
    .filter(d => ruleViolationTypes.includes(d.errorType))

  return ds.length > 0 ?
    DiagnosticHighlight.YES : 
    DiagnosticHighlight.NO
}
