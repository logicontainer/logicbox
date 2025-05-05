"use client";

import Card from "./Card";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";

export default function ContextSidebar() {
  const { lineInFocus } = useProof();
  const { lines } = useLines();
  const line = lines.find((line) => line.uuid === lineInFocus);

  const { proofDiagnostics } = useServer();
  const error = proofDiagnostics.find((d) => d.uuid === lineInFocus);

  const lineOrBox = line?.stepType === "box" ? "Box" : "Line";
  return (
    <div className="  sm:h-screen p-2">
      {line && (
        <Card>
          <p className="text-left text-xl py-2">{lineOrBox} in focus</p>
          <p>{JSON.stringify(line, null, 2)}</p>
          {error && (
            <>
              <p className="text-left text-sm py-2">Error</p>
              <p className="text-red-500">Error: {error.violationType}</p>
            </>
          )}
        </Card>
      )}
    </div>
  );
}
