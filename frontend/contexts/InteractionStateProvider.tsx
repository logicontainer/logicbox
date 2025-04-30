"use client";

import {
  LineProofStep,
} from "@/types/types";
import React from "react";

import { useProof } from "./ProofProvider";
import { Command, UpdateLineProofStepCommand } from "@/lib/commands";
import { useHistory } from "./HistoryProvider";
import { getLineBeingEdited } from "@/lib/state-helpers";
import { useRuleset } from "./RulesetProvider";

export enum TransitionEnum {
  CLICK_LINE,
  // CLICK_BOX,
  CLICK_OUTSIDE,
  // RIGHT_CLICK_LINE,
  // RIGHT_CLICK_BOX,
  
  EDIT_RULE,
  EDIT_REF,
  EDIT_FORMULA,

  SELECT_RULE,
  UPDATE_FORMULA,

  CLOSE,
}

export enum InteractionStateEnum {
  IDLE,

  EDITING_LINE,
  EDITING_REF,
  EDITING_FORMULA,
  EDITING_RULE,

  // CONTEXT_MENU,
}

export type InteractionState = { enum: InteractionStateEnum } & (
  | { enum: InteractionStateEnum.IDLE }
  | { enum: InteractionStateEnum.EDITING_LINE; lineUuid: string }
  | { enum: InteractionStateEnum.EDITING_RULE; lineUuid: string; }
  | { enum: InteractionStateEnum.EDITING_FORMULA; lineUuid: string; }
  | { enum: InteractionStateEnum.EDITING_REF; lineUuid: string; refIdx: number }
);

export type Transition = { enum: TransitionEnum } & (
  | { enum: TransitionEnum.CLICK_LINE; lineUuid: string }
  | { enum: TransitionEnum.CLICK_OUTSIDE }
  | { enum: TransitionEnum.CLOSE }
  | { enum: TransitionEnum.EDIT_RULE }
  | { enum: TransitionEnum.EDIT_FORMULA }
  | { enum: TransitionEnum.EDIT_REF; refIdx: number }
  | { enum: TransitionEnum.SELECT_RULE; ruleName: string }
  | { enum: TransitionEnum.UPDATE_FORMULA; formula: string }
);

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

