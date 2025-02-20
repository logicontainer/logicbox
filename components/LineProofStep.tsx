import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";

import { AddLineTooltip } from "./AddLineTooltip";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { cn } from "@/lib/utils";
import { useContextMenu } from "react-contexify";
import { useProof } from "@/contexts/ProofProvider";
import { useState } from "react";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { setLineInFocus, isFocused, isActiveEdit, setActiveEdit, removeIsActiveEditFromLine } = useProof();
  const [tooltipContent, setTooltipContent] = useState<string>()
  const isInFocus = isFocused(props.uuid)
  const isTheActiveEdit = isActiveEdit(props.uuid)

  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex)
  }
  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });
  function handleContextMenu (event: React.MouseEvent<HTMLElement> | React.TouchEvent<HTMLElement> | React.KeyboardEvent<HTMLElement> | KeyboardEvent) {
    show({
      event,
      props: {
        uuid: props.uuid,
      }
    })
  }
  return (
    (<div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors", isInFocus ? "text-blue-400" : "", isTheActiveEdit ? "bg-blue-400 text-slate-200" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={() => isTheActiveEdit ? removeIsActiveEditFromLine(props.uuid) : setActiveEdit(props.uuid)}
      onContextMenuCapture={handleContextMenu}
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