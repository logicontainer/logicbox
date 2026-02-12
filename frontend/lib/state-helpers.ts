import {
    HoveringEnum,
  HoveringState,
  InteractionState,
  InteractionStateEnum,
} from "@/contexts/InteractionStateProvider";
import { ProofContextProps } from "@/contexts/ProofProvider";

export function getLineBeingEdited(state: InteractionState): string | null {
  if (
    state.enum === InteractionStateEnum.EDITING_REF ||
    state.enum === InteractionStateEnum.EDITING_RULE ||
    state.enum === InteractionStateEnum.EDITING_FORMULA
  ) {
    return state.lineUuid;
  }
  return null;
}

export function lineIsBeingEdited(
  lineUuid: string,
  state: InteractionState,
): boolean {
  return lineUuid === getLineBeingEdited(state);
}

export function freshVarIsBeingEdited(
  boxUuid: string,
  state: InteractionState
) {
  return state.enum === InteractionStateEnum.EDITING_FRESH_VAR &&
    state.boxUuid === boxUuid
}

export function getSelectedStep(state: InteractionState): string | null {
  if (
    state.enum === InteractionStateEnum.EDITING_REF ||
    state.enum === InteractionStateEnum.EDITING_RULE ||
    state.enum === InteractionStateEnum.EDITING_FORMULA
  ) {
    return state.lineUuid;
  } else if (state.enum === InteractionStateEnum.IDLE) {
    return state.selectedProofStepUuid;
  } else if (state.enum === InteractionStateEnum.VIEWING_CONTEXT_MENU) {
    return state.proofStepUuid
  } else if (state.enum === InteractionStateEnum.EDITING_FRESH_VAR) {
    return state.boxUuid
  }
  return null;
}

export function stepIsSelected(
  stepUuid: string,
  state: InteractionState,
): boolean {
  return stepUuid === getSelectedStep(state);
}

export function formulaIsBeingHovered(
  stepUuid: string,
  state: HoveringState | null,
): boolean {
  return (
    state?.enum === HoveringEnum.HOVERING_FORMULA &&
    state.stepUuid === stepUuid
  );
}

export function refIsBeingHovered(
  stepUuid: string,
  refIdx: number,
  state: HoveringState | null,
): boolean {
  return (
    state?.enum === HoveringEnum.HOVERING_REF &&
    state.stepUuid === stepUuid &&
    state.refIdx === refIdx
  );
}

export function ruleIsBeingHovered(
  stepUuid: string,
  state: HoveringState | null,
): boolean {
  return (
    state?.enum === HoveringEnum.HOVERING_RULE &&
    state.stepUuid === stepUuid
  );
}

export function refIsBeingSelected(
  stepUuid: string,
  refIdx: number,
  state: InteractionState | null
): boolean {
  return state?.enum === InteractionStateEnum.EDITING_REF &&
         state.refIdx === refIdx &&
         state.lineUuid === stepUuid
}

export function stepIsReferee(
  stepUuid: string,
  hoveringState: HoveringState | null,
  proofContext: ProofContextProps,
): boolean {
  const { getProofStepDetails } = proofContext;
  const refererStepId = hoveringState?.stepUuid || null
  const hoveredRefIdx = hoveringState?.enum === HoveringEnum.HOVERING_REF ? hoveringState.refIdx : null
  const refererStep = refererStepId ? getProofStepDetails(refererStepId) : null;
  if (hoveredRefIdx === null || refererStep?.proofStep.stepType !== "line") {
    return false;
  }

  const referredStepUuid =
    refererStep.proofStep.justification.refs[hoveredRefIdx];
  return stepUuid === referredStepUuid;
}

export function stepIsDraggable(stepUuid: string, state: InteractionState, proofContext: ProofContextProps) {
  const { isDescendant } = proofContext
  return [ 
    InteractionStateEnum.IDLE,
    InteractionStateEnum.EDITING_RULE,
    InteractionStateEnum.EDITING_FRESH_VAR,
    InteractionStateEnum.VIEWING_CONTEXT_MENU,
    InteractionStateEnum.MOVING_STEP, // TODO: really?
    // note: EDITING_REF is not here, EDITING_FORMULA also not here
  ].includes(state.enum) 
    // also okay if we are editing a formula which is not this one (or this is a parent of that)
    || (state.enum === InteractionStateEnum.EDITING_FORMULA && !isDescendant(stepUuid, state.lineUuid))
}
