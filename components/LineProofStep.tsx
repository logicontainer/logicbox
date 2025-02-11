import "katex/dist/katex.min.css";

import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { LineProofStep as TLineProofStep } from "@/types/types";

export function LineProofStep ({ ...props }: TLineProofStep) {
  return (
    <div className="flex gap-8 text-lg/10 text-slate-800"><p className="grow"><InlineMath math={props.latexFormula} /></p>
      <Justification justification={props.justification} /></div>)
}