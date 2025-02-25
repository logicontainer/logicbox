import { useServer } from "@/contexts/ServerProvider";

export function UnsyncedServerCommands() {
  const serverContext = useServer();
  const marshalledCommands = serverContext.serverCommands.map((command) => {
    return command.marshall();
  });
  return (
    <div className="flex-col items-start">
      {marshalledCommands.map((command, index) => {
        return (
          <div key={index} className="text-xs text-slate-600">
            {JSON.stringify(command)}
          </div>
        );
      })}
    </div>
  );
}
