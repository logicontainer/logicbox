"use client";

import React from "react";
import { useHovering } from "./HoveringProvider";
import { HoveringEnum, TransitionEnum, useInteractionState } from "./InteractionStateProvider";

export interface StepDragContext {
  handleDragStart: (stepUuid: string) => void;
  handleDragOver: (stepUuid: string, isOnLowerHalf: boolean) => void;
  handleDragStop: () => void;
}

// Context Setup
const StepDragContext = React.createContext<StepDragContext | null>(null);

export function useStepDrag() {
  const context = React.useContext(StepDragContext);
  if (!context) {
    throw new Error("useStepDrag must be used within a StepDragProvider");
  }
  return context;
}

export function StepDragProvider({ children }: React.PropsWithChildren<object>) {
  const [currentlyDragged, setCurrentlyDragged] = React.useState<string | null>(null)

  const { handleHover } = useHovering()
  const { doTransition } = useInteractionState()

  const handleDragStart = (stepUuid: string) => {
    if (currentlyDragged !== stepUuid) {
      setCurrentlyDragged(stepUuid)
      doTransition({
        enum: TransitionEnum.START_DRAG_STEP,
        stepUuid
      })
    }
  }

  const handleDragOver = (stepUuid: string, isOnLowerHalf: boolean) => {
    handleHover({
      enum: HoveringEnum.HOVERING_STEP,
      stepUuid,
      aboveOrBelow: isOnLowerHalf ? "below" : "above"
    })
  }

  const handleDragStop = () => {
    if (currentlyDragged !== null) {
      setCurrentlyDragged(null)
      doTransition({ enum: TransitionEnum.STOP_DRAG_STEP })
    }
  }

  return <StepDragContext.Provider value={{ handleDragStart, handleDragOver, handleDragStop }}>
    {children}
  </StepDragContext.Provider>
}
