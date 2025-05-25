import {
  Diagnostic,
  BoxProofStep as TBoxProofStep,
  TLineNumber,
} from "@/types/types";
import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import FreshVars from "./FreshVars";
import { Highlight } from "@/lib/proof-step-highlight";
import { InlineMath } from "react-katex";
import { Proof } from "./Proof";
import { ProofStepWrapper } from "./ProofStepWrapper";
import React from "react";
import { cn } from "@/lib/utils";
import { getSelectedStep } from "@/lib/state-helpers";
import { getStepHighlight } from "@/lib/proof-step-highlight";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep({
  ...props
}: TBoxProofStep & {
  lines: TLineNumber[];
  diagnostics: Diagnostic[];
  isOuterProofStep?: boolean;
}) {
  const { doTransition, interactionState } = useInteractionState();
  const { setContextMenuPosition } = useContextMenu();
  const { currentlyHoveredUuid, handleHoverStep } = useHovering();

  const highlight = getStepHighlight(
    props.uuid,
    currentlyHoveredUuid,
    interactionState,
    useProof()
  );

  const freshVar = props.boxInfo?.freshVar;

  return (
    <ProofStepWrapper isOuterProofStep={props.isOuterProofStep} isBox={true}>
      <FreshVars value={freshVar} />
      <div
        className={cn(
          "pointer-events-auto border-2 overflow-hidden pt-1 mb-1",
          "border-black",
          freshVar && "mt-1.5 pt-1.5",

          highlight === Highlight.SELECTED && "border-red-500",

          highlight === Highlight.SELECTED && "bg-slate-100",
          highlight === Highlight.HOVERED && "bg-slate-50",
          highlight === Highlight.HOVERED_AND_OTHER_IS_SELECTING_REF &&
            "bg-blue-200",
          highlight === Highlight.REFERRED && "bg-blue-200"
        )}
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
        onMouseMove={(e) => {
          e.stopPropagation();
          handleHoverStep(props.uuid, null, false);
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
