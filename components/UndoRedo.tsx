import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider";

export function UndoRedo () {
  const historyContext = useHistory();
  const handleUndo = () => {
    historyContext.undo();
  }
  const handleRedo = () => {
    historyContext.redo();
  }
  return (
    <div className="flex gap-2">
      <button className={cn("border-solid border-2 border-slate-800 rounded-sm px-4", !historyContext.canUndo ? "text-slate-500 border-slate-500" : "")} onClick={handleUndo} disabled={!historyContext.canUndo}>
        Undo
      </button>
      <button className={cn("border-solid border-2 border-slate-800 rounded-sm px-4", !historyContext.canRedo ? "text-slate-500 border-slate-500" : "")} onClick={handleRedo} disabled={!historyContext.canRedo}>
        Redo
      </button>
    </div>
  )
}