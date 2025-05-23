"use client";

import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";
import Select, { SingleValue, Theme } from "react-select";
import {
  Justification as TJustification,
  TLineNumber,
  LineProofStep as TLineProofStep,
} from "@/types/types";

import { InlineMath } from "react-katex";
import { RefSelect } from "./RefSelect";
import { cn } from "@/lib/utils";
import { createHighlightedLatexRule } from "@/lib/rules";
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
  const { ruleset, rulesetDropdownOptions } = useRuleset();

  const proofContext = useProof();
  const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);
  const { setStepInFocus: setLineInFocus } = useProof();

  const { interactionState } = useInteractionState();
  const { doTransition } = useInteractionState();
  const rule = ruleset.rules.find(
    (rule) => rule.ruleName == justification.rule
  );
  const ruleNameLatex = rule?.latex.ruleName;

  if (currLineProofStepDetails?.proofStep.stepType !== "line") {
    return null;
  }

  const currLineProofStep =
    currLineProofStepDetails.proofStep as TLineProofStep;

  const isEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE &&
    interactionState.lineUuid === uuid;

  const rulesetDropdownValue = rulesetDropdownOptions.find(
    (option) => option.value === currLineProofStep.justification.rule
  );

  const handleChangeRule = (
    newValue: SingleValue<{ value: string; label: string }>
  ) => {
    if (newValue == null) {
      return;
    }

    doTransition({
      enum: TransitionEnum.UPDATE_RULE,
      ruleName: newValue.value,
    });
  };

  const dropdownTheme = (theme: Theme) => ({
    ...theme,
    spacing: {
      ...theme.spacing,
      controlHeight: 30,
      baseUnit: 0,
    },
  });
  return (
    <>
      <span
        className={cn(
          isEditingRule ? "bg-blue-400 text-white" : "hover:text-blue-600"
        )}
        onMouseMove={() =>
          doTransition({
            enum: TransitionEnum.HOVER_LINE,
            lineUuid: uuid,
          })
        }
        onClick={(e) => {
          console.log("onClickRule");
          e.stopPropagation();
          setLineInFocus(uuid);
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
                    setLineInFocus(uuid);
                  }}
                  onMouseMove={() =>
                    doTransition({
                      enum: TransitionEnum.HOVER_LINE,
                      lineUuid: uuid,
                    })
                  }
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
/* eslint-disable @typescript-eslint/no-explicit-any */
function CustomMenu(props: any) {
  const { innerProps, innerRef } = props;
  return (
    <div
      ref={innerRef}
      {...innerProps}
      className="bg-white border border-slate-300 rounded-md shadow-lg z-10 overflow-visible"
    >
      {props.children}
    </div>
  );
}

/* eslint-disable @typescript-eslint/no-explicit-any */
function CustomOption(props: any) {
  const { data, innerRef, innerProps } = props;
  return (
    <div
      ref={innerRef}
      {...innerProps}
      className="text-slate-800 hover:bg-slate-100 cursor-pointer px-2"
    >
      <InlineMath math={data?.latexRuleName || ""}></InlineMath>
    </div>
  );
}
/* eslint-disable @typescript-eslint/no-explicit-any */
function CustomValueContainer(props: any) {
  const { getValue, innerRef, innerProps } = props;
  const value = getValue()[0];
  return (
    <div
      ref={innerRef}
      {...innerProps}
      className="flex items-center gap-2 px-2"
    >
      <InlineMath math={value?.latexRuleName || ""}></InlineMath>
    </div>
  );
}
