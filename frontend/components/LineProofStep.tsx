"use client";

import "katex/dist/katex.min.css";

import {
  Diagnostic,
  TLineNumber,
  LineProofStep as TLineProofStep,
} from "@/types/types";
import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import AutosizeInput from "react-input-autosize";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { ProofStepWrapper } from "./ProofStepWrapper";
import React from "react";
import { cn } from "@/lib/utils";
import { formulaIsWrong } from "@/lib/diagnostic-helpers";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { getStepHighlight } from "@/lib/proof-step-highlight";

export function LineProofStep({
  ...props
}: TLineProofStep & {
  lines: TLineNumber[];
  diagnosticsForLine: Diagnostic[];
  isOuterProofStep?: boolean;
}) {
  const { doTransition } = useInteractionState();
  const { setContextMenuPosition } = useContextMenu();
  const { interactionState } = useInteractionState();
  const { currentlyHoveredUuid, onHoverStep } = useHovering()

  const highlight = getStepHighlight(props.uuid, currentlyHoveredUuid, interactionState)

  return (
    <ProofStepWrapper
      highlight={highlight}
      isOuterProofStep={props.isOuterProofStep}
    >
      <div
        key={props.uuid}
        className={cn(
          "text-nowrap pointer-events-auto",
          "flex relative justify-between gap-8 text-lg/10 text-slate-800 px-1 pointer transition-colors items-stretch",
        )}
        onClick={(e) => {
          e.stopPropagation();
          return doTransition({
            enum: TransitionEnum.CLICK_LINE,
            lineUuid: props.uuid,
          });
        }}
        onDoubleClick={(e) => {
          e.stopPropagation();
          return doTransition({
            enum: TransitionEnum.DOUBLE_CLICK_LINE,
            lineUuid: props.uuid,
          });
        }}
        onMouseMove={(e) => {
          e.stopPropagation();
          onHoverStep(props.uuid)
        }}
        onContextMenu={(e) => {
          e.preventDefault();
          e.stopPropagation();
          setContextMenuPosition({ x: e.pageX, y: e.pageY });
          doTransition({
            enum: TransitionEnum.RIGHT_CLICK_STEP,
            proofStepUuid: props.uuid,
            isBox: false,
          });
        }}
      >
        <Formula
          latexFormula={props.formula.latex ?? null}
          isSyncedWithServer={!props.formula.unsynced}
          formulaIsWrong={formulaIsWrong(props.diagnosticsForLine)}
          userInput={props.formula.userInput}
          lineUuid={props.uuid}
        />

        <div
          title="Select a rule"
          className="flex items-center gap-2 whitespace-nowrap"
        >
          <Justification
            uuid={props.uuid}
            justification={props.justification}
            lines={props.lines}
            onClickRule={() => {
              doTransition({
                enum: TransitionEnum.CLICK_RULE,
                lineUuid: props.uuid,
              });
            }}
            onClickRef={(refIdx) => {
              doTransition({
                enum: TransitionEnum.CLICK_REF,
                lineUuid: props.uuid,
                refIdx,
              });
            }}
          />
        </div>
      </div>
    </ProofStepWrapper>
  );
}

function Formula({
  userInput,
  latexFormula,
  lineUuid,
  isSyncedWithServer,
  formulaIsWrong,
}: {
  userInput: string;
  latexFormula: string | null;
  lineUuid: string;
  isSyncedWithServer: boolean;
  formulaIsWrong: boolean;
}) {
  const { interactionState, doTransition } = useInteractionState();

  const isEditingFormula =
    interactionState.enum === InteractionStateEnum.EDITING_FORMULA &&
    interactionState.lineUuid === lineUuid;

  const currentFormulaValue = isEditingFormula
    ? interactionState.currentFormula
    : userInput;
  const formulaInputRef = React.useRef<HTMLInputElement>(null);

  const handleInputRefChange = (ref: HTMLInputElement | null) => {
    formulaInputRef.current = ref;
  };

  const onKeyDownAutoSizeInput = (key: string) => {
    if (isEditingFormula && key === "Enter") {
      doTransition({ enum: TransitionEnum.CLOSE });
      formulaInputRef.current?.blur();
    }
  };

  const withUnderline = (str: string) => `{\\color{red}\\underline{${str}}}`;
  const formulaContent =
    !latexFormula || latexFormula === "" ? "???" : latexFormula;
  const formulaLatexContentWithUnderline = formulaIsWrong
    ? withUnderline(formulaContent)
    : formulaContent;

  return isEditingFormula ? (
    <AutosizeInput
      inputRef={handleInputRefChange}
      value={currentFormulaValue}
      onChange={(e) => {
        console.log(e);
        doTransition({
          enum: TransitionEnum.UPDATE_FORMULA,
          formula: e.target.value,
        });
      }}
      autoFocus={isEditingFormula}
      onKeyDown={(e) => onKeyDownAutoSizeInput(e.key)}
      title="Write a formula"
      className={cn(
        "text-slate-800 grow resize shrink",
        formulaIsWrong && "text-red-500"
      )}
      inputClassName="px-2"
    />
  ) : (
    <p
      className={cn(
        "shrink",
        formulaIsWrong && "text-red-500 underline underline-offset-2"
      )}
      onClickCapture={e => e.preventDefault()}
    >
      {!isSyncedWithServer || formulaIsWrong ? (
        currentFormulaValue
      ) : (
        <InlineMath 
          math={formulaLatexContentWithUnderline}
        ></InlineMath>
      )}
    </p>
  );
}
