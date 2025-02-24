import { Diagnostic, LineNumberLine } from "@/types/types";

import { Tooltip } from "react-tooltip";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";

export function LineNumber ({ line, proofStepDiagnostics }: { line: LineNumberLine, proofStepDiagnostics?: Diagnostic }) {
  const { setLineInFocus, removeFocusFromLine, isUnfocused } = useProof();


  return (<><p
    className={cn("text-sm/10 text-left text-slate-800 align-baseline cursor-pointer", isUnfocused(line.uuid) ? "text-slate-400" : "", proofStepDiagnostics ? "bg-red-500 text-slate-200" : "bg-green-500 text-slate-200")}
    onMouseOver={() => setLineInFocus(line.uuid)}
    onMouseLeave={() => removeFocusFromLine(line.uuid)}
    data-tooltip-content={proofStepDiagnostics?.violation?.explanation}
    data-tooltip-id={proofStepDiagnostics ? `line-number-tooltip-${line.uuid}` : ""}
  >
    {line.lineNumber}.
  </p><Tooltip className="z-50" id={`line-number-tooltip-${line.uuid}`} /></>);
}