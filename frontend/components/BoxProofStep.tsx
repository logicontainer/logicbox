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
import React from "react";
import { cn, isOnLowerHalf } from "@/lib/utils";
import { getStepHighlight } from "@/lib/proof-step-highlight";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useStepDrag } from "@/contexts/StepDragProvider";

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

  const highlight = getStepHighlight(
    props.uuid,
    interactionState,
    hoveringState,
    useProof(),
  );

  const freshVar = props.boxInfo?.freshVar;

  const dropZoneDirection: 'above' | 'below' | null = 
    interactionState.enum === InteractionStateEnum.MOVING_STEP && interactionState.toUuid === props.uuid ? interactionState.direction : null

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
        draggable
        onDragStart={_ => handleDragStart(props.uuid)}
        onDragOver={e => {
          e.stopPropagation()
          e.preventDefault()
          handleDragOver(props.uuid, isOnLowerHalf(e))
        }}
        onDrop={handleDragStop}
        onContextMenu={(e) => {
          e.preventDefault();
          e.stopPropagation();
          setContextMenuPosition({ x: e.pageX, y: e.pageY });
          doTransition({
            enum: TransitionEnum.RIGHT_CLICK_STEP,
            proofStepUuid: props.uuid,
            isBox: true,
          });
        }}
        onClick={(e) => {
          e.stopPropagation();
          doTransition({
            enum: TransitionEnum.CLICK_BOX,
            boxUuid: props.uuid,
          });
        }}
        onDoubleClick={e => {
          e.stopPropagation()
          doTransition({
            enum: TransitionEnum.DOUBLE_CLICK_BOX,
            boxUuid: props.uuid
          })
        }}
        onMouseMove={(e) => {
          e.stopPropagation();
          handleHover({ 
            enum: HoveringEnum.HOVERING_STEP,
            stepUuid: props.uuid,
            aboveOrBelow: isOnLowerHalf(e) ? "below" : "above"
          });
        }}
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
