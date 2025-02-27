import { AddBoxedLineCommand, AddLineCommand } from "@/lib/commands";

import { PlusIcon } from "./PlusIcon";
import { Tooltip } from "react-tooltip";
import { useHistory } from "@/contexts/HistoryProvider";
import { useProof } from "@/contexts/ProofProvider";

export function AddLinePopover({ uuid }: { uuid: string }) {
  const historyContext = useHistory();
  const proofContext = useProof();
  const handleAddLine = (isBox: boolean = false, prepend: boolean = false) => {
    const addLineCommand = isBox
      ? new AddBoxedLineCommand(uuid, prepend)
      : new AddLineCommand(uuid, prepend);
    historyContext.addToHistory(addLineCommand);
  };
  return (
    <Tooltip
      id={`add-line-${uuid}`}
      place="left"
      className="z-50"
      clickable
      data-tooltip-delay-hide={1000}
      noArrow
      render={({ content }) => {
        const contentObject = JSON.parse(content || "{}");
        return (
          <div
            className="z-50 flex flex-col gap-2 cursor-pointer"
            onMouseOverCapture={(e) => {
              console.log("Capture mouse over");
              e.stopPropagation();
              proofContext.setLineInFocus(proofContext.latestLineInFocus || "");
            }}
          >
            <div
              className="px-2 hover:text-green-400 hover:border-green-400"
              title="Add line"
              onClick={() => handleAddLine(false, contentObject.prepend)}
            >
              <PlusIcon />
            </div>
            <div
              className="px-2 border-2 hover:text-green-400 hover:border-green-400"
              title="Add boxed line"
              onClick={() => handleAddLine(true, contentObject.prepend)}
            >
              <PlusIcon />
            </div>
          </div>
        );
      }}
    />
  );
}
