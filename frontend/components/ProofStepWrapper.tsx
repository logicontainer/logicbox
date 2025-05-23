import { cn } from "@/lib/utils";

export function ProofStepWrapper({
  children,
  isBox,
  isOuterProofStep,
  currentlyBeingHovered,
  currentlySelected,
}: {
  children: React.ReactNode;
  isBox?: boolean;
  isOuterProofStep?: boolean;
  currentlyBeingHovered?: boolean;
  currentlySelected?: boolean;
}) {
  if (isOuterProofStep) {
    return children;
  } else {
    return (
      <div
        className={cn(
          "px-3 pointer-events-none bg-none",
          !isBox && currentlyBeingHovered && "bg-slate-50",
          !isBox && currentlySelected && "bg-slate-100"
        )}
      >
        {children}
      </div>
    );
  }
}
