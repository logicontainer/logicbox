"use client";

import React from "react";
import { useHovering } from "./HoveringProvider";
import { HoveringEnum, TransitionEnum, useInteractionState } from "./InteractionStateProvider";

export type DragInfo = {
  stepUuid: string,
  isOnLowerHalf: boolean,
}

export interface StepDragContext {
  handleDragStart: (stepUuid: string) => void;
  handleDragOver: (dragInfo: DragInfo | null) => void;
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
  const currentlyDragged = React.useRef<string | null>(null)

  const { handleHover } = useHovering()
  const { doTransition } = useInteractionState()

  const handleDragStart = (stepUuid: string) => {
    if (currentlyDragged.current !== stepUuid) {
      currentlyDragged.current = stepUuid
      doTransition({
        enum: TransitionEnum.START_DRAG_STEP,
        stepUuid
      })
    }
  }

  const handleDragOver = (dragInfo: DragInfo | null) => {
    handleHover(dragInfo && {
      enum: HoveringEnum.HOVERING_STEP,
      stepUuid: dragInfo.stepUuid,
      aboveOrBelow: dragInfo.isOnLowerHalf ? "below" : "above"
    } || null)
  }

  const handleDragStop = () => {
    if (currentlyDragged.current !== null) {
      currentlyDragged.current = null
      doTransition({ enum: TransitionEnum.STOP_DRAG_STEP })
    }
  }

  return <StepDragContext.Provider value={{ handleDragStart, handleDragOver, handleDragStop }}>
    {children}
  </StepDragContext.Provider>
}
