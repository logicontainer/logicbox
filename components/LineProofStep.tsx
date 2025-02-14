import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { AddLinePopover } from "./AddLinePopover";
import { AddLineTooltip } from "./AddLineTooltip";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";
import { useState } from "react";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { lineInFocus, setLineInFocus, removeFocusFromLine } = useProof();
  const [tooltipContent, setTooltipContent] = useState<string>()
  const isInFocus = lineInFocus == props.uuid;
  const isUnfocused = lineInFocus && lineInFocus != props.uuid;


  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex)
  }
  return (
    (<div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors", isUnfocused ? "text-slate-400" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onMouseLeave={() => {
        removeFocusFromLine(props.uuid)
        return
      }
      }
    >
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} prepend />
      <p className="shrink">
        <InlineMath math={props.latexFormula} />
      </p>
      <div data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}>
        <Justification justification={props.justification} lines={props.lines} onHover={handleOnHoverJustification} />
      </div>
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} />
      <AddLinePopover uuid={props.uuid} />
    </div>)
  );
}