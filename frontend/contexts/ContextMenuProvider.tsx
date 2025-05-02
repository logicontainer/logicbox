"use client";

import {
  BoxProofStep,
  LineProofStep,
  Proof,
  ProofStep,
  ProofStepDetails,
  ProofStepPosition,
  TLineNumber,
} from "@/types/types";
import {
  InteractionStateEnum,
  useInteractionState,
} from "./InteractionStateProvider";
import React, { useEffect, useState } from "react";

import { ProofStepContextMenu } from "@/components/ProofStepContextMenu";
import _ from "lodash";
import { useProof } from "./ProofProvider";
import { useContextMenu as useReactContexifyMenu } from "react-contexify";

export interface ContextMenuContextProps {
  setContextMenuDOMEvent: (
    event:
      | React.MouseEvent<HTMLElement>
      | React.TouchEvent<HTMLElement>
      | React.KeyboardEvent<HTMLElement>
      | KeyboardEvent
  ) => void;
}
// Context Setup
const ContextMenuContext = React.createContext<ContextMenuContextProps>({
  setContextMenuDOMEvent: () => {},
});

export function useContextMenu() {
  const context = React.useContext(ContextMenuContext);
  if (!context) {
    throw new Error("useContextMenu must be used within a ContextMenuProvider");
  }
  return context;
}

export function ContextMenuProvider({
  children,
}: React.PropsWithChildren<object>) {
  const [contextMenuDOMEvent, setContextMenuDOMEvent] = useState<
    | React.MouseEvent<HTMLElement>
    | React.TouchEvent<HTMLElement>
    | React.KeyboardEvent<HTMLElement>
    | KeyboardEvent
    | null
  >(null);

  const { interactionState } = useInteractionState();

  const { show, hideAll } = useReactContexifyMenu({
    id: "proof-step-context-menu",
  });

  const [trackedContextMenuVisibility, setTrackedContextMenuVisibility] =
    useState(false); // Should not be updated as reaction to state change. Should just track the state of the context menu

  useEffect(() => {
    const contextMenuShouldBeVisible =
      interactionState.enum === InteractionStateEnum.VIEWING_CONTEXT_MENU;
    if (contextMenuShouldBeVisible && trackedContextMenuVisibility === false) {
      console.log("balls");
      if (contextMenuDOMEvent === null) {
        throw new Error("contextMenuDOMEvent is null");
      }
      queueMicrotask(() => {
        show({
          event: contextMenuDOMEvent,
          props: {
            uuid: interactionState.proofStepUuid,
          },
        });
      });
    }

    if (!contextMenuShouldBeVisible && trackedContextMenuVisibility === true) {
      hideAll();
      setContextMenuDOMEvent(null);
    }
  }, [interactionState, trackedContextMenuVisibility]);

  return (
    <ContextMenuContext.Provider
      value={{
        setContextMenuDOMEvent,
      }}
    >
      <ProofStepContextMenu
        onVisibilityChange={setTrackedContextMenuVisibility}
      />
      {children}
    </ContextMenuContext.Provider>
  );
}
