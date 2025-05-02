"use client";

import {
  InteractionStateEnum,
  useInteractionState,
} from "./InteractionStateProvider";
import React, { useState } from "react";

import _ from "lodash";

export enum ContextMenuOptions {
  EDIT,
  DELETE,
  LINE_ABOVE,
  LINE_BELOW,
  BOX_ABOVE,
  BOX_BELOW,
}

export type ContextMenuPosition = {
  x: number;
  y: number;
};
export interface ContextMenuContextProps {
  contextMenuShouldBeVisible: boolean;
  contextMenuPosition: ContextMenuPosition;
  setContextMenuPosition: (position: ContextMenuPosition) => void;
}
// Context Setup
const ContextMenuContext = React.createContext<ContextMenuContextProps>({
  contextMenuShouldBeVisible: false,
  contextMenuPosition: { x: 0, y: 0 },
  setContextMenuPosition: () => {},
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
  const [contextMenuPosition, setContextMenuPosition] =
    useState<ContextMenuPosition>({
      x: 0,
      y: 0,
    });

  const { interactionState } = useInteractionState();

  const contextMenuShouldBeVisible =
    interactionState.enum === InteractionStateEnum.VIEWING_CONTEXT_MENU;

  return (
    <ContextMenuContext.Provider
      value={{
        contextMenuShouldBeVisible,
        contextMenuPosition,
        setContextMenuPosition,
      }}
    >
      {children}
    </ContextMenuContext.Provider>
  );
}
