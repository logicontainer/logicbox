import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { InlineMath } from "react-katex";
import { Justification } from "./Justification";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  return (
    <div className="flex gap-8 text-lg/10 text-slate-800"><p className="grow">
      <InlineMath math={props.latexFormula} /></p>
      <Justification justification={props.justification} lines={props.lines} />
    </div>
  )
}