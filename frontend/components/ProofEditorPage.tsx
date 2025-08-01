import { useHovering } from "@/contexts/HoveringProvider";
import ContextSidebar from "@/components/ContextSidebar";
import Footer from "@/components/Footer";
import ProofEditor from "./ProofEditor";

export default function ProofEditorPage({ proofId }: { proofId: string }) {
  const { handleHover } = useHovering()

  return (
    <div className="flex flex-col gap-3">
      <div className="flex justify-center items-start">
        <div onMouseMove={_ => handleHover(null)} className="h-screen grid grid-cols-[550px_1fr]">
          <div className="overflow-auto relative">
            <ContextSidebar />
          </div>
          <div className="overflow-auto relative">
            <ProofEditor proofId={proofId} />
          </div>
        </div>
      </div>
      <div className="sm:hidden">
        <Footer />
      </div>
    </div>
  );
}
