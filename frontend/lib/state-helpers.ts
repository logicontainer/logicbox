import { InteractionState, InteractionStateEnum } from "@/contexts/InteractionStateProvider"

export function getLineBeingEdited(state: InteractionState): string | null {
  if (
    state.enum === InteractionStateEnum.EDITING_LINE ||
    state.enum === InteractionStateEnum.EDITING_REF ||
    state.enum === InteractionStateEnum.EDITING_RULE ||
    state.enum === InteractionStateEnum.EDITING_FORMULA
  ) {
    return state.lineUuid
  }
  return null
}

export function lineIsBeingEdited(lineUuid: string, state: InteractionState): boolean {
  return lineUuid === getLineBeingEdited(state)
}
