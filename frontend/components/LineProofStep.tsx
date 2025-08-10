"use client";

import "katex/dist/katex.min.css";

import {
  TLineNumber,
  LineProofStep as TLineProofStep,
} from "@/types/types";
import {
  HoveringEnum,
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import AutosizeInput from "react-input-autosize";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import LineNumber from "./LineNumber";
import { ProofStepWrapper } from "./ProofStepWrapper";
import React, { TouchEvent } from "react";
import { cn, isOnLowerHalf } from "@/lib/utils";
import {
  getDiagnosticHighlightForFormula,
  getStepHighlight,
  DiagnosticHighlight,
} from "@/lib/proof-step-highlight";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { formulaIsBeingHovered, stepIsDraggable } from "@/lib/state-helpers";
import { useStepDrag } from "@/contexts/StepDragProvider";

import { isMobile } from 'react-device-detect'

export function LineProofStep({
  ...props
}: TLineProofStep & {
  lines: TLineNumber[];
  isOuterProofStep?: boolean;
}) {
  const { doTransition } = useInteractionState();
  const { setContextMenuPosition } = useContextMenu();
  const { interactionState } = useInteractionState();
  const { handleHover, hoveringState } = useHovering();
  const proofContext = useProof();
  const { handleDragOver, handleDragStop, handleDragStart } = useStepDrag()

  const touchTimeout = React.useRef<NodeJS.Timeout | null>(null)
  const touchPosition = React.useRef<{ x: number, y: number, target: HTMLElement } | null>(null)

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
    interactionState,
    hoveringState,
    proofContext,
  );


  const TOUCH_RIGHT_CLICK_MS = 400

  const isWithinBounds = (pos: { x: number, y: number, target: HTMLElement }): boolean => {
    const { x, y } = pos
    const rect = pos.target.getBoundingClientRect()
    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
  }

  const extractPosition = (e: TouchEvent<HTMLDivElement>): { x: number, y: number, target: HTMLElement } | null => {
    if (e.changedTouches.length <= 0) return null
    const { clientX, clientY } = e.changedTouches.item(e.changedTouches.length - 1)
    return { x: clientX, y: clientY, target: e.currentTarget }
  }

  const handleTouchStart = (e: TouchEvent<HTMLDivElement>) => {
    e.stopPropagation()
    touchPosition.current = extractPosition(e)
    touchTimeout.current = setTimeout(() => {
      console.log(touchPosition.current)
      if (touchPosition.current && isWithinBounds(touchPosition.current)) {
        setContextMenuPosition(touchPosition.current)
        doTransition({ enum: TransitionEnum.RIGHT_CLICK_STEP, proofStepUuid: props.uuid, isBox: false })
        touchTimeout.current = null
        touchPosition.current = null
      }
    }, TOUCH_RIGHT_CLICK_MS)
  }

  const handleTouchMove = (e: TouchEvent<HTMLDivElement>) => {
    e.stopPropagation()
    touchPosition.current = extractPosition(e)
  }

  const handleTouchEnd = (e: TouchEvent<HTMLDivElement>) => {
    e.stopPropagation()

    const pos = extractPosition(e)
    if (pos && isWithinBounds(pos) && touchTimeout.current != null) {
      doTransition({ enum: TransitionEnum.CLICK_BOX, boxUuid: props.uuid })
    }

    touchPosition.current = null
    if (touchTimeout.current) 
      clearTimeout(touchTimeout.current)
  }

  const dropZoneDirection: 'above' | 'below' | null =
    interactionState.enum === InteractionStateEnum.MOVING_STEP && interactionState.toUuid === props.uuid ? interactionState.direction : null

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
          dropZoneDirection === "above" && "border-t-[4px] border-black",
          dropZoneDirection === "below" && "border-b-[4px] border-black",
        )}

        onTouchStart={isMobile ? handleTouchStart : undefined}
        onTouchMove={isMobile ? handleTouchMove : undefined}
        onTouchEnd={isMobile ? handleTouchEnd : undefined}

        draggable={!isMobile && stepIsDraggable(props.uuid, interactionState)}
        onDragStart={isMobile ? undefined : (_ => handleDragStart(props.uuid))}
        onDragOver={isMobile ? undefined : (e => {
          e.preventDefault()
          e.stopPropagation()
          handleDragOver({ stepUuid: props.uuid, isOnLowerHalf: isOnLowerHalf(e) })
        })}
        onDrop={isMobile ? undefined : handleDragStop}
        onClick={isMobile ? undefined : ((e) => {
          e.stopPropagation();
          return doTransition({
            enum: TransitionEnum.CLICK_LINE,
            lineUuid: props.uuid,
          });
        })}
        onDoubleClick={isMobile ? undefined : (e) => {
          e.stopPropagation();
          return doTransition({
            enum: TransitionEnum.DOUBLE_CLICK_LINE,
            lineUuid: props.uuid,
          });
        }}
        onMouseMove={isMobile ? undefined : (e) => {
          e.stopPropagation();
          if (e.currentTarget !== e.target) return
          handleHover({
            enum: HoveringEnum.HOVERING_STEP,
            stepUuid: props.uuid,
            aboveOrBelow: isOnLowerHalf(e) ? "below" : "above",
          });
        }}
        onContextMenu={isMobile ? undefined : ((e) => {
          e.preventDefault();
          e.stopPropagation();
          setContextMenuPosition({ x: e.pageX, y: e.pageY });
          doTransition({
            enum: TransitionEnum.RIGHT_CLICK_STEP,
            proofStepUuid: props.uuid,
            isBox: false,
          });
        })}
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
  const { handleHover, hoveringState } = useHovering();
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

  React.useEffect(() => {
    if (isEditingFormula) {
      formulaInputRef?.current?.focus()
    }
  }, [isEditingFormula])

  return <div
    className={cn(
      "relative",
      formulaIsWrong ? "text-red-500" : "",
      formulaIsBeingHovered(lineUuid, hoveringState) && "text-blue-600",
    )}
    onMouseMove={_ => handleHover({ enum: HoveringEnum.HOVERING_FORMULA, stepUuid: lineUuid })}
  >
    <div
      className={cn(
        "absolute left-[-4px] top-[-1px] border-black",
        "bg-slate-100 z-10"
      )}
      style={isEditingFormula ? {} : { display: "none" }}
    >
      <AutosizeInput
        inputRef={handleInputRefChange}
        value={currentFormulaValue}
        onClickCapture={(e) => e.stopPropagation()}
        onDoubleClickCapture={(e) => e.stopPropagation()}
        onChange={(e) => doTransition({
          enum: TransitionEnum.UPDATE_CONTENT,
          content: e.target.value,
        })}
        onKeyDown={(e) => onKeyDownAutoSizeInput(e.key)}
        placeholder="???"
        inputClassName={cn(
          "px-1 py-1 focus:border-black focus:border outline-none rounded",
          "bg-transparent",
          "font-mono text-sm tracking-tighter",
          isMobile && "text-[16px] py-0",
        )}
      />
    </div>
    <div
      className={cn(
        "h-full",
        isEditingFormula ? "opacity-0" : "",
      )}
      onClick={isMobile ? undefined : (e => {
        e.stopPropagation()
        doTransition({
          enum: e.detail <= 1 ? TransitionEnum.CLICK_LINE : TransitionEnum.DOUBLE_CLICK_LINE,
          lineUuid
        })
      })}
    >
      {!isSyncedWithServer || !latexFormula || latexFormula === "" ? (
        <div className="h-full font-mono text-sm tracking-tighter flex items-center">{userInput === "" ? "???" : userInput}</div>
      ) : (
        <InlineMath math={formulaLatexContentWithUnderline}></InlineMath>
      )}
    </div>
  </div>
}
