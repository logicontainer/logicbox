import { useHovering } from "@/contexts/HoveringProvider";
import ContextSidebar from "@/components/ContextSidebar";
import ProofEditor from "./ProofEditor";
import { TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";

export default function ProofEditorPage({ proofId }: { proofId: string }) {
  const { handleHover } = useHovering()
  const { doTransition } = useInteractionState();

  return (
    <div className="flex justify-center items-start"
      onClick={() => doTransition({ enum: TransitionEnum.CLICK_OUTSIDE })}
    >
      <div onMouseMove={_ => handleHover(null)} className="h-screen w-[min(100%,550px)] md:w-auto md:grid md:grid-cols-[550px_1fr]">
        <div className="overflow-hidden relative">
          <ContextSidebar />
        </div>
        <div className="overflow-hidden relative">
          <ProofEditor proofId={proofId} />
        </div>
      </div>
    </div>
  );
}
