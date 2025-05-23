import React from "react";
import { InteractionStateEnum, TransitionEnum, useInteractionState } from "./InteractionStateProvider";

export interface HoveringContextProps {
  currentlyHoveredUuid: string | null;
  handleHoverStep: (uuid: string | null, refIdx: number | null, ruleIsHovered: boolean) => void;
}

// Context Setup
const HoveringContext = React.createContext<HoveringContextProps | null>(null);

export function useHovering() {
  const context = React.useContext(HoveringContext);
  if (!context) {
    throw new Error("useHovering must be used within a HoveringProvider");
  }
  return context;
}

export function HoveringProvider({ children }: React.PropsWithChildren<object>) {
  const { interactionState, doTransition } = useInteractionState()
  const [currentlyHoveredUuid, setCurrentlyHoveredUuid] = React.useState<string | null>(null);

  const handleHoverStep = (uuid: string | null, refIdx: number | null = null, ruleIsHovered: boolean = false) => {
    const currentRefIdx = interactionState.enum === InteractionStateEnum.IDLE && interactionState.hoveredRefIdx
    const currentRuleIsHovered = interactionState.enum === InteractionStateEnum.IDLE && interactionState.ruleIsHovered

    if (uuid !== currentlyHoveredUuid || refIdx !== currentRefIdx || ruleIsHovered !== currentRuleIsHovered) {
      doTransition({ enum: TransitionEnum.HOVER, stepUuid: uuid, refIdx, ruleIsHovered })
    }

    setCurrentlyHoveredUuid(uuid)
  }

  return (
    <HoveringContext.Provider
      value={{currentlyHoveredUuid, handleHoverStep}}
    >
      {children}
    </HoveringContext.Provider>
  );
}
