import { Lightbulb, TriangleAlert } from "lucide-react";

import { InlineMath } from "react-katex";
import { TLineNumber } from "@/types/types";
import { cn } from "@/lib/utils";
import { useServer } from "@/contexts/ServerProvider";
import { useProof } from "@/contexts/ProofProvider";
import { MemoizedInlineMath } from "./MemoizedInlineMath";
import { useHovering } from "@/contexts/HoveringProvider";
import { formulaIsBeingHovered, getSelectedStep, stepIsSelected } from "@/lib/state-helpers";
import { useInteractionState } from "@/contexts/InteractionStateProvider";

export default function LineNumber({ line }: { line: TLineNumber }) {
  const serverContext = useServer();
  const { getParentUuid, isDescendant } = useProof()
  const { interactionState } = useInteractionState()
  const proofDiagnostics = serverContext.proofDiagnostics;

  const parentUuid = getParentUuid(line.uuid)
  const shouldShowTriangle = proofDiagnostics.some(d => d.uuid === line.uuid || d.uuid === parentUuid)

  if (!line || line.stepType !== "line") {
    return null;
  }

  const selectedStep = getSelectedStep(interactionState)
  let latexString = line?.lineNumber.toString() + "."
  if (selectedStep !== null && (selectedStep === line.uuid || isDescendant(selectedStep, line.uuid))) {
    latexString = `\\mathbf{${latexString}}`
  }

  return (
    <div
      className={cn(
        "text-base/relaxed text-right align-baseline cursor-pointer px-1 w-full rounded-md h-full flex flex-row-reverse items-center justify-end",
      )}
    >
      <div className={cn("rounded-sm flex-grow")}>
        <MemoizedInlineMath math={latexString} />
      </div>
      {shouldShowTriangle && (
        <TriangleAlert className="text-red-500"></TriangleAlert>
      )}
    </div>
  );
}
