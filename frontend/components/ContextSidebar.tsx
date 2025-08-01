"use client";

import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import Card from "./Card";
import { InlineMath } from "react-katex";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { useLines } from "@/contexts/LinesProvider";
import { useServer } from "@/contexts/ServerProvider";
import { formulaIsBeingHovered, getSelectedStep, refIsBeingHovered } from "@/lib/state-helpers";
import Link from "next/link";
import { useProof } from "@/contexts/ProofProvider";
import DownloadProofButton from "./DownloadProofButton";
import { DiagnosticsPanel } from "./DiagnosticsPanel";
import { useHovering } from "@/contexts/HoveringProvider";
import { BoxProofStep, LineProofStep, Rule } from "@/types/types";
import { Label } from "./ui/label";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useRuleset } from "@/contexts/RulesetProvider";
import React from "react";
import { CaretLeftIcon, CaretRightIcon } from "@radix-ui/react-icons";

import { Toolbar } from "radix-ui";
import { useHistory } from "@/contexts/HistoryProvider";
import ProofValidityIcon from "./ProofValidityIcon";
import { useProofStore } from "@/store/proofStore";
import { ButtonGroup } from "./ui/button-group";
import { Button } from "./ui/button";
function RuleShowPanel({
  ruleLatex
}: {
  ruleLatex: string
}) {
  return <div className="w-full h-full flex justify-center items-center text-md border border-black rounded-sm">
    <InlineMath math={ruleLatex} />
  </div>
}

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

  const ruleLatex = getRuleAtStepAsLatex(lineUuid, refHighlights, formulaIsBeingHovered(lineUuid, hoveringState), "blue") ?? "???";

  const { proofDiagnostics } = useServer();
  const errors = proofDiagnostics.filter((d) => d.uuid === lineUuid);

  return <Card className="w-full min-h-40">
    <div className="h-32 grid grid-cols-[1fr_2fr]">
      <div className="overflow-x-hidden flex flex-col gap-4">
        <Label className="text-lg">
          <InlineMath math={"\\textbf{Line }\\mathbf{" + (getReferenceString(lineUuid) ?? "???") + "}"} />
        </Label>
        {lineStep.justification.refs.length === 0 ? [] :
          <Label>References: <span className="text-xs">
            <InlineMath math={refLineNumbers.join(", ")} />
          </span></Label>
        }
      </div>
      <RuleShowPanel ruleLatex={ruleLatex} />
    </div>
    {errors.length > 0 ? <hr className="mt-2" /> : null}
    <DiagnosticsPanel diagnostics={errors} />
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
        <InlineMath math={"\\textbf{Box " + (getReferenceString(boxUuid) ?? "???") + "}"} />
      </Label>
      <Label>
        {boxStep.boxInfo.freshVar ? <>
          Fresh variable: <InlineMath math={boxStep.boxInfo.freshVar} />
        </> : null}
      </Label>
    </div>
  </Card>
}

function RulePanel() {
  const { rulesets } = useRuleset();
  const { doTransition } = useInteractionState();
  const [hoveredRule, setHoveredRule] = React.useState<string | null>(null);

  const allRules = rulesets.map((s) => s.rules).flat();
  const hoveredRuleDetails = allRules.find(
    (rule) => rule.ruleName === hoveredRule,
  );

  const hoveredRuleDetailsLatex = hoveredRuleDetails
    ? createHighlightedLatexRule(
      hoveredRuleDetails.latex.ruleName,
      hoveredRuleDetails.latex.premises,
      hoveredRuleDetails.latex.conclusion,
      [],
      false,
    )
    : "";

  const handleChangeRule = (ruleName: string) => {
    if (ruleName == null) {
      return;
    }
    doTransition({
      enum: TransitionEnum.UPDATE_RULE,
      ruleName,
    });
  };

  const createRuleElement = (rule: Rule) => (
    <div
      key={rule.ruleName}
      className="flex items-center justify-center gap-1 p-2 border border-gray-300 rounded-md cursor-pointer hover:bg-gray-100"
      onMouseOver={() => setHoveredRule(rule.ruleName)}
      onMouseLeave={() => setHoveredRule(null)}
      onClick={() => handleChangeRule(rule.ruleName)}
    >
      <h3 className="text">
        <InlineMath math={rule.latex.ruleName}></InlineMath>
      </h3>
      <p className="text-sm text-gray-600"></p>
    </div>
  )

  const [first, ...rest] = allRules.map(createRuleElement)

  return <Card className="w-full flex flex-col gap-5">
    <div className="grid grid-cols-3 gap-2">
      {first}
      <div className="col-span-2 row-span-3">
        <RuleShowPanel ruleLatex={hoveredRuleDetailsLatex} />
      </div>
      {rest}
    </div>
  </Card>
}

export default function ContextSidebar() {
  const { proof } = useProof();
  const diagnosticContext = useDiagnostics();
  const { getStep } = diagnosticContext;
  const { interactionState, doTransition } = useInteractionState();
  const historyContext = useHistory();
  const proofs = useProofStore((state) => state.proofs);

  const stepInFocus = getSelectedStep(interactionState);

  const proofStep = stepInFocus !== null ? getStep(stepInFocus) : null;

  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE;

  const handleUndo = () => {
    doTransition({ enum: TransitionEnum.UNDO })
  };
  const handleRedo = () => {
    doTransition({ enum: TransitionEnum.REDO })
  };

  return (
    <div className="lg:h-screen p-2 overflow-auto">
      <div className="flex flex-col gap-2">
        <Card className="flex items-center justify-between gap-1 py-2">
          <div className="flex items-center gap-3">
            <Link href={"/gallery"} title="Go to your proof gallery">
              <img className="w-12 h-12" src="/logicbox-icon.svg"></img>
            </Link>
            <div className="w-[1px] self-stretch bg-gray-600 my-1"></div>
            <div className="flex justify-between items-center overflow-scroll">
              <div className="flex flex-col items-start">
                <p className="text-xl text-clip text-nowrap">{proof.title}</p>
                <p className="text-sm font-light text-clip text-nowrap">{proof.title}</p>
              </div>
            </div>
          </div>
          <Toolbar.Root
            className="flex gap-3"
            aria-label="Formatting options"
          >
            <Toolbar.ToolbarButton className="cursor-auto">
              <ProofValidityIcon />
            </Toolbar.ToolbarButton>
            <DownloadProofButton className={"flex"} proofId={proof.id} />

            <ButtonGroup className="flex items-center">
              <Button
                variant={"outline"}
                title="Undo latest action"
                onClick={handleUndo}
                disabled={!historyContext.canUndo}
                className="py-0"
              >
                <CaretLeftIcon className="w-20 h-20" />
              </Button>
              <Button
                variant={"outline"}
                title="Redo latest action"
                onClick={handleRedo}
                disabled={!historyContext.canRedo}
              >
                <CaretRightIcon className="w-8 h-8" />
              </Button>
            </ButtonGroup>

          </Toolbar.Root>
        </Card>
        {!isEditingRule && stepInFocus && proofStep?.stepType === "line" && <>
          <LineFocusPanel lineUuid={stepInFocus} lineStep={proofStep} />
        </>}
        {!isEditingRule && stepInFocus && proofStep?.stepType === "box" && (
          <BoxFocusPanel boxUuid={stepInFocus} boxStep={proofStep} />
        )}
        {isEditingRule && <RulePanel />}
      </div>
    </div>
  );
}
