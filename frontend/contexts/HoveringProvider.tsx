import React from "react";
import { TransitionEnum, useInteractionState } from "./InteractionStateProvider";

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
  const { doTransition } = useInteractionState()
  const [currentlyHoveredUuid, setCurrentlyHoveredUuid] = React.useState<string | null>(null);

  const lastHover = React.useRef<{
    uuid: string | null
    refIdx: number | null
    ruleIsHovered: boolean
  }>(null)

  const handleHoverStep = (uuid: string | null, refIdx: number | null = null, ruleIsHovered: boolean = false) => {
    setCurrentlyHoveredUuid(uuid)

    if (uuid !== lastHover.current?.uuid || refIdx !== lastHover.current?.refIdx || ruleIsHovered !== lastHover.current?.ruleIsHovered) {
      doTransition({ enum: TransitionEnum.HOVER, stepUuid: uuid, refIdx, ruleIsHovered })
      lastHover.current = { uuid: uuid, refIdx, ruleIsHovered }
    }
  }

  return (
    <HoveringContext.Provider
      value={{currentlyHoveredUuid, handleHoverStep}}
    >
      {children}
    </HoveringContext.Provider>
  );
}
