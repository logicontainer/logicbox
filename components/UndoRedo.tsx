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
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-4 h-4">
          <path d="M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6s-2.69 6-6 6-6-2.69-6-6H4c0 4.42 3.58 8 8 8s8-3.58 8-8-3.58-8-8-8z" />
        </svg>
      </button>
      <button className={cn("border-solid border-2 border-slate-800 rounded-sm px-4", !historyContext.canRedo ? "text-slate-500 border-slate-500" : "")} onClick={handleRedo} disabled={!historyContext.canRedo}>
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-4 h-4">
          <path d="M12 4V1l4 4-4 4V6c-3.31 0-6 2.69-6 6s2.69 6 6 6 6-2.69 6-6h2c0 4.42-3.58 8-8 8s-8-3.58-8-8 3.58-8 8-8z" />
        </svg>
      </button>
    </div>
  )
}