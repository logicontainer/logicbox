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
import LineNumber from "./LineNumber";
import { ProofStepWrapper } from "./ProofStepWrapper";
import React from "react";
import { cn } from "@/lib/utils";
import {
  getDiagnosticHighlightForFormula,
  getStepHighlight,
  DiagnosticHighlight,
} from "@/lib/proof-step-highlight";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";

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
  const { currentlyHoveredUuid, handleHoverStep } = useHovering();
  const proofContext = useProof();

  const parentRef = React.useRef<HTMLDivElement>(null);
  const lineNumberRef = React.useRef<HTMLDivElement>(null);
  React.useEffect(() => {
    // Extract height of the parent element
    if (parentRef.current) {
      const parentHeight = parentRef.current.offsetHeight;
      // Set the height of the line number element
      if (lineNumberRef.current) {
        lineNumberRef.current.style.height = `${parentHeight}px`;
      }
    }
  }, [parentRef, lineNumberRef]);

  const line = props.lines.find(
    (l) => l.uuid === props.uuid && l.stepType == "line",
  );
  if (line?.stepType !== "line") {
    console.error(
      `LineProofStep: Expected line with uuid ${props.uuid} to be of type 'line', but found ${line?.stepType}`,
    );
    return;
  }
  const stepHighlight = getStepHighlight(
    props.uuid,
    currentlyHoveredUuid,
    interactionState,
    proofContext,
  );

  return (
    <ProofStepWrapper
      highlight={stepHighlight}
      isOuterProofStep={props.isOuterProofStep}
    >
      <div
        ref={parentRef}
        key={props.uuid}
        className={cn(
          "text-nowrap pointer-events-auto",
          "flex justify-between gap-8 text-lg/10 text-slate-800 px-1 pointer transition-colors items-stretch",
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
          if (e.currentTarget !== e.target) return;
          handleHoverStep(props.uuid, null, false);
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
        <div ref={lineNumberRef} className="w-16 left-0 absolute">
          <LineNumber line={line} />
        </div>
        <Formula
          latexFormula={props.formula.latex ?? null}
          isSyncedWithServer={!props.formula.unsynced}
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
}: {
  userInput: string;
  latexFormula: string | null;
  lineUuid: string;
  isSyncedWithServer: boolean;
}) {
  const { interactionState, doTransition } = useInteractionState();
  const { handleHoverStep } = useHovering();
  const diagnosticContext = useDiagnostics();

  const isEditingFormula =
    interactionState.enum === InteractionStateEnum.EDITING_FORMULA &&
    interactionState.lineUuid === lineUuid;

  const currentFormulaValue = isEditingFormula
    ? interactionState.currentFormula
    : userInput;

  const formulaInputRef = React.useRef<HTMLInputElement>(null);

  const formulaDsHighlight = getDiagnosticHighlightForFormula(
    lineUuid,
    diagnosticContext,
  );

  const handleInputRefChange = (ref: HTMLInputElement | null) => {
    formulaInputRef.current = ref;
  };

  const onKeyDownAutoSizeInput = (key: string) => {
    if (isEditingFormula && key === "Enter") {
      doTransition({ enum: TransitionEnum.CLOSE });
      formulaInputRef.current?.blur();
    }
  };

  const errorHighlight = (str: string) => `\\mathbf{\\underline{${str}}}`;
  const formulaContent =
    !latexFormula || latexFormula === "" ? "???" : latexFormula;
  const formulaIsWrong = formulaDsHighlight === DiagnosticHighlight.YES;
  const formulaLatexContentWithUnderline = formulaIsWrong
    ? errorHighlight(formulaContent)
    : formulaContent;

  return isEditingFormula ? (
    <AutosizeInput
      inputRef={handleInputRefChange}
      value={currentFormulaValue}
      onClickCapture={(e) => e.stopPropagation()}
      onDoubleClickCapture={(e) => e.stopPropagation()}
      onMouseOver={(e) => {
        e.stopPropagation();
        handleHoverStep(lineUuid, null, false);
      }}
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
        formulaIsWrong && "text-red-500",
      )}
      inputClassName="px-2"
    />
  ) : (
    <p
      className={cn("shrink", formulaIsWrong && "text-red-500")}
      onMouseOver={(e) => {
        e.stopPropagation();
        handleHoverStep(lineUuid, null, false);
      }}
    >
      {!isSyncedWithServer || !latexFormula || latexFormula === "" ? (
        currentFormulaValue
      ) : (
        <span
          onClickCapture={(e) => {
            e.stopPropagation();
          }}
        >
          <InlineMath math={formulaLatexContentWithUnderline}></InlineMath>
        </span>
      )}
    </p>
  );
}
