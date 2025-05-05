import { LineNumber } from "./LineNumber";
import { TLineNumber } from "@/types/types";
import { useServer } from "@/contexts/ServerProvider";

export function LineNumbers({ lines }: { lines: TLineNumber[] }) {
  const serverContext = useServer();
  const proofDiagnostics = serverContext.proofDiagnostics;
  const diagnosticMap = Object.fromEntries(
    proofDiagnostics.map((d) => [d.uuid, d])
  );
  if (!lines) return;

  return (
    <>
      <div className="flex-col items-start">
        {lines
          .filter((line) => line.stepType !== "box")
          .map((line) => {
            return (
              <LineNumber
                key={line.uuid}
                line={line}
                proofStepDiagnostics={diagnosticMap[line.uuid]}
              />
            );
          })}
      </div>
    </>
  );
}
