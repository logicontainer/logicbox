import { Lightbulb, TriangleAlert } from "lucide-react";

import { InlineMath } from "react-katex";
import { TLineNumber } from "@/types/types";
import { cn } from "@/lib/utils";
import { useServer } from "@/contexts/ServerProvider";
import { useProof } from "@/contexts/ProofProvider";

export default function LineNumber({ line }: { line: TLineNumber }) {
  const serverContext = useServer();
  const { getParentUuid } = useProof()
  let proofDiagnostics = serverContext.proofDiagnostics;

  const parentUuid = getParentUuid(line.uuid)
  const shouldShowTriangle = proofDiagnostics.some(d => d.uuid === line.uuid || d.uuid === parentUuid)

  if (!line || line.stepType !== "line") {
    return null;
  }
  return (
    <div
      className={cn(
        "text-base/relaxed text-right align-baseline cursor-pointer px-1 w-full rounded-md h-full flex flex-row-reverse items-center justify-end",
      )}
    >
      <div className={cn("rounded-sm flex-grow")}>
        <InlineMath math={line?.lineNumber.toString() + "."} />
      </div>
      {shouldShowTriangle && (
        <TriangleAlert className="text-red-500"></TriangleAlert>
      )}
    </div>
  );
}
