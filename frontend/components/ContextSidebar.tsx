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
import { BoxProofStep, LineProofStep,  ProofWithMetadata, Rule } from "@/types/types";
import { Label } from "./ui/label";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useRuleset } from "@/contexts/RulesetProvider";
import React from "react";
import { CaretLeftIcon, CaretRightIcon } from "@radix-ui/react-icons";

import { Toolbar } from "radix-ui";
import { useHistory } from "@/contexts/HistoryProvider";
import ProofValidityIcon from "./ProofValidityIcon";
import { ButtonGroup } from "./ui/button-group";
import { Button } from "./ui/button";
import { cn } from "@/lib/utils";
import { logicNameToString } from "./GalleryItem";
import { createSequentLaTeX } from "@/lib/sequent";

import { Tooltip } from 'react-tooltip'

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

  return <div className="w-full min-h-40">
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
  </div>
}

function BoxFocusPanel({
  boxUuid,
  boxStep,
}: {
  boxUuid: string,
  boxStep: BoxProofStep
}) {
  const { getReferenceString } = useLines()

  return <div className="w-full h-40">
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
  </div>
}

function RulePanel({ shouldShowRuleTooltip }: { shouldShowRuleTooltip: boolean }) {
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
      className="RULE_ELEMENT flex items-center justify-center gap-1 p-2 border border-gray-300 rounded-md cursor-pointer hover:bg-gray-100"
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

  return <div className="w-full flex flex-col gap-5">
    <div className="grid grid-cols-3 gap-2">
      {first}
      <div className="col-span-2 row-span-3">
        <RuleShowPanel ruleLatex={hoveredRuleDetailsLatex} />
      </div>
      {rest}
    </div>
    <Tooltip 
      anchorSelect=".RULE_ELEMENT" 
      delayHide={0} 
      variant="dark" 
      place="right"
      className={(!shouldShowRuleTooltip || hoveredRule === null) && "hidden" || undefined}
    >
      <InlineMath math={hoveredRuleDetailsLatex}/>
    </Tooltip>
  </div>
}

function ProofEditorToolbar({ proof }: { proof: ProofWithMetadata }) {
  const { undo, redo, canUndo, canRedo } = useHistory()

  const [sequentIsVisible, setSequentVisbility] = React.useState<boolean>(false)

  return <div
    onMouseEnter={_ => setSequentVisbility(true)}
    onMouseLeave={_ => setSequentVisbility(false)}
  >
    <Card className={cn(
      "flex flex-col gap-0",
      "px-2 py-0",
      sequentIsVisible && "bg-accent"
    )}>
      <div className="flex items-center justify-between gap-1 py-2">
        <div className="flex items-center gap-2 md:gap-3">
          <Link href={"/gallery"} title="Go to your proof gallery">
            <img className="w-12 h-12" src="/logicbox-icon.svg"></img>
          </Link>
          <div className="w-[1px] self-stretch bg-gray-600 my-1"></div>
          <div className="flex justify-between items-center overflow-scroll">
            <div className="flex flex-col items-start">
              <p className="text md:text-xl text-clip text-nowrap">{proof.title}</p>
              <p className="text-xs md:text-sm font-light text-clip text-nowrap">{logicNameToString(proof.logicName)}</p>
            </div>
          </div>
        </div>
        <Toolbar.Root
          className="flex gap-1 md:gap-3 items-center"
          aria-label="Formatting options"
        >
          <Toolbar.ToolbarButton className="cursor-auto">
            <ProofValidityIcon />
          </Toolbar.ToolbarButton>
          <DownloadProofButton className="hidden md:flex items-center h-full" proofId={proof.id} />

          <ButtonGroup className="flex items-center">
            <Button
              variant={"outline"}
              title="Undo latest action"
              onClick={undo}
              disabled={!canUndo}
              className="py-0"
            >
              <CaretLeftIcon className="w-20 h-20" />
            </Button>
            <Button
              variant={"outline"}
              title="Redo latest action"
              onClick={redo}
              disabled={!canRedo}
            >
              <CaretRightIcon className="w-8 h-8" />
            </Button>
          </ButtonGroup>

        </Toolbar.Root>
      </div>
      {sequentIsVisible && <>
        <hr/>
        <div className={cn(
          !sequentIsVisible && "hidden",
          "py-1 flex items-center justify-center text-sm"
        )}>
          <InlineMath math={createSequentLaTeX(proof.proof) ?? "???"}/>
        </div>
      </>}
    </Card>
  </div>
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

  const showLineFocusPanel = !isEditingRule && stepInFocus && proofStep?.stepType === "line";
  const showBoxFocusPanel = !isEditingRule && stepInFocus && proofStep?.stepType === "box";
  const showRulePanel = isEditingRule;
  const noPanelIsShown = !(showLineFocusPanel || showBoxFocusPanel || showRulePanel)

  const scrollAreaRef = React.useRef<HTMLDivElement>(null)
  const [shouldShowRuleTooltip, setShouldShowTooltip] = React.useState<boolean>(false);

  // TODO: this could probably be made better by listening to resize events or something
  React.useEffect(() => {
    const value = scrollAreaRef.current?.scrollHeight !== scrollAreaRef.current?.clientHeight
    if (shouldShowRuleTooltip !== value) {
      setShouldShowTooltip(value)
    }
  }, [scrollAreaRef.current?.scrollHeight, scrollAreaRef.current?.clientHeight])

  return (
    <div className="lg:h-screen p-2"> 
      <div className="flex flex-col gap-2">
        <ProofEditorToolbar proof={proof}/>
        <Card ref={scrollAreaRef} className={cn("max-h-48 md:max-h-max overflow-scroll", noPanelIsShown && "min-h-48 h-12")}>
          {showLineFocusPanel && <>
            <LineFocusPanel lineUuid={stepInFocus} lineStep={proofStep} />
          </>}
          {showBoxFocusPanel && (
            <BoxFocusPanel boxUuid={stepInFocus} boxStep={proofStep} />
          )}
          {showRulePanel && <RulePanel shouldShowRuleTooltip={shouldShowRuleTooltip}/>}
          {noPanelIsShown && <div className="flex items-center justify-center w-full h-full">
            <p className="text-sm font-light text-gray-600">Interact with the proof to inspect context here.</p>
          </div>}
        </Card>
      </div>
    </div>
  );
}
