import React from "react";
import {
    HoveringState,
  TransitionEnum,
  useInteractionState,
} from "./InteractionStateProvider";
import _ from "lodash";

export interface HoveringContextProps {
  hoveringState: HoveringState | null;
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
  const [currentState, setCurrentState] = React.useState<HoveringState | null>(null);
  const lastState = React.useRef<HoveringState | null>(null);

  const handleHover = (hovering: HoveringState | null) => {
    if (!_.isEqual(hovering, lastState.current)) {
      doTransition({
        enum: TransitionEnum.HOVER,
        hovering: hovering
      });
      setCurrentState(hovering);
      lastState.current = hovering;
    }
  };

  return (
    <HoveringContext.Provider value={{ hoveringState: currentState, handleHover }}>
      {children}
    </HoveringContext.Provider>
  );
}
