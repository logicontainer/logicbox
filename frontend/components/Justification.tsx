"use client";

import {
    HoveringEnum,
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
  const { hoveringState, handleHover } = useHovering();
  const diagnosticsContext = useDiagnostics();

  const proofContext = useProof();
  const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);

  const { interactionState } = useInteractionState();
  const rule = rulesets
    .map((set) => set.rules)
    .flat()
    .find((rule) => rule.ruleName == justification.rule);

  let ruleNameLatex = rule?.latex.ruleName ?? "???";
  const ruleNameHighlight = getDiagnosticHighlightForRule(
    uuid,
    diagnosticsContext,
  );

  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE &&
    interactionState.lineUuid === uuid;

  if (isEditingRule) {
    ruleNameLatex = `\\boxed{${ruleNameLatex}}`
  }
  else if (ruleNameHighlight === DiagnosticHighlight.YES) {
    ruleNameLatex = `\\underline{${ruleNameLatex}}`;
  }

  if (currLineProofStepDetails?.proofStep.stepType !== "line") {
    return null;
  }

  return (
    <>
      <span
        className={cn(
          (isEditingRule || ruleIsBeingHovered(uuid, hoveringState)) && "text-blue-600",
          ruleNameHighlight === DiagnosticHighlight.YES && "text-red-500",
        )}
        onClick={(e) => {
          e.stopPropagation();
          onClickRule();
        }}
        onMouseMove={(e) => {
          e.stopPropagation();
          handleHover({ enum: HoveringEnum.HOVERING_RULE, stepUuid: uuid });
        }}
      >
        <InlineMath math={ruleNameLatex ?? "???"}></InlineMath>
      </span>
      {justification.refs && (
        <>
          <InlineMath math={`\\,`} />
          {justification.refs.map((ref, i) => {
              let refLatex = "";
              const referencedLine = lines.find((line) => line.uuid == ref);
              if (referencedLine?.stepType === "box") {
                refLatex = `${referencedLine.boxStartLine}\\text{-}${referencedLine.boxEndLine}`;
              } else if (referencedLine?.stepType === "line") {
                refLatex = JSON.stringify(referencedLine?.lineNumber);
              } else {
                refLatex = "?";
              }

              const diagnosticHighlight = getDiagnosticHighlightForReference(uuid, i, diagnosticsContext);
              const isCurrentlyBeingChanged =
                interactionState.enum === InteractionStateEnum.EDITING_REF &&
                interactionState.lineUuid === uuid &&
                interactionState.refIdx === i;
              if (isCurrentlyBeingChanged) {
                refLatex = `\\boxed{${refLatex}}`
              } else if (diagnosticHighlight === DiagnosticHighlight.YES) {
                refLatex = `\\textbf{\\underline{${refLatex}}}`;
              }

              const comma = i < justification.refs.length - 1 ? "," : "";
              return (
                <span
                  key={i}
                  className={cn(
                    diagnosticHighlight === DiagnosticHighlight.YES &&
                      "text-red-500",
                    refIsBeingHovered(uuid, i, hoveringState) &&
                      "text-blue-600",
                  )}
                  onClick={(e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    onClickRef(i);
                  }}
                  onMouseMove={(e) => {
                    e.stopPropagation()
                    handleHover({
                      enum: HoveringEnum.HOVERING_REF,
                      stepUuid: uuid,
                      refIdx: i,
                    });
                  }}
                >
                  <InlineMath math={`${refLatex}${comma}`} />
                </span>
              );
            }
          )}
        </>
      )}
    </>
  );
}
