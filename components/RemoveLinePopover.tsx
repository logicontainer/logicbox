// import { RemoveLineCommand } from "@/lib/commands";
// import { useHistory } from "@/contexts/HistoryProvider";
import { Tooltip } from "react-tooltip";
import { useProof } from "@/contexts/ProofProvider";

export function RemoveLinePopover ({ uuid }: { uuid: string }) {
  // const historyContext = useHistory();
  const proofContext = useProof();
  const handleRemoveLine = () => {
    // const removeLineCommand = new RemoveLineCommand(uuid)
    // historyContext.addToHistory(removeLineCommand)
  }
  return (
    <Tooltip id={`remove-line-${uuid}`} place="left" className="z-50" openOnClick
      data-tooltip-delay-hide={1000} noArrow
    >
      <div className="z-50 flex flex-col gap-2 cursor-pointer" onMouseOverCapture={(e) => {
        console.log("Capture mouse over (remove)")
        e.stopPropagation();
        proofContext.setLineInFocus(proofContext.latestLineInFocus || "")
      }}>
        Are you sure?
        <button className="bg-red-600 text-slate-200" onClick={handleRemoveLine}
        >Confirm</button>
      </div>
    </Tooltip>
  )
}