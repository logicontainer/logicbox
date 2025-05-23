import React from "react";
import { TransitionEnum, useInteractionState } from "./InteractionStateProvider";

export interface HoveringContextProps {
  currentlyHoveredUuid: string | null;
  onHoverStep: (_: string | null) => void;
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

  const onHoverStep = (uuid: string | null) => {
    if (uuid !== currentlyHoveredUuid) {
      doTransition({ enum: TransitionEnum.HOVER, stepUuid: uuid })
    }
    setCurrentlyHoveredUuid(uuid)
  }

  return (
    <HoveringContext.Provider
      value={{currentlyHoveredUuid, onHoverStep}}
    >
      {children}
    </HoveringContext.Provider>
  );
}
