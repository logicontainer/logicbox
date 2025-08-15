import { useHovering } from "@/contexts/HoveringProvider";
import ContextSidebar from "@/components/ContextSidebar";
import ProofEditor from "./ProofEditor";
import { TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { useStepDrag } from "@/contexts/StepDragProvider";
import React from "react";
import { HelpDialogButton } from "@/components/HelpDialogButton"

export default function ProofEditorPage({ proofId }: { proofId: string }) {
  const { handleHover } = useHovering()
  const { doTransition } = useInteractionState();
  const { handleDragOver, handleDragStop } = useStepDrag()

  return (
    <div className="flex justify-center items-start"
      onClick={() => doTransition({ enum: TransitionEnum.INTERACT_OUTSIDE })}
      onMouseMove={() => handleHover(null)}
      onDragOver={() => handleDragOver(null)}
      onDragEnd={() => handleDragStop()}
    >
      <div
        className="h-screen w-[min(100%,550px)] md:w-auto md:grid md:grid-cols-[1fr_550px]"
      >
        <div
          className="overflow-hidden relative md:order-2"
          onClick={e => e.stopPropagation()} // don't make INTERACT_OUTSIDE event
        >
          <ContextSidebar />
        </div>
        <div className="overflow-hidden relative md:order-1 ">
          <ProofEditor proofId={proofId} />
        </div>
      </div>
      <HelpDialogButton/>
    </div>
  );
}
