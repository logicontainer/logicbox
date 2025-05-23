import { InteractionState, InteractionStateEnum } from "@/contexts/InteractionStateProvider";
import { getSelectedStep } from "./state-helpers";

export enum Highlight {
  NOTHING,
  HOVERED,
  SELECTED,
  HOVERED_AND_OTHER_IS_SELECTING_REF,
}

export function getStepHighlight(stepUuid: string, currentlyHoveredUuid: string | null, interactionState: InteractionState) {
  const currentlyBeingHovered = currentlyHoveredUuid === stepUuid
  const currentlySelected = getSelectedStep(interactionState) == stepUuid;
  const otherIsEditingRef = interactionState.enum === InteractionStateEnum.EDITING_REF
  if (currentlyBeingHovered && otherIsEditingRef) {
    return Highlight.HOVERED_AND_OTHER_IS_SELECTING_REF;
  } else if (currentlySelected) {
    return Highlight.SELECTED;
  } else if (currentlyBeingHovered) {
    return Highlight.HOVERED;
  }

  return Highlight.NOTHING
}
