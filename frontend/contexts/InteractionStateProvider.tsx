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
import React, { useEffect, useState } from "react";

import _ from "lodash";
import { parseLinesFromProof } from "@/lib/lines-parser";
import { useProof } from "./ProofProvider";
import { useServer } from "./ServerProvider";

export enum TransitionEnum {
  CLICK_LINE,
  // CLICK_BOX,
  // CLICK_OUTSIDE,
  // RIGHT_CLICK_LINE,
  // RIGHT_CLICK_BOX,
  EDIT_REF,
  // EDIT_FORMULA,
  // EDIT_RULE,
  // UPDATE_RULE,
  // UPDATE_FORMULA,
}

enum InteractionStateEnum {
  IDLE,
  EDITING_LINE,
  SELECTING_REF,
  // CONTEXT_MENU,
  // EDITING_FORMULA,
  // EDITING_RULE,
}

type InteractionState = { enum: InteractionStateEnum } & (
  | { enum: InteractionStateEnum.IDLE }
  | { enum: InteractionStateEnum.EDITING_LINE; lineUuid: string }
  | {
      enum: InteractionStateEnum.SELECTING_REF;
    }
);

type Transition = { enum: TransitionEnum } & (
  | { enum: TransitionEnum.CLICK_LINE; lineUuid: string }
  | { enum: TransitionEnum.EDIT_REF; lineUuid: string }
);
// | { enum: TransitionEnum.CLICK_BOX; boxUuid: string }
// | { enum: TransitionEnum.CLICK_OUTSIDE }
// | { enum: TransitionEnum.RIGHT_CLICK_LINE; lineUuid: string }
// | { enum: TransitionEnum.RIGHT_CLICK_BOX; boxUuid: string }
// | { enum: TransitionEnum.EDIT_FORMULA; lineUuid: string }
// | { enum: TransitionEnum.EDIT_RULE; lineUuid: string }
// | { enum: TransitionEnum.UPDATE_RULE; lineUuid: string }
// | { enum: TransitionEnum.UPDATE_FORMULA; lineUuid: string }

type FuncForStateAndTransition<
  S extends InteractionStateEnum,
  T extends TransitionEnum
> = (
  state: InteractionState & { enum: S },
  t: Transition & { enum: T }
) => InteractionState;

type Behavior = {
  [S in InteractionStateEnum]?: {
    [T in TransitionEnum]?: FuncForStateAndTransition<S, T>;
  };
};

export interface InteractionStateContextProps {
  interactionState: InteractionState;
  doTransition: (trans: Transition) => void;
}
// Context Setup
const InteractionStateContext =
  React.createContext<InteractionStateContextProps>({
    interactionState: { enum: InteractionStateEnum.IDLE },
    doTransition: () => {},
  });

export function useInteractionState() {
  const context = React.useContext(InteractionStateContext);
  if (!context) {
    throw new Error(
      "useInteractionState must be used within a InteractionStateProvider"
    );
  }
  return context;
}

export function InteractionStateProvider({
  children,
}: React.PropsWithChildren<object>) {
  const [interactionState, setInteractionStateValue] =
    useState<InteractionState>({
      enum: InteractionStateEnum.IDLE,
    });

  const behavior: Behavior = {
    [InteractionStateEnum.EDITING_LINE]: {
      [TransitionEnum.CLICK_LINE]: (state, trans) => {
        if (trans.lineUuid == state.lineUuid) {
          return state;
        } else {
          return {
            enum: InteractionStateEnum.EDITING_LINE,
            lineUuid: trans.lineUuid,
          };
        }
      },
    },
  };

  const doTransition = (transition: Transition) => {
    setInteractionStateValue((prevState) => {
      const func = behavior[prevState.enum]?.[transition.enum] as
        | FuncForStateAndTransition<
            typeof prevState.enum,
            typeof transition.enum
          >
        | undefined;
      if (!func) {
        console.warn(
          `No transition function found for state ${prevState.enum} and transition ${transition.enum}`,
          prevState,
          transition
        );
        return prevState;
      } else {
        return func(prevState, transition);
      }
    });
  };

  return (
    <InteractionStateContext.Provider
      value={{ interactionState, doTransition }}
    >
      {children}
    </InteractionStateContext.Provider>
  );
}
