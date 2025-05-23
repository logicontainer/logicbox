"use client";

import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";
import {
  Justification as TJustification,
  TLineNumber,
} from "@/types/types";

import { InlineMath } from "react-katex";
import { RefSelect } from "./RefSelect";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";
import { useRuleset } from "@/contexts/RulesetProvider";

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
  const { ruleset } = useRuleset();

  const proofContext = useProof();
  const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);

  const { interactionState } = useInteractionState();
  const { doTransition } = useInteractionState();
  const rule = ruleset.rules.find(
    (rule) => rule.ruleName == justification.rule
  );
  const ruleNameLatex = rule?.latex.ruleName;

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
          isEditingRule ? "bg-blue-400 text-white" : "hover:text-blue-600"
        )}
        onClick={(e) => {
          e.stopPropagation();
          onClickRule();
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
              } else {
                refLatex = JSON.stringify(referencedLine?.lineNumber);
              }

              const comma = i < justification.refs.length - 1 ? "," : "";
              return (
                <span
                  key={i}
                  className="hover:text-slate-600"
                  onClick={(e) => {
                    e.stopPropagation();
                    e.preventDefault();
                    onClickRef(i);
                  }}
                >
                  <InlineMath math={`${refLatex || "?"}${comma}`} />
                </span>
              );
            }
          })}
        </>
      )}
    </>
  );
}
