import "katex/dist/katex.min.css";

import Select, { SingleValue, Theme } from "react-select";
import { TLineNumber, LineProofStep as TLineProofStep } from "@/types/types";

import AutosizeInput, { AutosizeInputProps } from "react-input-autosize";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { RefSelect } from "./RefSelect";
import { UpdateLineProofStepCommand } from "@/lib/commands";
import { cn } from "@/lib/utils";
import { useContextMenu } from "react-contexify";
import { useHistory } from "@/contexts/HistoryProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useRuleset } from "@/contexts/RulesetProvider";
import { useState } from "react";
import { InteractionStateEnum, TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { lineIsBeingEdited } from "@/lib/state-helpers";
import React from "react";

export function LineProofStep({
  ...props
}: TLineProofStep & { lines: TLineNumber[] }) {
  const { interactionState } = useInteractionState()
  const isTheActiveEdit = lineIsBeingEdited(props.uuid, interactionState)

  return isTheActiveEdit ? (
    <LineProofStepEdit {...props} />
  ) : (
    <LineProofStepView {...props} />
  );
}

export function LineProofStepView({
  ...props
}: TLineProofStep & { lines: TLineNumber[] }) {
  const {
    setLineInFocus,
    isFocused,
  } = useProof();

  const { doTransition } = useInteractionState()

  const [tooltipContent, setTooltipContent] = useState<string>();
  const isInFocus = isFocused(props.uuid);

  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex);
  };
  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });
  function handleContextMenu(
    event:
      | React.MouseEvent<HTMLElement>
      | React.TouchEvent<HTMLElement>
      | React.KeyboardEvent<HTMLElement>
      | KeyboardEvent
  ) {
    show({
      event,
      props: {
        uuid: props.uuid,
      },
    });
  }
  return (
    <div
      className={cn(
        "flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors",
        isInFocus ? "text-blue-400" : ""
      )}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={() => doTransition({ enum: TransitionEnum.CLICK_LINE, lineUuid: props.uuid })}
      onContextMenuCapture={handleContextMenu}
    >
      <p className="shrink">
        {props.formula.unsynced ? (
          props.formula.userInput
        ) : (
          <InlineMath math={props.formula.latex || ""} />
        )}
      </p>
      <div
        data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}
      >
        <Justification
          justification={props.justification}
          lines={props.lines}
          onHover={handleOnHoverJustification}
        />
      </div>
    </div>
  );
}

interface ControlledFocusProps {
  value: string 
  onChange: (_: string) => void

  shouldFocus: boolean;
  onFocus?: () => void;
  onBlur?: () => void;

  title: string
  className?: string
  inputClassName?: string
}

export function LineProofStepEdit({
  ...props
}: TLineProofStep & { lines: TLineNumber[] }) {

  const { setLineInFocus, } = useProof();
  const { interactionState, doTransition } = useInteractionState()

  const [tooltipContent] = useState<string>("");
  const proofContext = useProof();
  const rulesetContext = useRuleset();

  const currentlyEditingRule = interactionState.enum === InteractionStateEnum.EDITING_RULE && interactionState.lineUuid === props.uuid
  const currentlyEditingFormula = interactionState.enum === InteractionStateEnum.EDITING_FORMULA && interactionState.lineUuid === props.uuid

  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });

  const currLineProofStepDetails = proofContext.getProofStepDetails(props.uuid);
  if (currLineProofStepDetails?.proofStep.stepType !== "line") {
    return null;
  }
  const currLineProofStep =
    currLineProofStepDetails.proofStep as TLineProofStep;

  const rulesetDropdownValue = rulesetContext.rulesetDropdownOptions.find(
    (option) => option.value === currLineProofStep.justification.rule
  );

  const handleChangeRule = (
    newValue: SingleValue<{ value: string; label: string }>
  ) => {
    if (newValue == null) {
      return;
    }

    doTransition({ enum: TransitionEnum.SELECT_RULE, ruleName: newValue.value })
  };

  function handleContextMenu(
    event:
      | React.MouseEvent<HTMLElement>
      | React.TouchEvent<HTMLElement>
      | React.KeyboardEvent<HTMLElement>
      | KeyboardEvent
  ) {
    show({
      event,
      props: {
        uuid: props.uuid,
      },
    });
  }

  const dropdownTheme = (theme: Theme) => ({
    ...theme,
    spacing: {
      ...theme.spacing,
      controlHeight: 30,
      baseUnit: 0,
    },
  });

  const onFocusAutoSizeInput = () => {
    if (!currentlyEditingFormula) 
      doTransition({ enum: TransitionEnum.EDIT_FORMULA })
  }

  const onBlurAutoSizeInput = () => {
    if (currentlyEditingFormula)
      doTransition({ enum: TransitionEnum.CLOSE })
  }

  return (
    <div
      className={cn(
        "flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors items-stretch border-blue-400 border-2"
      )}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={(e) => {
        if (e.target !== e.currentTarget) {
          return;
        }
        return doTransition({ enum: TransitionEnum.CLICK_LINE, lineUuid: props.uuid })
      }}
      onContextMenuCapture={handleContextMenu}
    >
      <AutosizeInput 
        value={currLineProofStep.formula.userInput}
        onChange={e => console.log(e.target.value)}

        onFocus={onFocusAutoSizeInput}
        onBlur={onBlurAutoSizeInput}

        title="Write a formula"
        className="text-slate-800 grow resize shrink"
        inputClassName="px-2"
      />
      <div
        data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}
        title="Select a rule"
        className="flex items-center gap-2 whitespace-nowrap"
      >
        <Select
          instanceId={props.uuid}
          value={rulesetDropdownValue}
          onChange={handleChangeRule}

          menuIsOpen={currentlyEditingRule}
          onMenuOpen={() => doTransition({ enum: TransitionEnum.EDIT_RULE })}
          closeMenuOnSelect={false} // ensure that CLOSE is not sent when we select something
          onMenuClose={() => {
            if (currentlyEditingRule) // i don't know why this is necessary, i think Select i buggy
              doTransition({ enum: TransitionEnum.CLOSE }) 
          }}

          options={rulesetContext.rulesetDropdownOptions}
          theme={dropdownTheme}
          styles={{
            singleValue: (base) => ({
              ...base,
              paddingLeft: "8px",
              paddingRight: "8px",
            }),
            input(base) {
              return {
                ...base,
                paddingLeft: "8px",
                paddingRight: "8px",
              };
            },
          }}
        />
        {currLineProofStep.justification.refs.length > 0 && (
          <div className="flex gap-2">
            {currLineProofStep.justification.refs.map((ref, index) => {
              return (
                <RefSelect
                  key={index}
                  value={ref}
                  onClick={() => doTransition({ enum: TransitionEnum.EDIT_REF, refIdx: index })}
                ></RefSelect>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
