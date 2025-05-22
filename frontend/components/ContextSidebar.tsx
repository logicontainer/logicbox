"use client";

import {
  InteractionStateEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import Card from "./Card";
import { DiagnosticMessage } from "./Diagnostics";
import { InlineMath } from "react-katex";
import RulePanel from "./RulePanel";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";

export default function ContextSidebar() {
  const { lineInFocus } = useProof();
  const { lines, getReferenceString } = useLines();
  const { getRuleAtStepAsLatex } = useDiagnostics();
  const { interactionState } = useInteractionState();

  const line = lines.find((line) => line.uuid === lineInFocus);

  const { proofDiagnostics } = useServer();
  const errors = proofDiagnostics.filter((d) => d.uuid === lineInFocus);

  const lineOrBox = line?.stepType === "box" ? "Box" : "Line";
  const refStr = lineInFocus && getReferenceString(lineInFocus);

  const ruleLatex = lineInFocus && getRuleAtStepAsLatex(lineInFocus, [], false);

  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE;

  return (
    <div className="  sm:h-screen p-2">
      {line && (
        <div className="flex flex-col gap-2">
          <Card>
            <h2 className="text-left text-lg font-bold pb-2">
              {lineOrBox} {refStr} in focus
            </h2>
            <p className="flex justify-center items-center text-md bg-gray-100 rounded-md py-4 h-32">
              <InlineMath math={ruleLatex ?? "???"} />
            </p>
            {errors.length > 0 ? <hr className="mt-2" /> : null}
            {errors.map((error) => {
              return (
                <div
                  key={error.uuid + error.violationType + JSON.stringify(error)}
                >
                  <div className="py-3">
                    <DiagnosticMessage diagnostic={error} />
                  </div>
                  <hr />
                </div>
              );
            })}
          </Card>
          {isEditingRule && (
            <Card>
              <RulePanel />
            </Card>
          )}
        </div>
      )}
    </div>
  );
}
