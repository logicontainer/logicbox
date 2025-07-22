import React from "react";
import {
    HoveringState,
  TransitionEnum,
  useInteractionState,
} from "./InteractionStateProvider";
import _ from "lodash";

export interface HoveringContextProps {
  handleHover: (_: HoveringState | null) => void;
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

export function HoveringProvider({
  children,
}: React.PropsWithChildren<object>) {
  const { doTransition } = useInteractionState();
  const [currentlyHoveredUuid, setCurrentlyHoveredUuid] = React.useState<
    string | null
  >(null);

  const lastHover = React.useRef<HoveringState | null>(null);

  const handleHover = (hovering: HoveringState | null) => {
    setCurrentlyHoveredUuid(hovering?.stepUuid ?? null);

    if (!_.isEqual(hovering, lastHover.current)) {
      doTransition({
        enum: TransitionEnum.HOVER,
        hovering: hovering
      });
      lastHover.current = hovering;
    }
  };

  return (
    <HoveringContext.Provider value={{ handleHover }}>
      {children}
    </HoveringContext.Provider>
  );
}
