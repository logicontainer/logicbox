import { InlineMath } from "react-katex";
import { cn } from "@/lib/utils";
import AutosizeInput from "react-input-autosize";
import React from "react";
import { HoveringEnum, HoveringState, InteractionState, InteractionStateEnum, TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { freshVarIsBeingEdited } from "@/lib/state-helpers";
import { ProofContextProps, useProof } from "@/contexts/ProofProvider";
import { getStepHighlight, StepHighlight } from "@/lib/proof-step-highlight";
import { useHovering } from "@/contexts/HoveringProvider";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { MemoizedInlineMath } from "./MemoizedInlineMath";

function computeBackgroundColor(boxUuid: string, interactionState: InteractionState, proofContext: ProofContextProps, hoveringState: HoveringState | null): string {
  const highlight = getStepHighlight(boxUuid, interactionState, hoveringState, proofContext);
  switch (highlight) {
    case StepHighlight.SELECTED: return "bg-slate-100"
    case StepHighlight.HOVERED: return "bg-slate-50"
    case StepHighlight.HOVERED_AND_OTHER_IS_SELECTING_REF: return "bg-blue-200"
    case StepHighlight.REFERRED: return "bg-blue-200"
    default: {
      const parent = proofContext.getParentUuid(boxUuid)
      if (!parent) return ""
      return computeBackgroundColor(parent, interactionState, proofContext, hoveringState)
    }
  }
}

export default function FreshVars({
  value: latexFormula,
  boxUuid,
}: {
  value: string | null;
  boxUuid: string;
}) {
  const { interactionState, doTransition } = useInteractionState()

  const formulaInputRef = React.useRef<HTMLInputElement>(null);
  const handleInputRefChange = (ref: HTMLInputElement | null) => {
    formulaInputRef.current = ref;
  };

  const currentlyBeingEdited = freshVarIsBeingEdited(boxUuid, interactionState)
  const currentFreshVarValue = interactionState.enum === InteractionStateEnum.EDITING_FRESH_VAR ?
    interactionState.freshVar : latexFormula

  const onKeyDownAutoSizeInput = (key: string) => {
    if (currentlyBeingEdited && key === "Enter") {
      doTransition({ enum: TransitionEnum.CLOSE });
      formulaInputRef.current?.blur();
    }
  };

  React.useEffect(() => {
    if (currentlyBeingEdited) {
      formulaInputRef.current?.focus()
    }
  }, [currentlyBeingEdited])

  const { hoveringState, handleHover } = useHovering()
  const { setContextMenuPosition } = useContextMenu()

  return <div 
    className={cn(
      "absolute bg-white -translate-y-1/2 translate-x-3 px-1 rounded-sm overflow-visible",
      computeBackgroundColor(boxUuid, interactionState, useProof(), hoveringState)
    )}
    onMouseMove={e => {
      e.stopPropagation()
      handleHover({ enum: HoveringEnum.HOVERING_STEP, stepUuid: boxUuid, aboveOrBelow: 'above' })
    }}
    onContextMenu={e => {
      e.stopPropagation()
      e.preventDefault()
      setContextMenuPosition({ x: e.pageX, y: e.pageY })
      doTransition({ enum: TransitionEnum.RIGHT_CLICK_STEP, proofStepUuid: boxUuid, isBox: true })
    }}
  >
    <div
      className={cn(
        !currentlyBeingEdited && "hidden"
      )}
    >
      <AutosizeInput
        inputRef={handleInputRefChange}
        value={currentFreshVarValue ?? ""}
        onClickCapture={(e) => e.stopPropagation()}
        onDoubleClickCapture={(e) => e.stopPropagation()}
        onChange={(e) => doTransition({ enum: TransitionEnum.UPDATE_CONTENT, content: e.target.value })}
        onKeyDown={(e) => onKeyDownAutoSizeInput(e.key)}
        placeholder="???"
        className="grow resize"
        inputClassName={cn(
          "px-1 py-2 focus:border-black focus:border outline-none rounded",
          "bg-transparent",
          "font-mono text-sm",
        )}
      />
    </div>
    <div 
      className={cn(
        "h-full",
        (currentlyBeingEdited || !latexFormula) && "hidden"
      )}
      onDoubleClick={e => {
        doTransition({
          enum: TransitionEnum.DOUBLE_CLICK_BOX,
          boxUuid
        })

        e.stopPropagation();
      }}
    >
      {latexFormula ? <MemoizedInlineMath math={latexFormula}></MemoizedInlineMath> : null}
    </div>
  </div>
}
