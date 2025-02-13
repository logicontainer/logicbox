import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { AddLineCommand } from "@/lib/commands";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { Tooltip } from "react-tooltip";
import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useState } from "react";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { lineInFocus, setLineInFocus, removeFocusFromLine } = useProof();
  const [tooltipContent, setTooltipContent] = useState<string>()
  const historyContext = useHistory();
  const isInFocus = lineInFocus == props.uuid;
  const handleAddLine = (prepend: boolean = false) => {
    const addLineCommand = new AddLineCommand(props.uuid, prepend);
    historyContext.addToHistory(addLineCommand)
  }

  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex)
  }
  return (
    (<div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 cursor-pointer px-[-1rem]", isInFocus ? "text-highlight underline" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)} onMouseLeave={() => removeFocusFromLine(props.uuid)}
    >
      {isInFocus && <div className="h-4 w-4 bg-slate-200 absolute top-[-8px] rounded-full text-slate-600 hover:bg-green-400 hover:text-slate-50" onClick={() => handleAddLine(true)} onMouseOverCapture={(e) => {
        e.stopPropagation();
      }}><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-4 h-4">
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
        </svg>
      </div>}
      <p className="shrink">
        <InlineMath math={props.latexFormula} />
      </p>
      <div data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}>
        <Justification justification={props.justification} lines={props.lines} onHover={handleOnHoverJustification} />
      </div>
      {isInFocus && <div className="h-4 w-4 bg-slate-200 absolute bottom-[-8px] rounded-full text-slate-600 hover:bg-green-400 hover:text-slate-50" onClick={() => handleAddLine(false)} onMouseOverCapture={(e) => {
        e.stopPropagation();
      }}><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-4 h-4">
          <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
        </svg>
      </div>}
    </div>)
  );
}