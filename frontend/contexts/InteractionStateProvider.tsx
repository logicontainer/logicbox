"use client";

import { BoxProofStep, LineProofStep } from "@/types/types";
import React from "react";

import { useProof } from "./ProofProvider";
import {
  AddBoxedLineCommand,
  AddLineCommand,
  Command,
  RemoveProofStepCommand,
  SetFreshVarOnBoxCommand,
  UpdateLineProofStepCommand,
} from "@/lib/commands";
import { useHistory } from "./HistoryProvider";
import { useRuleset } from "./RulesetProvider";
import { useServer } from "./ServerProvider";
import { ContextMenuOptions } from "./ContextMenuProvider";
import { v4 as uuidv4 } from "uuid";

export enum TransitionEnum {
  CLICK_OUTSIDE,
  CLICK_BOX,
  RIGHT_CLICK_STEP,

  DOUBLE_CLICK_BOX,

  CLICK_LINE,
  DOUBLE_CLICK_LINE,
  HOVER,

  CLICK_RULE,
  CLICK_REF,

  UPDATE_RULE,
  UPDATE_CONTENT,

  VALIDATE_PROOF,

  CLICK_CONTEXT_MENU_OPTION,

  CLOSE,
}

export enum InteractionStateEnum {
  IDLE,

  EDITING_REF,
  EDITING_FORMULA,
  EDITING_RULE,
  EDITING_FRESH_VAR,

  VIEWING_CONTEXT_MENU,
}

export enum HoveringEnum {
  HOVERING_STEP,
  HOVERING_FORMULA,
  HOVERING_REF,
  HOVERING_RULE,
}

export type HoveringState = { enum: HoveringEnum } & (
  | { enum: HoveringEnum.HOVERING_STEP, stepUuid: string }
  | { enum: HoveringEnum.HOVERING_FORMULA, stepUuid: string }
  | { enum: HoveringEnum.HOVERING_RULE, stepUuid: string }
  | { enum: HoveringEnum.HOVERING_REF, stepUuid: string, refIdx: number}
)

export type InteractionState = { enum: InteractionStateEnum } & (
  | {
      enum: InteractionStateEnum.IDLE;
      selectedProofStepUuid: string | null;
      sticky: boolean;
    }
  | { enum: InteractionStateEnum.EDITING_RULE; lineUuid: string }
  | {
      enum: InteractionStateEnum.EDITING_FORMULA;
      lineUuid: string;
      currentFormula: string;
    }
  | { enum: InteractionStateEnum.EDITING_REF; lineUuid: string; refIdx: number }
  | { enum: InteractionStateEnum.EDITING_FRESH_VAR; boxUuid: string; freshVar: string | null; }
  | {
      enum: InteractionStateEnum.VIEWING_CONTEXT_MENU;
      proofStepUuid: string;
      isBox: boolean;
    }
);

export type Transition = { enum: TransitionEnum } & (
  | { enum: TransitionEnum.CLICK_LINE; lineUuid: string }
  | { enum: TransitionEnum.DOUBLE_CLICK_LINE; lineUuid: string }
  | {
      enum: TransitionEnum.HOVER;
      hovering: HoveringState | null; // null if we are hovering nothing
    }
  | { enum: TransitionEnum.CLICK_BOX; boxUuid: string }
  | {
      enum: TransitionEnum.RIGHT_CLICK_STEP;
      proofStepUuid: string;
      isBox: boolean;
    }
  | { enum: TransitionEnum.CLICK_OUTSIDE }
  | { enum: TransitionEnum.CLOSE }
  | { enum: TransitionEnum.CLICK_RULE; lineUuid: string }
  | { enum: TransitionEnum.CLICK_REF; lineUuid: string; refIdx: number }
  | { enum: TransitionEnum.VALIDATE_PROOF }
  | { enum: TransitionEnum.UPDATE_RULE; ruleName: string }
  | { enum: TransitionEnum.UPDATE_CONTENT; content: string }
  | {
      enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION;
      option: ContextMenuOptions;
    }
  | { enum: TransitionEnum.DOUBLE_CLICK_BOX; boxUuid: string }
);

