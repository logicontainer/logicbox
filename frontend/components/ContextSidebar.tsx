"use client";

import {
  InteractionStateEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import Card from "./Card";
import { InlineMath } from "react-katex";
import RulePanel from "./RulePanel";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { useLines } from "@/contexts/LinesProvider";
import { useServer } from "@/contexts/ServerProvider";
import { formulaIsBeingHovered, getSelectedStep, refIsBeingHovered } from "@/lib/state-helpers";
import Link from "next/link";
import { useProof } from "@/contexts/ProofProvider";
import DownloadProofButton from "./DownloadProofButton";
import { DiagnosticsPanel } from "./DiagnosticsPanel";
import { useHovering } from "@/contexts/HoveringProvider";
import { BoxProofStep, LineProofStep } from "@/types/types";
import { Label } from "./ui/label";

function LineFocusPanel({
  lineUuid,
  lineStep,
}: {
  lineUuid: string,
  lineStep: LineProofStep,
}) {
  const { getReferenceString } = useLines()
  const { getRuleAtStepAsLatex } = useDiagnostics()
  const { hoveringState } = useHovering()

  const refHighlights = lineStep.justification.refs
    .map((_, idx) => idx)
    .filter((idx) =>
      refIsBeingHovered(lineUuid, idx, hoveringState),
    )

  const refLineNumbers = lineStep.justification.refs
    .map(uuid => getReferenceString(uuid))
    .map(n => n === null ? "?" : n)
    .map(n => n.replace("-", "\\text{-}"))

  const ruleLatex = lineUuid && getRuleAtStepAsLatex(lineUuid, refHighlights, formulaIsBeingHovered(lineUuid, hoveringState), "blue");

  const { proofDiagnostics } = useServer();
  const errors = proofDiagnostics.filter((d) => d.uuid === lineUuid);

  return <Card className="w-full min-h-40">
    <div className="h-32 grid grid-cols-[1fr_2fr]">
      <div className="overflow-x-hidden flex flex-col gap-4">
        <Label className="text-lg">
          <InlineMath math={"\\textbf{Line }\\mathbf{" + (getReferenceString(lineUuid) ?? "???") + "}"}/>
        </Label>
        {lineStep.justification.refs.length === 0 ? [] : 
          <Label>References: <span className="text-xs"
          ><InlineMath math={refLineNumbers.join(", ")}/></span></Label>
        }
      </div>
      <div className="flex justify-center items-center text-md border border-black rounded-sm">
        <InlineMath math={ruleLatex ?? "???"} />
      </div>
    </div>
    {errors.length > 0 ? <hr className="mt-2"/> : null}
    <DiagnosticsPanel diagnostics={errors}/>
  </Card>
}

function BoxFocusPanel({
  boxUuid,
  boxStep,
}: {
  boxUuid: string,
  boxStep: BoxProofStep
}) {
  const { getReferenceString } = useLines()

  return <Card className="w-full h-40">
    <div className="overflow-x-hidden flex flex-col gap-4">
      <Label className="text-lg">
        <InlineMath math={"\\textbf{Box " + (getReferenceString(boxUuid) ?? "???") + "}"}/>
      </Label>
      <Label>
        {boxStep.boxInfo.freshVar ? <>
          Fresh variable: <InlineMath math={boxStep.boxInfo.freshVar}/>
        </> : null}
      </Label>
    </div>
  </Card>
}

export default function ContextSidebar() {
  const { proof } = useProof();
  const diagnosticContext = useDiagnostics();
  const { getStep } = diagnosticContext;
  const { interactionState } = useInteractionState();

  const stepInFocus = getSelectedStep(interactionState);


  const proofStep = stepInFocus !== null ? getStep(stepInFocus) : null;

  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE;

  return (
    <div className="lg:h-screen p-2 overflow-auto">
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
          {!isEditingRule && stepInFocus && proofStep?.stepType === "line" && <>
            <LineFocusPanel lineUuid={stepInFocus} lineStep={proofStep}/>
          </>}
          {!isEditingRule && stepInFocus && proofStep?.stepType === "box" && (
            <BoxFocusPanel boxUuid={stepInFocus} boxStep={proofStep}/>
          )}
          {isEditingRule && <RulePanel />}
      </div>
    </div>
  );
}
