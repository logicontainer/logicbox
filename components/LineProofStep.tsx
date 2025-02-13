import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { AddLineCommand } from "@/lib/commands";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider";
import { useProof } from "@/contexts/ProofProvider";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { lineInFocus, setLineInFocus, removeFocusFromLine } = useProof();
  const historyContext = useHistory();
  const isInFocus = lineInFocus == props.uuid;
  const handleAddLine = (prepend: boolean = false) => {
    const addLineCommand = new AddLineCommand(props.uuid, prepend);
    historyContext.addToHistory(addLineCommand)
  }
  return (
    <div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 cursor-pointer px-[-1rem]", isInFocus ? "text-highlight underline" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onMouseLeave={() => removeFocusFromLine(props.uuid)}
    >
      {isInFocus && <div className="h-4 w-4 bg-red-200 absolute top-[-8px] hover:bg-red-400" onClick={() => handleAddLine(true)} onMouseOverCapture={(e) => {
        e.stopPropagation();
      }}></div>}
      <p className="shrink">
        <InlineMath math={props.latexFormula} />
      </p>
      <Justification justification={props.justification} lines={props.lines} />
      {isInFocus && <div className="h-4 w-4 bg-green-200 absolute bottom-[-8px] hover:bg-green-600" onClick={() => handleAddLine(false)} onMouseOverCapture={(e) => {
        e.stopPropagation();
      }}></div>}
    </div>
  )
}