function defineBehaviorForStates<S extends InteractionStateEnum>(
  states: S[],
  transitions: {
    [T in TransitionEnum]?: FuncForStateAndTransition<S, T>;
  },
): Partial<Record<S, typeof transitions>> {
  return states.reduce((acc, state) => {
    acc[state] = transitions;
    return acc;
  }, {} as Partial<Record<S, typeof transitions>>);
}

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
    React.useState<InteractionState>({
      enum: InteractionStateEnum.IDLE,
    });

  const { getProofStepDetails } = useProof()
  const { ruleset } = useRuleset()
  const historyContext = useHistory()

  const commandQueue = React.useRef<Command[]>([]);
  const [transitionCount, setTransitionCount] = React.useState(0) // to trigger command queue emptying
  const executeCommand = (cmd: Command) => commandQueue.current.push(cmd)

  // when transition count changes, flush the command queue
  React.useEffect(() => {
    if (commandQueue.current.length > 0) {
      commandQueue.current.forEach(cmd => historyContext.addToHistory(cmd))
      commandQueue.current = []
    }
  }, [transitionCount, historyContext])

  const getLineProofStep = (uuid: string) => {
    const currLineProofStepDetails = getProofStepDetails(uuid);
    if (!currLineProofStepDetails) 
      throw new Error(`Updating ref, but the line we are editing ${uuid} doesn't have step details`);

    if (currLineProofStepDetails.proofStep.stepType !== 'line')
      throw new Error(`Updating ref, but the line we are editing doesn't have stepType 'line', has ${currLineProofStepDetails.proofStep.stepType}`)

    const currLineProofStep = currLineProofStepDetails.proofStep as LineProofStep
    return currLineProofStep
  }
  
  const updateRule = (lineUuid: string, newRule: string) => {
    const currLineProofStep = getLineProofStep(lineUuid)

    const ruleSetEntry = ruleset.rules.find(r => r.ruleName === newRule)
    if (ruleSetEntry === undefined)
      throw new Error(`Updating rule, but could not find rule with name ${newRule} in ruleset ${ruleset}`)

    const newNumPremises = ruleSetEntry.numPremises;

    let newRefs = currLineProofStep.justification.refs;
    if (newNumPremises > newRefs.length) {
      newRefs = newRefs.concat(Array(newNumPremises - newRefs.length).fill("?"));
    } else {
      newRefs = newRefs.slice(0, newNumPremises);
    }

    const updatedLineProofStep: LineProofStep = {
      ...currLineProofStep,
      justification: {
        rule: newRule, 
        refs: newRefs,
      },
    };

    executeCommand(new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep))
  }

  const updateRef = (lineUuid: string, refIdx: number, newRefUuid: string) => {
    const currLineProofStep = getLineProofStep(lineUuid)

    const newRefs = currLineProofStep.justification.refs.map((ref, i) => {
      if (i === refIdx) {
        return newRefUuid
      } else {
        return ref;
      }
    });

    const updatedLineProofStep = {
      ...currLineProofStep,
      justification: {
        ...currLineProofStep.justification,
        refs: newRefs,
      },
    };

    executeCommand(new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep))
  }

  const updateFormula = (lineUuid: string, formula: string) => {
    const currLineProofStep = getLineProofStep(lineUuid)
    const updatedLineProofStep: LineProofStep = {
      ...currLineProofStep,
      formula: {
        userInput: formula,
        unsynced: true,
      },
    };

    executeCommand(new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep))
  }

  const { EDITING_LINE, IDLE, EDITING_REF, EDITING_RULE, EDITING_FORMULA } = InteractionStateEnum
  const { EDIT_REF, EDIT_RULE, EDIT_FORMULA, CLICK_LINE, SELECT_RULE, CLOSE, UPDATE_FORMULA } = TransitionEnum

  const behavior: Behavior = {
    [IDLE]: {
      [CLICK_LINE]: (_, trans) => ({ enum: EDITING_LINE, lineUuid: trans.lineUuid })
    },

    [EDITING_LINE]: {
      [CLICK_LINE]: (state, trans) => {
        if (trans.lineUuid == state.lineUuid)
          return state;

        return {
          enum: InteractionStateEnum.EDITING_LINE,
          lineUuid: trans.lineUuid,
        };
      },
      [UPDATE_FORMULA]: (state, { formula }) => {
        updateFormula(state.lineUuid, formula)
        return state;
      },
      [EDIT_RULE]: (state, _) => ({ enum: EDITING_RULE, lineUuid: state.lineUuid }),
      [EDIT_REF]: (state, { refIdx }) => ({ enum: EDITING_REF, lineUuid: state.lineUuid, refIdx }),
      [EDIT_FORMULA]: (state, _) => ({ enum: EDITING_FORMULA, lineUuid: state.lineUuid })
    },

    [EDITING_REF]: {
      [EDIT_REF]: (state, trans) => ({ enum: EDITING_REF, lineUuid: state.lineUuid, refIdx: trans.refIdx }),
      [CLICK_LINE]: ({ refIdx, lineUuid: editedLineUuid }, { lineUuid: clickedLineUuid }) => {
        updateRef(editedLineUuid, refIdx, clickedLineUuid)
        return { enum: EDITING_LINE, lineUuid: editedLineUuid }
      },
    },

    [EDITING_RULE]: {
      [SELECT_RULE]: ({ lineUuid }, { ruleName }) => {
        updateRule(lineUuid, ruleName)
        return { enum: EDITING_LINE, lineUuid }
      },
      [CLOSE]: ({lineUuid}, _) => ({ enum: EDITING_LINE, lineUuid })
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
          `No transition function found for state ${InteractionStateEnum[prevState.enum]} and transition ${TransitionEnum[transition.enum]}`,
          prevState,
          transition
        );
        return prevState;
      } else {
        const newState = func(prevState, transition);
        console.log(`${TransitionEnum[transition.enum]}: ${InteractionStateEnum[prevState.enum]} -> ${InteractionStateEnum[newState.enum]}`)
        return newState
      }
    })

    // trigger flushing of command queue
    setTransitionCount(c => c + 1) 
  };

  return (
    <InteractionStateContext.Provider
      value={{ interactionState, doTransition }}
    >
      {children}
    </InteractionStateContext.Provider>
  );
}
