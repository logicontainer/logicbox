import { LineNumberLine } from "@/types/types";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";

export function LineNumber ({ line }: { line: LineNumberLine }) {
  const { setLineInFocus, removeFocusFromLine, isUnfocused } = useProof();


  return (
    <p
      className={cn("text-sm/10 text-left text-slate-800 align-baseline cursor-pointer", isUnfocused(line.uuid) ? "text-slate-400" : "")}
      onMouseOver={() => setLineInFocus(line.uuid)}
      onMouseLeave={() => removeFocusFromLine(line.uuid)}
    >
      {line.lineNumber}.
    </p>
  )
}