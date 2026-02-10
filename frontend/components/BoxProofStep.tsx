import {
  Diagnostic,
  BoxProofStep as TBoxProofStep,
  TLineNumber,
} from "@/types/types";
import {
    HoveringEnum,
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import FreshVars from "./FreshVars";
import { StepHighlight } from "@/lib/proof-step-highlight";
import { Proof } from "./Proof";
import { ProofStepWrapper } from "./ProofStepWrapper";
import React, { TouchEvent } from "react";
import { cn, isOnLowerHalf } from "@/lib/utils";
import { getStepHighlight } from "@/lib/proof-step-highlight";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useStepDrag } from "@/contexts/StepDragProvider";
import { stepIsDraggable } from "@/lib/state-helpers";
import { isMobile } from "react-device-detect";

export function BoxProofStep({
  ...props
}: TBoxProofStep & {
  lines: TLineNumber[];
  diagnostics: Diagnostic[];
  isOuterProofStep?: boolean;
}) {
  const { doTransition, interactionState } = useInteractionState();
  const { setContextMenuPosition } = useContextMenu();
  const { handleHover, hoveringState } = useHovering();
  const { handleDragStart, handleDragOver, handleDragStop } = useStepDrag()
  const proofContext = useProof()

  const highlight = getStepHighlight(
    props.uuid,
    interactionState,
    hoveringState,
    proofContext
  );

  const freshVar = props.boxInfo?.freshVar;

  const dropZoneDirection: 'above' | 'below' | null = 
    interactionState.enum === InteractionStateEnum.MOVING_STEP && interactionState.toUuid === props.uuid ? interactionState.direction : null

  const touchTimeout = React.useRef<NodeJS.Timeout | null>(null)
  const touchPosition = React.useRef<{ x: number, y: number, target: HTMLElement } | null>(null)

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
      if (touchPosition.current && isWithinBounds(touchPosition.current)) {
        setContextMenuPosition(touchPosition.current)
        doTransition({ enum: TransitionEnum.RIGHT_CLICK_STEP, proofStepUuid: props.uuid, isBox: true })
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
      doTransition({ enum: TransitionEnum.CLICK_LINE, lineUuid: props.uuid })
    }

    touchPosition.current = null
    if (touchTimeout.current)
      clearTimeout(touchTimeout.current)
  }

  return (
    <ProofStepWrapper isOuterProofStep={props.isOuterProofStep} isBox={true}>
      <FreshVars value={freshVar} boxUuid={props.uuid}/>
      <div
        className={cn(
          "pointer-events-auto border-2 overflow-x-visible pt-1 mb-1",
          "border-black",
          freshVar && "mt-1.5 pt-1.5",
          dropZoneDirection === "above" && "border-t-[4px]",
          dropZoneDirection === "below" && "border-b-[4px]",
          highlight === StepHighlight.SELECTED && "border-red-500",
          highlight === StepHighlight.SELECTED && "bg-slate-100",
          highlight === StepHighlight.HOVERED && "bg-slate-50",
          highlight === StepHighlight.HOVERED_AND_OTHER_IS_SELECTING_REF && "bg-blue-200",
          highlight === StepHighlight.REFERRED && "bg-blue-200",
        )}
        draggable={stepIsDraggable(props.uuid, interactionState, proofContext)}
        onDragStart={isMobile ? undefined : (_ => handleDragStart(props.uuid))}
        onDragOver={isMobile ? undefined : (e => {
          e.stopPropagation()
          e.preventDefault()
          handleDragOver({ stepUuid: props.uuid, isOnLowerHalf: isOnLowerHalf(e) })
        })}
        onDrop={isMobile ? undefined : handleDragStop}
        onContextMenu={isMobile ? undefined : ((e) => {
          e.preventDefault();
          e.stopPropagation();
          setContextMenuPosition({ x: e.pageX, y: e.pageY });
          doTransition({
            enum: TransitionEnum.RIGHT_CLICK_STEP,
            proofStepUuid: props.uuid,
            isBox: true,
          });
        })}
        onClick={isMobile ? undefined : ((e) => {
          e.stopPropagation();
          doTransition({
            enum: TransitionEnum.CLICK_BOX,
            boxUuid: props.uuid,
          });
        })}
        onDoubleClick={isMobile ? undefined : ((e => {
          e.stopPropagation()
          doTransition({
            enum: TransitionEnum.DOUBLE_CLICK_BOX,
            boxUuid: props.uuid
          })
        }))}
        onMouseMove={isMobile ? undefined : ((e) => {
          e.stopPropagation();
          handleHover({ 
            enum: HoveringEnum.HOVERING_STEP,
            stepUuid: props.uuid,
            aboveOrBelow: isOnLowerHalf(e) ? "below" : "above"
          });
        })}

        onTouchStart={isMobile ? handleTouchStart : undefined}
        onTouchMove={isMobile ? handleTouchMove : undefined}
        onTouchEnd={isMobile ? handleTouchEnd : undefined}
      >
        <Proof
          proof={props.proof}
          lines={props.lines}
          uuid={props.uuid}
          diagnostics={props.diagnostics}
        />
      </div>
    </ProofStepWrapper>
  );
}
