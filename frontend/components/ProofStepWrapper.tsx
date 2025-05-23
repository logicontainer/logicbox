import { cn } from "@/lib/utils";
import { Highlight } from "@/lib/proof-step-highlight";

export function ProofStepWrapper({
  children,
  isBox,
  isOuterProofStep,
  highlight,
}: {
  children: React.ReactNode;
  isBox?: boolean;
  isOuterProofStep?: boolean;
  highlight?: Highlight;
  currentlySelected?: boolean;
}) {
  return (
    <div
      className={cn(
        !isOuterProofStep && "px-3 pointer-events-none bg-none",
        !isBox && highlight === Highlight.HOVERED  && "bg-slate-50",
        !isBox && highlight === Highlight.SELECTED && "bg-slate-100",
        !isBox && highlight === Highlight.HOVERED_AND_OTHER_IS_SELECTING_REF && "bg-blue-200"
      )}
    >
      {children}
    </div>
  );
}
