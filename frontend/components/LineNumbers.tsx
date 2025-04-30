import { LineNumber } from "./LineNumber";
import { TLineNumber } from "@/types/types";
import { useServer } from "@/contexts/ServerProvider";

export function LineNumbers({ lines }: { lines: TLineNumber[] }) {
  const serverContext = useServer();
  const proofDiagnostics = serverContext.proofDiagnostics;
  if (!lines) return;

  return (
    <>
      <div className="flex-col items-start">
        {lines
          .filter((line) => !line.isBox)
          .map((line) => {
            const proofStepDiagnostics = proofDiagnostics.find(
              (diagnostic) => diagnostic.uuid === line.uuid
            );
            return (
              <LineNumber
                key={line.uuid}
                line={line}
                proofStepDiagnostics={proofStepDiagnostics}
              />
            );
          })}
      </div>
    </>
  );
}
