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
import { useServer } from "./ServerProvider";

export enum TransitionEnum {
  CLICK_LINE,
  // CLICK_BOX,
  CLICK_OUTSIDE,
  // RIGHT_CLICK_LINE,
  // RIGHT_CLICK_BOX,
  
  CLICK_RULE,
  CLICK_REF,
  CLICK_FORMULA,

  SELECT_RULE,
  UPDATE_FORMULA,

  VALIDATE_PROOF,

  CLOSE,
}

export enum InteractionStateEnum {
  IDLE,

  EDITING_REF,
  EDITING_FORMULA,
  EDITING_RULE,

  // CONTEXT_MENU,
}

export type InteractionState = { enum: InteractionStateEnum } & (
  | { enum: InteractionStateEnum.IDLE }
  | { enum: InteractionStateEnum.EDITING_RULE; lineUuid: string; }
  | { enum: InteractionStateEnum.EDITING_FORMULA; lineUuid: string; currentFormula: string }
  | { enum: InteractionStateEnum.EDITING_REF; lineUuid: string; refIdx: number }
);

export type Transition = { enum: TransitionEnum } & (
  | { enum: TransitionEnum.CLICK_LINE; lineUuid: string }
  | { enum: TransitionEnum.CLICK_OUTSIDE }
  | { enum: TransitionEnum.CLOSE }
  | { enum: TransitionEnum.CLICK_RULE; lineUuid: string }
  | { enum: TransitionEnum.CLICK_FORMULA; lineUuid: string  }
  | { enum: TransitionEnum.CLICK_REF; lineUuid: string; refIdx: number }
  | { enum: TransitionEnum.VALIDATE_PROOF }
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

  const serverContext = useServer()
  const proofContext = useProof()
  const { ruleset } = useRuleset()
  const historyContext = useHistory()
  
  // keep command queue of state modifying things which must be executed fully in order
  enum Validate { VALIDATE }
  const commandQueue = React.useRef<(Validate | Command)[]>([]);
  const enqueueCommand = (f: Validate | Command) => commandQueue.current.push(f)
  const [flushTrigger, setFlushTrigger] = React.useState(0)

  React.useEffect(() => {
    if (commandQueue.current.length === 0) 
      return;

    // execute a single command
    const cmd = commandQueue.current.shift()!
    if (cmd === Validate.VALIDATE) {
      serverContext.validateProof(proofContext.proof)
    } else {
      historyContext.addToHistory(cmd)
    }

    if (commandQueue.current.length > 0) {
      // update the flush trigger, so after a full state rerender, 
      //  we get a new command
      setTimeout(() => setFlushTrigger(c => c + 1), 0)
    }
  }, [flushTrigger])


  const getLineProofStep = (uuid: string) => {
    const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);
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

    enqueueCommand(new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep))
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

    enqueueCommand(new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep))
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

    enqueueCommand(new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep))
  }

  const startEditingFormula = (lineUuid: string) => {
    const line = getLineProofStep(lineUuid)
    return { enum: InteractionStateEnum.EDITING_FORMULA, lineUuid, currentFormula: line.formula.userInput } satisfies InteractionState
  }

  const { IDLE, EDITING_REF, EDITING_RULE, EDITING_FORMULA } = InteractionStateEnum
  const { CLICK_REF, CLICK_RULE, CLICK_FORMULA, CLICK_LINE, SELECT_RULE, CLOSE, UPDATE_FORMULA, CLICK_OUTSIDE, VALIDATE_PROOF } = TransitionEnum

  const behavior: Behavior = {
    [IDLE]: {
      [CLICK_LINE]: (_, { lineUuid }) => {
        return startEditingFormula(lineUuid)
      },
      [VALIDATE_PROOF]: (state, _) => {
        enqueueCommand(Validate.VALIDATE)
        return state
      },
      [CLICK_RULE]: (_, { lineUuid }) => ({ enum: EDITING_RULE, lineUuid }),
      [CLICK_REF]: (_, { lineUuid, refIdx }) => ({ enum: EDITING_REF, lineUuid, refIdx }),
      [CLICK_FORMULA]: (_, { lineUuid }) => {
        // obtain the contents of the formula
        const { formula: { userInput } } = getLineProofStep(lineUuid)
        return ({ enum: EDITING_FORMULA, lineUuid, currentFormula: userInput })
      },
    },

    [EDITING_FORMULA]: {
      [UPDATE_FORMULA]: (state, { formula }) => {
        return { ...state, currentFormula: formula };
      },
      [CLOSE]: (state, _) => {
        updateFormula(state.lineUuid, state.currentFormula)
        return { enum: IDLE }
      },
      [CLICK_LINE]: (state, { lineUuid: clickedLineUuid }) => {
        // we are exiting this state, so update formula in proof
        updateFormula(state.lineUuid, state.currentFormula)

        // go to editing mode on the clicked line
        return startEditingFormula(clickedLineUuid)
      },
      [CLICK_OUTSIDE]: (state, _) => {
        updateFormula(state.lineUuid, state.currentFormula);
        return { enum: IDLE }
      },
      [VALIDATE_PROOF]: (state, _) => {
        updateFormula(state.lineUuid, state.currentFormula)
        enqueueCommand(Validate.VALIDATE)
        return { enum: IDLE }
      }
    },

    [EDITING_REF]: {
      [CLICK_REF]: (state, trans) => {
        if (trans.refIdx === state.refIdx) {
          // unselect
          return { enum: IDLE }
        }

        return { enum: EDITING_REF, lineUuid: state.lineUuid, refIdx: trans.refIdx }
      },
      [CLICK_LINE]: ({ refIdx, lineUuid: editedLineUuid }, { lineUuid: clickedLineUuid }) => {
        updateRef(editedLineUuid, refIdx, clickedLineUuid)
        return { enum: IDLE }
      },
    },

    [EDITING_RULE]: {
      [SELECT_RULE]: ({ lineUuid }, { ruleName }) => {
        updateRule(lineUuid, ruleName)
        return { enum: IDLE }
      },
      [CLOSE]: () => ({ enum: IDLE })
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

    setFlushTrigger(t => t + 1)
  };

  return (
    <InteractionStateContext.Provider
      value={{ interactionState, doTransition }}
    >
      {children}
    </InteractionStateContext.Provider>
  );
}
