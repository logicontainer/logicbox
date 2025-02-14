import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { AddLineTooltip } from "./AddLineTooltip";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
// import { RemoveLineTooltip } from "./RemoveLineTooltip";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";
import { useState } from "react";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { isUnfocused, setLineInFocus, isFocused } = useProof();
  const [tooltipContent, setTooltipContent] = useState<string>()
  const isInFocus = isFocused(props.uuid)

  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex)
  }
  return (
    (<div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors", isUnfocused(props.uuid) ? "text-slate-400" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)}
    >
      {/* <RemoveLineTooltip uuid={props.uuid} isVisible={isInFocus} prepend /> */}
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} prepend />
      <p className="shrink">
        <InlineMath math={props.latexFormula} />
      </p>
      <div data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}>
        <Justification justification={props.justification} lines={props.lines} onHover={handleOnHoverJustification} />
      </div>
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} />
    </div>)
  );
}