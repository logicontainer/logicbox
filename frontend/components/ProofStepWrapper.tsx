import { StepHighlight } from "@/lib/proof-step-highlight";
import { InlineMath } from "react-katex";
import { cn } from "@/lib/utils";

export function ProofStepWrapper({
  children,
  isBox,
  isOuterProofStep,
  highlight,
}: {
  children: React.ReactNode;
  isBox?: boolean;
  isOuterProofStep?: boolean;
  highlight?: StepHighlight;
  currentlySelected?: boolean;
}) {
  return (
    <div
      className={cn(
        !isOuterProofStep && "px-3 pointer-events-none bg-none",
        !isBox && highlight === StepHighlight.HOVERED && "bg-slate-50",
        !isBox && highlight === StepHighlight.SELECTED && "bg-slate-100",
        !isBox &&
          highlight === StepHighlight.HOVERED_AND_OTHER_IS_SELECTING_REF &&
          "bg-blue-200",
        !isBox && highlight === StepHighlight.REFERRED && "bg-blue-200"
      )}
    >
      {children}
    </div>
  );
}
