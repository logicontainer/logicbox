import { InlineMath } from "react-katex";
import { TLineNumber } from "@/types/types";
import { cn } from "@/lib/utils";
import { useServer } from "@/contexts/ServerProvider";

export default function LineNumber({ line }: { line: TLineNumber }) {
  const serverContext = useServer();
  const proofDiagnostics = serverContext.proofDiagnostics;
  const diagnosticMap = Object.fromEntries(
    proofDiagnostics.map((d) => [d.uuid, d])
  );

  const proofStepDiagnostics = diagnosticMap[line.uuid];

  if (!line || line.stepType !== "line") {
    return null;
  }
  return (
    <div
      className={cn(
        "text-base/relaxed text-center align-baseline cursor-pointer px-1 w-full rounded-md h-full flex items-center justify-stretch"
      )}
    >
      <span
        className={cn(
          "w-full rounded-sm",
          proofStepDiagnostics ? "bg-red-500 text-slate-200" : ""
        )}
      >
        <InlineMath math={line?.lineNumber.toString() + "."} />
      </span>
    </div>
  );
}
