import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { cn } from "@/utils/utils";
import { useProof } from "@/contexts/ProofProvider";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { lineInFocus, setLineInFocus, removeFocusFromLine } = useProof();
  const isInFocus = lineInFocus == props.uuid;
  return (
    <div
      className={cn("flex justify-between gap-8 text-lg/10 text-slate-800 cursor-pointer px-[-1rem]", isInFocus ? "text-highlight underline" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onMouseLeave={() => removeFocusFromLine(props.uuid)}
    >
      <p className="shrink">
        <InlineMath math={props.latexFormula} /></p>
      <Justification justification={props.justification} lines={props.lines} />
    </div>
  )
}