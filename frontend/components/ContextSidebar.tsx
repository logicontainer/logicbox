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
import { getSelectedStep, refIsBeingHovered } from "@/lib/state-helpers";
import Link from "next/link";
import { useProof } from "@/contexts/ProofProvider";
import { Button } from "./ui/button";
import { DownloadIcon } from "lucide-react";
import DownloadProofButton from "./DownloadProofButton";

export default function ContextSidebar() {
  const { proof } = useProof();
  const { lines, getReferenceString } = useLines();
  const diagnosticContext = useDiagnostics();
  const { getRuleAtStepAsLatex, getStep } = diagnosticContext;
  const { interactionState } = useInteractionState();

  const stepInFocus = getSelectedStep(interactionState);
  const proofLine = lines.find((line) => line.uuid === stepInFocus);

  const { proofDiagnostics } = useServer();
  const errors = proofDiagnostics.filter((d) => d.uuid === stepInFocus);

  const lineOrBox = proofLine?.stepType === "box" ? "Box" : "Line";
  const refStr = stepInFocus && getReferenceString(stepInFocus);

  const proofStep = stepInFocus !== null ? getStep(stepInFocus) : null;
  const optFreshVarString =
    (proofStep?.stepType === "box"
      ? `Fresh var: ${proofStep.boxInfo.freshVar}`
      : null) ?? "";

  const hoveredRefs =
    stepInFocus && proofStep?.stepType === "line"
      ? proofStep.justification.refs
          .map((_, idx) => idx)
          .filter((idx) =>
            refIsBeingHovered(stepInFocus, idx, interactionState),
          )
      : [];

  const ruleLatex =
    stepInFocus &&
    getRuleAtStepAsLatex(stepInFocus, hoveredRefs, false, "blue");
  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE;

  return (
    <div className="lg:h-screen p-2 overflox-auto">
      <div className="flex flex-col gap-2">
        <Card className="flex items-center justify-start gap-3 py-2">
          <Link href={"/gallery"} title="Go to your proof gallery">
            <div className="flex items-cetner justify-center gap-2 py-2">
              <img className="w-12 h-12" src="/logicbox-icon.svg"></img>
              <h1 className="text-left text-2xl font-bold py-2">LogicBox</h1>
            </div>
          </Link>
          <div className="w-[1px] self-stretch bg-gray-600 my-3"></div>
          <div className="flex justify-between grow items-center">
            <p className="text-xl">{proof.title}</p>
            <DownloadProofButton proofId={proof.id} />
          </div>
        </Card>
        {proofLine && !isEditingRule && (
          <Card>
            <h2 className="text-left text-lg font-bold pb-2">
              {lineOrBox} {refStr} in focus. {optFreshVarString}
            </h2>
            <p className="flex justify-center items-center text-md bg-gray-100 rounded-md py-4 h-32">
              <InlineMath math={ruleLatex ?? "???"} />
            </p>
            {errors.length > 0 ? <hr className="mt-2" /> : null}
            {errors.map((error) => {
              return (
                <div
                  key={error.uuid + error.errorType + JSON.stringify(error)}
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
