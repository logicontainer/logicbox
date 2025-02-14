import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider"

export function History () {
  const historyContext = useHistory();
  return (
    <>
      <p>History:</p>
      <div className="h-32 w-96 overflow-y-auto border-2">
        <ol>
          {historyContext.history.map((command, i) => {
            return (<li
              className={cn(historyContext.now - 1 == i ? "text-green-500" : "")}
              key={command.getCommandUuid()}>
              {command.getDescription()}
            </li>)
          })}
        </ol>
      </div>
    </>
  )
}