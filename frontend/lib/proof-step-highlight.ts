import { InteractionState, InteractionStateEnum } from "@/contexts/InteractionStateProvider";
import { getSelectedStep, stepIsReferee } from "./state-helpers";
import { ProofContextProps } from "@/contexts/ProofProvider";

export enum Highlight {
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
    return Highlight.NOTHING;
  } else if (currentlyBeingHovered && otherIsEditingRef) {
    return Highlight.HOVERED_AND_OTHER_IS_SELECTING_REF;
  } else if (stepIsReferee(stepUuid, interactionState, proofContext)) {
    return Highlight.REFERRED;
  } else if (currentlySelected) {
    return Highlight.SELECTED;
  } else if (currentlyBeingHovered) {
    return Highlight.HOVERED;
  }

  return Highlight.NOTHING
}
