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
import { createHighlightedLatexRule } from "@/lib/rules";
import { useProof } from "@/contexts/ProofProvider";
import { useRuleset } from "@/contexts/RulesetProvider";

export function Justification({
  uuid,
  justification,
  lines,
  onHover,
  onClickRule,
  onClickRef,
}: {
  uuid: string;
  justification: TJustification;
  lines: TLineNumber[];
  onHover: (highlightedLatex: string) => void;
  onClickRule: () => void;
  onClickRef: (idx: number) => void;
}) {
  const { ruleset, rulesetDropdownOptions } = useRuleset();

  const proofContext = useProof();
  const currLineProofStepDetails = proofContext.getProofStepDetails(uuid);

  const { interactionState } = useInteractionState();
  const { doTransition } = useInteractionState();
  const rule = ruleset.rules.find(
    (rule) => rule.ruleName == justification.rule
  );
  if (!rule) return;

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
      {isEditingRule ? (
        <Select
          instanceId={uuid}
          value={rulesetDropdownValue}
          onChange={handleChangeRule}
          menuIsOpen={isEditingRule}
          onMenuOpen={() =>
            doTransition({
              enum: TransitionEnum.CLICK_RULE,
              lineUuid: uuid,
            })
          }
          closeMenuOnSelect={false} // ensure that CLOSE is not sent when we select something
          options={rulesetDropdownOptions}
          theme={dropdownTheme}
          styles={{
            singleValue: (base) => ({
              ...base,
              paddingLeft: "8px",
              paddingRight: "8px",
            }),
            input(base) {
              return {
                ...base,
                paddingLeft: "8px",
                paddingRight: "8px",
              };
            },
          }}
        />
      ) : (
        <span
          className="hover:text-red-500"
          onMouseOver={() =>
            onHover(
              createHighlightedLatexRule(
                rule.latex.ruleName,
                rule.latex.premises,
                rule.latex.conclusion,
                [],
                false
              )
            )
          }
          onClick={(e) => {
            onClickRule();
            e.stopPropagation();
          }}
        >
          <InlineMath math={rule.latex.ruleName}></InlineMath>
        </span>
      )}
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
                  onClick={() =>
                    doTransition({
                      enum: TransitionEnum.CLICK_REF,
                      lineUuid: uuid,
                      refIdx: i,
                    })
                  }
                ></RefSelect>
              );
            } else {
              let refLatex = "";
              const referencedLine = lines.find((line) => line.uuid == ref);
              if (referencedLine?.isBox) {
                refLatex = `${referencedLine.boxStartLine}\\text{-}${referencedLine.boxEndLine}`;
              } else {
                refLatex = JSON.stringify(referencedLine?.lineNumber);
              }

              const comma = i < justification.refs.length - 1 ? "," : "";
              return (
                <span
                  key={i}
                  className="hover:text-red-500"
                  onClick={(e) => {
                    onClickRef(i);
                    e.stopPropagation();
                  }}
                  onMouseOver={() =>
                    onHover(
                      createHighlightedLatexRule(
                        rule.latex.ruleName,
                        rule.latex.premises,
                        rule.latex.conclusion,
                        [i],
                        false
                      )
                    )
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
