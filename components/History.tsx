import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider"

export function History () {
  const historyContext = useHistory();
  return (
    <>
      <p>History:</p>
      <ol>
        {historyContext.history.map((command, i) => {
          return (<li
            className={cn(historyContext.now - 1 == i ? "text-red-500" : "")}
            key={command.getCommandUuid()}>
            {command.getDescription()}
          </li>)
        })}
      </ol>
    </>
  )
}