import { LineNumber } from "./LineNumber";
import { TLineNumber } from "@/types/types";
import { cn } from "@/lib/utils";
import { useServer } from "@/contexts/ServerProvider";

export function LineNumbers({ lines }: { lines: TLineNumber[] }) {
  const serverContext = useServer();
  const proofDiagnostics = serverContext.proofDiagnostics;
  const diagnosticMap = Object.fromEntries(
    proofDiagnostics.map((d) => [d.uuid, d])
  );
  if (!lines) return;

  const nonBoxLines = lines.filter((line) => line.stepType !== "box");

  return (
    <>
      <div className="flex-col items-start">
        {nonBoxLines.map((line, i) => {
          return (
            <LineNumber
              key={line.uuid}
              line={line}
              proofStepDiagnostics={diagnosticMap[line.uuid]}
              className={cn(
                i === 0 && "rounded-t-md",
                i === nonBoxLines.length - 1 && "rounded-b-md"
              )}
            />
          );
        })}
      </div>
    </>
  );
}
