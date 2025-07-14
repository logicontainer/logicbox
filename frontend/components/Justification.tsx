"use client";

import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";
import { Justification as TJustification, TLineNumber } from "@/types/types";

import { InlineMath } from "react-katex";
import { RefSelect } from "./RefSelect";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";
import { useRuleset } from "@/contexts/RulesetProvider";
import { useHovering } from "@/contexts/HoveringProvider";
import { refIsBeingHovered, ruleIsBeingHovered } from "@/lib/state-helpers";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import {
  DiagnosticHighlight,
  getDiagnosticHighlightForReference,
  getDiagnosticHighlightForRule,
} from "@/lib/proof-step-highlight";

export function Justification({
  uuid,
  justification,
  lines,
  onClickRule,
  onClickRef,
}: {
  uuid: string;
  justification: TJustification;
  lines: TLineNumber[];
  onClickRule: () => void;
  onClickRef: (idx: number) => void;
}) {
  const { rulesets } = useRuleset();
  const { handleHoverStep } = useHovering();
  const diagnosticsContext = useDiagnostics();

  const proofContext = useProof();
  const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);

  const { interactionState } = useInteractionState();
  const { doTransition } = useInteractionState();
  const rule = rulesets
    .map((set) => set.rules)
    .flat()
    .find((rule) => rule.ruleName == justification.rule);

  let ruleNameLatex = rule?.latex.ruleName ?? "???";
  const ruleNameHighlight = getDiagnosticHighlightForRule(
    uuid,
    diagnosticsContext,
  );
  if (ruleNameHighlight === DiagnosticHighlight.YES) {
    ruleNameLatex = `\\underline{${ruleNameLatex}}`;
  }

  if (currLineProofStepDetails?.proofStep.stepType !== "line") {
    return null;
  }

  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE &&
    interactionState.lineUuid === uuid;

  return (
    <>
      <span
        className={cn(
          isEditingRule && "bg-blue-400 text-white",
          ruleIsBeingHovered(uuid, interactionState) && "text-blue-600",
          ruleNameHighlight === DiagnosticHighlight.YES && "text-red-500",
        )}
        onClick={(e) => {
          e.stopPropagation();
          onClickRule();
        }}
        onMouseOver={(e) => {
          e.stopPropagation();
          handleHoverStep(uuid, null, true);
        }}
      >
        <InlineMath math={ruleNameLatex ?? "???"}></InlineMath>
      </span>
      {justification.refs && (
        <>
          <InlineMath math={`\\,`} />
          {justification.refs.map((ref, i) => {
            const isCurrentlyBeingChanged =
              interactionState.enum === InteractionStateEnum.EDITING_REF &&
              interactionState.lineUuid === uuid &&
              interactionState.refIdx === i;

            if (isCurrentlyBeingChanged) {
              return (
                <RefSelect
                  key={i}
                  value={ref}
                  isCurrentlyBeingChanged={
                    interactionState.enum ===
                      InteractionStateEnum.EDITING_REF &&
                    interactionState.lineUuid === uuid &&
                    interactionState.refIdx === i
                  }
                  onClick={(e) => {
                    e.stopPropagation();
                    doTransition({
                      enum: TransitionEnum.CLICK_REF,
                      lineUuid: uuid,
                      refIdx: i,
                    });
                  }}
                ></RefSelect>
              );
            } else {
              let refLatex = "";
              const referencedLine = lines.find((line) => line.uuid == ref);
              if (referencedLine?.stepType === "box") {
                refLatex = `${referencedLine.boxStartLine}\\text{-}${referencedLine.boxEndLine}`;
              } else if (referencedLine?.stepType === "line") {
                refLatex = JSON.stringify(referencedLine?.lineNumber);
              } else {
                refLatex = "?";
              }

              const referenceHighlight = getDiagnosticHighlightForReference(
                uuid,
                i,
                diagnosticsContext,
              );
              if (referenceHighlight === DiagnosticHighlight.YES) {
                refLatex = `\\textbf{\\underline{${refLatex}}}`;
              }

              const comma = i < justification.refs.length - 1 ? "," : "";
              return (
                <span
                  key={i}
                  className={cn(
                    referenceHighlight === DiagnosticHighlight.YES &&
                      "text-red-500",
                    refIsBeingHovered(uuid, i, interactionState) &&
                      "text-blue-600",
                  )}
                  onClick={(e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    onClickRef(i);
                  }}
                  onMouseOver={(e) => {
                    e.stopPropagation();
                    handleHoverStep(uuid, i, false);
                  }}
                >
                  <InlineMath math={`${refLatex}${comma}`} />
                </span>
              );
            }
          })}
        </>
      )}
    </>
  );
}
