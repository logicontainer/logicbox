"use client";

import "katex/dist/katex.min.css";

import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";
import { Diagnostic, TLineNumber, LineProofStep as TLineProofStep } from "@/types/types";

import AutosizeInput from "react-input-autosize";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import React from "react";
import { cn } from "@/lib/utils";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useState } from "react";

export function LineProofStep({
  ...props
}: TLineProofStep & { lines: TLineNumber[], diagnosticsForLine: Diagnostic[] }) {
  const { setLineInFocus } = useProof();
  const { interactionState, doTransition } = useInteractionState();
  const { setContextMenuPosition } = useContextMenu();
  const [tooltipContent, setTooltipContent] = useState<string>("");

  const handleOnHoverJustification = (highlightedLatex: string | null) => {
    setTooltipContent(highlightedLatex || "");
  };

  return (
    <div
      className={cn(
        "flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors items-stretch"
      )}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={(e) => {
        if (e.target !== e.currentTarget) {
          return;
        }
        return doTransition({
          enum: TransitionEnum.CLICK_LINE,
          lineUuid: props.uuid,
        });
      }}
      onContextMenuCapture={(e) => {
        e.preventDefault();
        setContextMenuPosition({ x: e.clientX, y: e.clientY });
        doTransition({
          enum: TransitionEnum.RIGHT_CLICK_STEP,
          proofStepUuid: props.uuid,
          isBox: false,
        });
      }}
    >
      <Formula
        userInput={props.formula.userInput}
        latexFormula={props.formula.latex ?? null}
        isSyncedWithServer={!props.formula.unsynced}
        lineUuid={props.uuid}
      />

      <div
        data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}
        title="Select a rule"
        className="flex items-center gap-2 whitespace-nowrap"
      >
        <Justification
          uuid={props.uuid}
          justification={props.justification}
          lines={props.lines}
          onHover={handleOnHoverJustification}
          onClickRule={() =>
            doTransition({
              enum: TransitionEnum.CLICK_RULE,
              lineUuid: props.uuid,
            })
          }
          onClickRef={(refIdx) =>
            doTransition({
              enum: TransitionEnum.CLICK_REF,
              lineUuid: props.uuid,
              refIdx,
            })
          }
        />
      </div>
    </div>
  );
}


function Formula({
  userInput,
  latexFormula,
  lineUuid,
  isSyncedWithServer
} : {
  userInput: string,
  latexFormula: string | null,
  lineUuid: string,
  isSyncedWithServer: boolean
}) {
  const { interactionState, doTransition } = useInteractionState()

  const isEditingFormula =
    interactionState.enum === InteractionStateEnum.EDITING_FORMULA &&
    interactionState.lineUuid === lineUuid

  const currentFormulaValue = isEditingFormula ? interactionState.currentFormula : userInput
  const formulaInputRef = React.useRef<HTMLInputElement>(null)

  const handleInputRefChange = (ref: HTMLInputElement | null) => {
    formulaInputRef.current = ref;
  };

  const onKeyDownAutoSizeInput = (key: string) => {
    if (isEditingFormula && key === "Enter") {
      doTransition({ enum: TransitionEnum.CLOSE });
      formulaInputRef.current?.blur();
    }
  };

  const formulaIsWrong = false
  const withUnderline = (str: string) => `{\\color{red}\\underline{${str}}}`
  const formulaContent = !latexFormula || latexFormula === "" ?  "???" : latexFormula
  const formulaLatexContentWithUnderline = formulaIsWrong ? withUnderline(formulaContent) : formulaContent

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
      className={cn("text-slate-800 grow resize shrink", formulaIsWrong && "text-red-500")}
      inputClassName="px-2"
    />
  ) : (
    <p
      className={cn("shrink", formulaIsWrong && "text-red-500 underline underline-offset-2")}
      onClick={() =>
        doTransition({
          enum: TransitionEnum.CLICK_LINE,
          lineUuid
        })
      }
    >

      {!isSyncedWithServer ? (
        currentFormulaValue
      ) : (
        <InlineMath math={formulaLatexContentWithUnderline} />
      )}
    </p>
  )
}
