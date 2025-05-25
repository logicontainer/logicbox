import { Highlight } from "@/lib/proof-step-highlight";
import { InlineMath } from "react-katex";
import { cn } from "@/lib/utils";

export function ProofStepWrapper({
  children,
  isBox,
  isOuterProofStep,
  highlight,
  freshVar,
}: {
  children: React.ReactNode;
  isBox?: boolean;
  isOuterProofStep?: boolean;
  highlight?: Highlight;
  currentlySelected?: boolean;
  freshVar?: string;
}) {
  return (
    <div
      className={cn(
        !isOuterProofStep && "px-3 pointer-events-none bg-none",
        !isBox && highlight === Highlight.HOVERED && "bg-slate-50",
        !isBox && highlight === Highlight.SELECTED && "bg-slate-100",
        !isBox &&
          highlight === Highlight.HOVERED_AND_OTHER_IS_SELECTING_REF &&
          "bg-blue-200",
        !isBox && highlight === Highlight.REFERRED && "bg-blue-200"
      )}
    >
      {isBox && freshVar && (
        <span className="absolute bg-white px-1 text-lg/tight ml-3 -translate-y-1/2 rounded-sm">
          <InlineMath math={freshVar}></InlineMath>
        </span>
      )}
      {children}
    </div>
  );
}
