import { InlineMath } from "react-katex";
import { cn } from "@/lib/utils";
import AutosizeInput from "react-input-autosize";
import React from "react";
import { HoveringEnum, InteractionStateEnum, TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { freshVarIsBeingEdited } from "@/lib/state-helpers";
import { useProof } from "@/contexts/ProofProvider";
import { getStepHighlight, StepHighlight } from "@/lib/proof-step-highlight";
import { useHovering } from "@/contexts/HoveringProvider";
import { useContextMenu } from "@/contexts/ContextMenuProvider";

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

  const highlight = getStepHighlight(
    boxUuid,
    interactionState,
    hoveringState,
    useProof(),
  );

  return <div 
    className={cn(
      "absolute bg-white -translate-y-1/2 translate-x-3 px-1 rounded-sm overflow-visible",
      highlight === StepHighlight.SELECTED && "border-red-500",
      highlight === StepHighlight.SELECTED && "bg-slate-100",
      highlight === StepHighlight.HOVERED && "bg-slate-50",
      highlight === StepHighlight.HOVERED_AND_OTHER_IS_SELECTING_REF &&
        "bg-blue-200",
      highlight === StepHighlight.REFERRED && "bg-blue-200",
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
      {latexFormula ? <InlineMath math={latexFormula}></InlineMath> : null}
    </div>
  </div>
}