type FuncForStateAndTransition<
  S extends InteractionStateEnum,
  T extends TransitionEnum,
> = (
  state: InteractionState & { enum: S },
  t: Transition & { enum: T },
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
  React.createContext<InteractionStateContextProps | null>(null);

export function useInteractionState() {
  const context = React.useContext(InteractionStateContext);
  if (!context) {
    throw new Error(
      "useInteractionState must be used within a InteractionStateProvider",
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
      selectedProofStepUuid: null,
      sticky: false,
    });

  const serverContext = useServer();
  const proofContext = useProof();
  const { rulesets } = useRuleset();
  const historyContext = useHistory();

  // keep command queue of state modifying things which must be executed fully in order
  enum Validate {
    VALIDATE,
  }
  const commandQueue = React.useRef<(Validate | Command)[]>([]);
  const enqueueCommand = (f: Validate | Command) =>
    commandQueue.current.push(f);
  const [flushTrigger, setFlushTrigger] = React.useState(0);

  React.useEffect(() => {
    if (commandQueue.current.length === 0) return;

    // execute a single command
    const cmd = commandQueue.current.shift()!;
    if (cmd === Validate.VALIDATE) {
      serverContext.validateProof(proofContext.proof);
    } else {
      historyContext.addToHistory(cmd);
    }

    if (commandQueue.current.length > 0) {
      // update the flush trigger, so after a full state rerender,
      //  we get a new command
      setTimeout(() => setFlushTrigger((c) => c + 1), 0);
    }
  }, [flushTrigger]);

  const getLineProofStep = (uuid: string) => {
    const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);
    if (!currLineProofStepDetails)
      throw new Error(
        `Updating ref, but the line we are editing ${uuid} doesn't have step details`,
      );

    if (currLineProofStepDetails.proofStep.stepType !== "line")
      throw new Error(
        `Updating ref, but the line we are editing doesn't have stepType 'line', has ${currLineProofStepDetails.proofStep.stepType}`,
      );

    const currLineProofStep =
      currLineProofStepDetails.proofStep as LineProofStep;
    return currLineProofStep;
  };

  const getBoxProofStep = (uuid: string) => {
    const details = proofContext.getProofStepDetails(uuid);
    if (!details)
      throw new Error(
        `Attempting to get box proof step ${uuid}, but details are null`,
      );

    if (details.proofStep.stepType !== "box")
      throw new Error(
        `Attempting to get box proof step ${uuid}, but has stepType = ${details.proofStep.stepType}`,
      );

    const currBoxProofStep = details.proofStep as BoxProofStep;
    return currBoxProofStep;
  };

  const updateRuleAndValidate = (lineUuid: string, newRule: string) => {
    const currLineProofStep = getLineProofStep(lineUuid);

    const allRules = rulesets.flatMap((s) => s.rules);
    const ruleSetEntry = allRules.find((r) => r.ruleName === newRule);
    if (ruleSetEntry === undefined)
      throw new Error(
        `Updating rule, but could not find rule with name ${newRule} in rulesets ${rulesets}`,
      );

    const newNumPremises = ruleSetEntry.numPremises;

    let newRefs = currLineProofStep.justification.refs;
    if (newNumPremises > newRefs.length) {
      newRefs = newRefs.concat(
        Array(newNumPremises - newRefs.length).fill("?"),
      );
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

    enqueueCommand(
      new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep),
    );
    enqueueCommand(Validate.VALIDATE);
  };

  const updateRefAndValidate = (
    lineUuid: string,
    refIdx: number,
    newRefUuid: string,
  ) => {
    const currLineProofStep = getLineProofStep(lineUuid);

    const newRefs = currLineProofStep.justification.refs.map((ref, i) => {
      if (i === refIdx) {
        return newRefUuid;
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

    enqueueCommand(
      new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep),
    );
    enqueueCommand(Validate.VALIDATE);
  };

  const updateFormulaInProofAndValidate = (
    lineUuid: string,
    formula: string,
  ) => {
    const currLineProofStep = getLineProofStep(lineUuid);

    // if nothing change, don't add to history
    if (currLineProofStep.formula.userInput === formula) return;

    const updatedLineProofStep: LineProofStep = {
      ...currLineProofStep,
      formula: {
        userInput: formula,
        unsynced: true,
        latex: null,
        ascii: null,
      },
    };

    enqueueCommand(
      new UpdateLineProofStepCommand(lineUuid, updatedLineProofStep),
    );
    enqueueCommand(Validate.VALIDATE);
  };

  const updateFreshVarInProofAndValidate = (
    boxUuid: string,
    freshVar: string | null,
  ) => {
    enqueueCommand(
      new SetFreshVarOnBoxCommand(boxUuid, freshVar),
    );
    enqueueCommand(Validate.VALIDATE);
  };

  const startEditingFormula = (lineUuid: string) => {
    const line = getLineProofStep(lineUuid);
    return {
      enum: InteractionStateEnum.EDITING_FORMULA,
      lineUuid,
      currentFormula: line.formula.userInput,
    } satisfies InteractionState;
  };

  const startEditingFreshVar = (boxUuid: string) => {
    const box = getBoxProofStep(boxUuid);
    return {
      enum: InteractionStateEnum.EDITING_FRESH_VAR,
      boxUuid,
      freshVar: box.boxInfo.freshVar ?? ""
    } satisfies InteractionState;
  };

  const fullyIdle = (): InteractionState & {
    enum: InteractionStateEnum.IDLE;
  } => {
    return {
      enum: InteractionStateEnum.IDLE,
      selectedProofStepUuid: null,
      sticky: false,
    };
  };

  const handleClickStepInIdle = (
    state: InteractionState & { enum: InteractionStateEnum.IDLE },
    clickedUuid: string,
  ): InteractionState => {
    if (!state.sticky) {
      return {
        ...fullyIdle(),
        selectedProofStepUuid: clickedUuid,
        sticky: true,
      };
    }

    return {
      ...fullyIdle(),
      selectedProofStepUuid: clickedUuid,
      sticky: clickedUuid !== state.selectedProofStepUuid,
    };
  };

  const stickySelectStep = (step: string) => {
    return {
      ...fullyIdle(),
      selectedProofStepUuid: step,
      sticky: true,
    }
  }

  const {
    IDLE,
    EDITING_REF,
    EDITING_RULE,
    EDITING_FRESH_VAR,
    EDITING_FORMULA,
    VIEWING_CONTEXT_MENU,
  } = InteractionStateEnum;
  const {
    CLICK_REF,
    CLICK_RULE,
    CLICK_LINE,
    DOUBLE_CLICK_LINE,
    DOUBLE_CLICK_BOX,
    HOVER,
    CLICK_BOX,
    RIGHT_CLICK_STEP,
    UPDATE_RULE,
    CLOSE,
    UPDATE_CONTENT,
    CLICK_OUTSIDE,
    VALIDATE_PROOF,
    CLICK_CONTEXT_MENU_OPTION,
  } = TransitionEnum;

  const doNothing = (s: any) => s;

  const behavior: Behavior = {
    [IDLE]: {
      [CLICK_OUTSIDE]: fullyIdle,

      [CLICK_LINE]: (state, { lineUuid }) =>
        handleClickStepInIdle(state, lineUuid),

      [DOUBLE_CLICK_LINE]: (_, { lineUuid }) => startEditingFormula(lineUuid),
      [HOVER]: (state, { hovering }) => {
        return {
          ...state,
          selectedProofStepUuid: state.sticky ? state.selectedProofStepUuid : (hovering?.stepUuid ?? null),
          hovering: hovering,
        }
      },
      [CLICK_RULE]: (_, { lineUuid }) => ({ enum: EDITING_RULE, lineUuid }),
      [CLICK_REF]: (_, { lineUuid, refIdx }) => ({
        enum: EDITING_REF,
        lineUuid,
        refIdx,
      }),

      [CLICK_BOX]: (state, { boxUuid }) =>
        handleClickStepInIdle(state, boxUuid),

      [RIGHT_CLICK_STEP]: (_, { proofStepUuid, isBox }) => {
        return { enum: VIEWING_CONTEXT_MENU, proofStepUuid, isBox };
      },

      [VALIDATE_PROOF]: (state, _) => {
        enqueueCommand(Validate.VALIDATE);
        return state;
      },

      [DOUBLE_CLICK_BOX]: (_, { boxUuid }) => {
        return startEditingFreshVar(boxUuid)
      }
    },

    [EDITING_FORMULA]: {
      [CLICK_OUTSIDE]: (state, _) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return stickySelectStep(state.lineUuid)
      },

      [CLICK_LINE]: (_, { lineUuid }) => stickySelectStep(lineUuid),
      [HOVER]: doNothing,

      [DOUBLE_CLICK_LINE]: (state, { lineUuid: clickedLineUuid }) => {
        // we are exiting this state, so update formula in proof
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);

        // go to editing mode on the clicked line
        return startEditingFormula(clickedLineUuid);
      },

      [CLICK_RULE]: (state, { lineUuid: clickedLineUuid }) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return { enum: EDITING_RULE, lineUuid: clickedLineUuid };
      },

      [CLICK_REF]: (state, { lineUuid: clickedLineUuid, refIdx }) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return { enum: EDITING_REF, lineUuid: clickedLineUuid, refIdx };
      },

      [CLICK_BOX]: (state, { boxUuid }) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return stickySelectStep(boxUuid)
      },

      [UPDATE_CONTENT]: (state, { content: formula }) => {
        return { ...state, currentFormula: formula };
      },

      [VALIDATE_PROOF]: (state, _) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return stickySelectStep(state.lineUuid);
      },

      [CLOSE]: (state, _) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return stickySelectStep(state.lineUuid);
      },

      [RIGHT_CLICK_STEP]: (state, { proofStepUuid, isBox }) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula);
        return { enum: VIEWING_CONTEXT_MENU, proofStepUuid, isBox };
      },

      [DOUBLE_CLICK_BOX]: (state, { boxUuid }) => {
        updateFormulaInProofAndValidate(state.lineUuid, state.currentFormula)
        return startEditingFreshVar(boxUuid)
      }
    },

    [EDITING_RULE]: {
      [CLICK_OUTSIDE]: () => fullyIdle(),

      [CLICK_LINE]: (state, { lineUuid }) => {
        if (state.lineUuid === lineUuid) {
          return stickySelectStep(state.lineUuid);
        } else {
          return fullyIdle();
        }
      },

      [DOUBLE_CLICK_LINE]: (_, { lineUuid }) => startEditingFormula(lineUuid),
      [HOVER]: doNothing,

      [CLICK_RULE]: (_, { lineUuid }) => ({ enum: EDITING_RULE, lineUuid }),
      [CLICK_REF]: (_, { lineUuid, refIdx }) => ({
        enum: EDITING_REF,
        lineUuid,
        refIdx,
      }),

      [CLICK_BOX]: (_, { boxUuid }) => stickySelectStep(boxUuid),

      [UPDATE_RULE]: ({ lineUuid }, { ruleName }) => {
        updateRuleAndValidate(lineUuid, ruleName);
        return stickySelectStep(lineUuid);
      },

      [VALIDATE_PROOF]: (state, _) => {
        enqueueCommand(Validate.VALIDATE);
        return stickySelectStep(state.lineUuid);
      },

      [CLOSE]: () => fullyIdle(),

      [RIGHT_CLICK_STEP]: (_, { proofStepUuid, isBox }) => {
        return { enum: VIEWING_CONTEXT_MENU, proofStepUuid, isBox };
      },

      [DOUBLE_CLICK_BOX]: (_, { boxUuid }) => {
        return startEditingFreshVar(boxUuid)
      }
    },

    [EDITING_REF]: {
      [CLICK_OUTSIDE]: () => fullyIdle(),

      [CLICK_LINE]: (
        { refIdx, lineUuid: editedLineUuid },
        { lineUuid: clickedLineUuid },
      ) => {
        if (editedLineUuid !== clickedLineUuid) {
          updateRefAndValidate(editedLineUuid, refIdx, clickedLineUuid);
        }
        return stickySelectStep(editedLineUuid);
      },

      [HOVER]: doNothing,
      [DOUBLE_CLICK_LINE]: doNothing,

      [CLICK_BOX]: (
        { refIdx, lineUuid: editedLineUuid },
        { boxUuid: clickedBoxUuid },
      ) => {
        updateRefAndValidate(editedLineUuid, refIdx, clickedBoxUuid);
        return stickySelectStep(editedLineUuid)
      },

      [CLICK_RULE]: (
        { refIdx, lineUuid: editedLineUuid },
        { lineUuid: clickedLineUuid },
      ) => {
        if (editedLineUuid !== clickedLineUuid) {
          updateRefAndValidate(editedLineUuid, refIdx, clickedLineUuid);
          return stickySelectStep(editedLineUuid);
        }
        return { enum: EDITING_RULE, lineUuid: clickedLineUuid };
      },

      [CLICK_REF]: (state, trans) => {
        if (trans.lineUuid !== state.lineUuid) {
          updateRefAndValidate(state.lineUuid, state.refIdx, trans.lineUuid);
          return stickySelectStep(state.lineUuid);
        }

        if (trans.refIdx === state.refIdx) {
          // unselect
          return fullyIdle();
        }

        return {
          enum: EDITING_REF,
          lineUuid: state.lineUuid,
          refIdx: trans.refIdx,
        };
      },

      [VALIDATE_PROOF]: () => {
        enqueueCommand(Validate.VALIDATE);
        return fullyIdle();
      },

      [CLOSE]: () => fullyIdle(),

      [RIGHT_CLICK_STEP]: (_, { proofStepUuid, isBox }) => {
        return { enum: VIEWING_CONTEXT_MENU, proofStepUuid, isBox };
      },

      [DOUBLE_CLICK_BOX]: ({ lineUuid: editedLineUuid, refIdx }, { boxUuid }) => {
        updateRefAndValidate(editedLineUuid, refIdx, boxUuid);
        return stickySelectStep(editedLineUuid)
      },
    },
    
    [EDITING_FRESH_VAR]: {
      [HOVER]: doNothing,
      [CLICK_OUTSIDE]: (_, {}) => fullyIdle(),
      [CLICK_BOX]: (_, { boxUuid }) => stickySelectStep(boxUuid),
      [RIGHT_CLICK_STEP]: (_, { proofStepUuid, isBox }) => ({ enum: VIEWING_CONTEXT_MENU, proofStepUuid, isBox }),

      [DOUBLE_CLICK_BOX]: (state, { boxUuid }) => {
        if (state.boxUuid !== boxUuid) {
          updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
          return startEditingFreshVar(boxUuid)
        }
        return state
      },

      [CLICK_LINE]: (state, { lineUuid }) => {
        updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
        return stickySelectStep(lineUuid)
      },

      [DOUBLE_CLICK_LINE]: (state, { lineUuid }) => {
        updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
        return startEditingFormula(lineUuid)
      },

      [CLICK_RULE]: (state, { lineUuid }) => {
        updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
        return { enum: EDITING_RULE, lineUuid }
      },

      [CLICK_REF]: (state, { lineUuid, refIdx }) => {
        updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
        return { enum: EDITING_REF, lineUuid, refIdx }
      },


      [UPDATE_CONTENT]: (state, { content }) => {
        return { 
          ...state,
          freshVar: content === "" ? null : content
        }
      },

      [VALIDATE_PROOF]: (state, {}) => {
        updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
        return fullyIdle()
      },

      [CLOSE]: (state, _) => {
        updateFreshVarInProofAndValidate(state.boxUuid, state.freshVar)
        return stickySelectStep(state.boxUuid)
      },
    },

    [VIEWING_CONTEXT_MENU]: {
      [CLICK_OUTSIDE]: () => fullyIdle(),

      [DOUBLE_CLICK_LINE]: (_, { lineUuid }) => {
        return startEditingFormula(lineUuid);
      },

      [HOVER]: doNothing,

      [CLICK_LINE]: (_, { lineUuid }) => ({
        ...fullyIdle(),
        selectedProofStepUuid: lineUuid,
      }),
      [CLICK_BOX]: (_, { boxUuid }) => ({
        ...fullyIdle(),
        selectedProofStepUuid: boxUuid,
      }),

      [CLICK_RULE]: (_, { lineUuid }) => {
        return { enum: EDITING_RULE, lineUuid };
      },

      [CLICK_REF]: (_, { lineUuid, refIdx }) => ({
        enum: EDITING_REF,
        lineUuid,
        refIdx,
      }),

      [RIGHT_CLICK_STEP]: (_, { proofStepUuid, isBox }) => {
        return { enum: VIEWING_CONTEXT_MENU, proofStepUuid, isBox };
      },

      [VALIDATE_PROOF]: () => {
        enqueueCommand(Validate.VALIDATE);
        return fullyIdle();
      },

      [CLOSE]: () => fullyIdle(),

      [DOUBLE_CLICK_BOX]: (_, { boxUuid }) => {
        return startEditingFreshVar(boxUuid)
      },

      [CLICK_CONTEXT_MENU_OPTION]: (state, { option }) => {
        switch (option) {
          case ContextMenuOptions.EDIT_FORMULA:
            if (state.isBox) {
              throw new Error(
                "Editing a box is not implemented yet. Why are you here?",
              );
            }

            const line = getLineProofStep(state.proofStepUuid);

            return {
              enum: EDITING_FORMULA,
              lineUuid: state.proofStepUuid,
              currentFormula: line.formula.userInput,
            };

          case ContextMenuOptions.EDIT_FRESH_VAR:
            if (!state.isBox) {
              throw new Error("Editing fresh var of line. Why are you here?");
            }

            let fv = prompt("Enter fresh var");
            if (fv === "") fv = null;
            fv &&
              enqueueCommand(
                new SetFreshVarOnBoxCommand(state.proofStepUuid, fv),
              );
            fv && enqueueCommand(Validate.VALIDATE);

            return fullyIdle();

          case ContextMenuOptions.DELETE:
            enqueueCommand(new RemoveProofStepCommand(state.proofStepUuid));
            enqueueCommand(Validate.VALIDATE);
            return fullyIdle();

          case ContextMenuOptions.LINE_ABOVE:
          case ContextMenuOptions.LINE_BELOW: {
            const newLineUuid = uuidv4();
            enqueueCommand(
              new AddLineCommand(
                state.proofStepUuid,
                option === ContextMenuOptions.LINE_ABOVE,
                newLineUuid,
              ),
            );
            enqueueCommand(Validate.VALIDATE);
            return {
              enum: EDITING_FORMULA,
              lineUuid: newLineUuid,
              currentFormula: "",
            };
          }

          case ContextMenuOptions.BOX_ABOVE:
          case ContextMenuOptions.BOX_BELOW: {
            const newLineUuid = uuidv4();
            enqueueCommand(
              new AddBoxedLineCommand(
                state.proofStepUuid,
                option === ContextMenuOptions.BOX_ABOVE,
                newLineUuid,
              ),
            );
            enqueueCommand(Validate.VALIDATE);
            return {
              enum: EDITING_FORMULA,
              lineUuid: newLineUuid,
              currentFormula: "",
            };
          }
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
          `No transition function found for state ${
            InteractionStateEnum[prevState.enum]
          } and transition ${TransitionEnum[transition.enum]}`,
          prevState,
          transition,
        );
        return prevState;
      } else {
        const newState = func(prevState, transition);
        // console.log(
        //   `${TransitionEnum[transition.enum]}: ${
        //     InteractionStateEnum[prevState.enum]
        //   } -> ${InteractionStateEnum[newState.enum]}`
        // );

        if (JSON.stringify(prevState) === JSON.stringify(newState)) {
          return prevState;
        } else {
          return newState;
        }
      }
    });

    setFlushTrigger((t) => t + 1);
  };

  return (
    <InteractionStateContext.Provider
      value={{ interactionState, doTransition }}
    >
      {children}
    </InteractionStateContext.Provider>
  );
}
