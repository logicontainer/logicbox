import { LineNumberLine } from "@/types/types";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";

export function LineNumber ({ line }: { line: LineNumberLine }) {
  const { lineInFocus, setLineInFocus, removeFocusFromLine } = useProof();
  const isInFocus = lineInFocus == line.uuid;

  return (
    <p
      className={cn("text-sm/10 text-left text-slate-800 align-baseline cursor-pointer", isInFocus ? "text-highlight underline" : "")}
      onMouseOver={() => setLineInFocus(line.uuid)}
      onMouseLeave={() => removeFocusFromLine(line.uuid)}
    >
      {line.lineNumber}.
    </p>
  )
}