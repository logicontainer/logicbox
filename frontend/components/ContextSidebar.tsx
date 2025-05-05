"use client";

import Card from "./Card";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";
import { DiagnosticMessage } from "./Diagnostics";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { InlineMath } from "react-katex";

export default function ContextSidebar() {
  const { lineInFocus } = useProof();
  const { lines, getReferenceString } = useLines();
  const { getRuleAtStepAsLatex } = useDiagnostics()
  const line = lines.find((line) => line.uuid === lineInFocus);

  const { proofDiagnostics } = useServer();
  const errors = proofDiagnostics.filter((d) => d.uuid === lineInFocus);

  const lineOrBox = line?.stepType === "box" ? "Box" : "Line";
  const refStr = lineInFocus && getReferenceString(lineInFocus)

  const ruleLatex = lineInFocus && getRuleAtStepAsLatex(lineInFocus, [], false)

  return (
    <div className="  sm:h-screen p-2">
      {line && (
        <Card>
          <p className="text-left text-xl py-2">{lineOrBox} {refStr} in focus</p>
          <p className="flex justify-center items-center text-md bg-gray-100 rounded-md py-4 h-32">
            <InlineMath math={ruleLatex ?? "???"}/>
          </p>
          {errors.length > 0 ? <hr className="mt-2"/> : null}
          {
            errors.map(error => {
              return <>
                <div className="py-3" key={error.uuid + error.violationType + JSON.stringify(error)}>
                  <DiagnosticMessage diagnostic={error}/>
                </div>
                <hr/>
              </>
            })
          }
        </Card>
      )}
    </div>
  );
}
