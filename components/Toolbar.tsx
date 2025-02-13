import { AddLineCommand } from "@/lib/commands";
import { History } from "./History";
import { UndoRedo } from "./UndoRedo";
import { useHistory } from "@/contexts/HistoryProvider";

export function Toolbar () {
  const historyContext = useHistory();


  const handleAddLine = () => {
    const addLineCommand = new AddLineCommand("j");
    historyContext.addToHistory(addLineCommand)
  }

  return (
    <div className="flex gap-4">
      <div className="flex-col">
        <UndoRedo />
        <History />
      </div>
      <div>
        <button className="border-solid border-2 border-slate-800 rounded-sm px-4" onClick={handleAddLine}>
          Add line before what is initially line 8
        </button>
      </div></div>
  )
}