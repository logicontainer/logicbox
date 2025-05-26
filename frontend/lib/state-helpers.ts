import {
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
  state: InteractionState
): boolean {
  return lineUuid === getLineBeingEdited(state);
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
  }
  return null;
}

export function stepIsSelected(stepUuid: string, state: InteractionState): boolean {
  return stepUuid === getSelectedStep(state)
}

export function formulaIsBeingHovered(stepUuid: string, state: InteractionState): boolean {
  return state.enum === InteractionStateEnum.IDLE && state.selectedProofStepUuid === stepUuid && !state.sticky && state.hoveredRefIdx === null && !state.ruleIsHovered
}

export function refIsBeingHovered(stepUuid: string, refIdx: number, state: InteractionState): boolean {
  return state.enum === InteractionStateEnum.IDLE && state.selectedProofStepUuid === stepUuid && state.hoveredRefIdx === refIdx
}

export function ruleIsBeingHovered(stepUuid: string, state: InteractionState): boolean {
  return state.enum === InteractionStateEnum.IDLE && state.selectedProofStepUuid === stepUuid && state.ruleIsHovered
}

export function stepIsReferee(stepUuid: string, interactionState: InteractionState, proofContext: ProofContextProps): boolean {
  const { getProofStepDetails } = proofContext
  const refererStepId = (interactionState.enum === InteractionStateEnum.IDLE) ? interactionState.selectedProofStepUuid : null
  const hoveredRefIdx = (interactionState.enum === InteractionStateEnum.IDLE) ? interactionState.hoveredRefIdx : null
  const refererStep = refererStepId ? getProofStepDetails(refererStepId) : null
  if (hoveredRefIdx === null || refererStep?.proofStep.stepType !== "line") {
    return false
  }

  const referredStepUuid = refererStep.proofStep.justification.refs[hoveredRefIdx]
  return stepUuid === referredStepUuid
}
