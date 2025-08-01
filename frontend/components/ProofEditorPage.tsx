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
      <div onMouseMove={_ => handleHover(null)} className="h-screen w-full sm:w-auto sm:grid sm:grid-cols-[550px_1fr]">
        <div className="overflow-auto relative">
          <ContextSidebar />
        </div>
        <div className="overflow-auto relative">
          <ProofEditor proofId={proofId} />
        </div>
      </div>
    </div>
  );
}
