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
import { useServer } from "@/contexts/ServerProvider";
import { getSelectedStep } from "@/lib/state-helpers";

function SelectRuleSidebar() {

}

export default function ContextSidebar() {
  const { lines, getReferenceString } = useLines();
  const { getRuleAtStepAsLatex } = useDiagnostics();
  const { interactionState } = useInteractionState();

  const stepInFocus = getSelectedStep(interactionState)
  const line = lines.find((line) => line.uuid === stepInFocus);

  const { proofDiagnostics } = useServer();
  const errors = proofDiagnostics.filter((d) => d.uuid === stepInFocus);

  const lineOrBox = line?.stepType === "box" ? "Box" : "Line";
  const refStr = stepInFocus && getReferenceString(stepInFocus);
  const ruleLatex = stepInFocus && getRuleAtStepAsLatex(stepInFocus, [], false);
  const isEditingRule = interactionState.enum === InteractionStateEnum.EDITING_RULE;

  return (
    <div className="  sm:h-screen p-2">
      <div className="flex flex-col gap-2">
        {(line && !isEditingRule) && (
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
        )}
        {isEditingRule && (
          <Card>
            <RulePanel />
          </Card>
        )}
      </div>
    </div>
  );
}
