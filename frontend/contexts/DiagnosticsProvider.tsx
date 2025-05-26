"use client";

import { Diagnostic, ProofStep} from "@/types/types";
import React from "react";

import _ from "lodash";
import { useLines } from "./LinesProvider";
import { useProof } from "./ProofProvider";
import { useRuleset } from "./RulesetProvider";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useServer } from "./ServerProvider";

export interface DiagnosticsContextProps {
  diagnostics: Diagnostic[]

  getStep: (uuid: string) => ProofStep | null;
  getStepAsLatex: (uuid: string) => string | null
  getStepAsLatexWithTag: (uuid: string) => string | null
  getRefLatex: (stepUuid: string, refIdx: number) => string | null
  getRefString: (stepUuid: string, refIdx: number) => string | null
  getRefLatexWithTag: (stepUuid: string, refIdx: number) => string | null
  getRuleAtStepAsLatex: (rule: string, highlightedPremises: number[], conclusionIsHighlighted: boolean, highlightColor: string) => string | null
  getRuleNameAtStepAsLatex: (rule: string) => string | null
}

// Context Setup
const DiagnosticsContext = React.createContext<DiagnosticsContextProps | null>(null);

export function useDiagnostics() {
  const context = React.useContext(DiagnosticsContext);
  if (!context) {
    throw new Error("useLines must be used within a DiagnosticsProvider");
  }
  return context;
}

export function DiagnosticsProvider({ children }: React.PropsWithChildren<{}>) {
  const { proofDiagnostics: diagnostics } = useServer()
  const { getReferenceString } = useLines()
  const { getProofStepDetails } = useProof()
  const { rulesets } = useRuleset()
  const allRules = rulesets.map(s => s.rules).flat()

  const getStep = (stepUuid: string) => {
    return getProofStepDetails(stepUuid)?.proofStep ?? null
  }

  const getStepAsLatex: (_: string) => string | null = (stepUuid: string) => {
    const step = getStep(stepUuid)
    if (!step)
      return null

    if (step.stepType === "line") {
      return step.formula.latex
    } else if (step.stepType === "box") {
      if (step.proof.length === 0)
        return "EMPTY BOX"

      const ass = getStepAsLatex(step.proof[0].uuid) ?? "???"
      const concl = getStepAsLatex(step.proof[step.proof.length - 1].uuid) ?? "???"

      return `\\begin{array}{|c|} \\hline ${ass} \\\\ \\vdots \\\\ ${concl} \\\\ \\hline \\end{array}`
    } else {
      // assert unreachable case
      step satisfies never
      return null
    }
  }

  const getStepAsLatexWithTag: (_: string) => string | null = (stepUuid: string) => {
    const step = getStep(stepUuid)
    return step && `${getStepAsLatex(stepUuid)}\\quad (\\text{${step.stepType}}\\; \\text{${getReferenceString(stepUuid)}})`
  }

  const getRefId = (stepUuid: string, refIdx: number) => {
    const step = getStep(stepUuid)

    if (step?.stepType !== "line")
      return null

    return step.justification.refs.at(refIdx) ?? null
  }

  const getRefLatex = (stepUuid: string, refIdx: number) => {
    const refId = getRefId(stepUuid, refIdx)
    return refId && getStepAsLatex(refId)
  }

  const getRefString = (stepUuid: string, refIdx: number) => {
    const refId = getRefId(stepUuid, refIdx)
    if (!refId) return null
    const refStep = getStep(refId)
    if (!refStep) return null
    return getReferenceString(refId)
  }

  const getRefLatexWithTag = (stepUuid: string, refIdx: number, withAlignMarkAtRef: boolean = false) => {
    const refId = getRefId(stepUuid, refIdx)
    const refStep = refId && getStep(refId)
    return refStep && `${getRefLatex(stepUuid, refIdx)}\\quad ${withAlignMarkAtRef ? "&&" : ""}(\\text{${refStep.stepType}}\\; \\text{${getRefString(stepUuid, refIdx)}})`
  }

  const getRuleNameAtStepAsLatex = (stepUuid: string) => {
    const step = getStep(stepUuid)
    if (!step || step.stepType !== "line")
      return null
    return allRules.filter(s => s.ruleName === step.justification.rule).map(s => s.latex.ruleName).at(0) ?? null
  }

  const getRuleAtStepAsLatex = (stepUuid: string, highlightedPremises: number[], conclusionIsHighlighted: boolean, highlightColor: string) => {
    const step = getStep(stepUuid)
    if (!step || step.stepType !== "line")
      return null
    return allRules
      .filter(s => s.ruleName === step.justification.rule)
      .map(s => createHighlightedLatexRule(s.latex.ruleName, s.latex.premises, s.latex.conclusion, highlightedPremises, conclusionIsHighlighted, highlightColor))
      .at(0) ?? null
  }

  return (
    <DiagnosticsContext.Provider
      value={{
        diagnostics,
        getStep,
        getRefString,
        getRefLatex,
        getStepAsLatex,
        getStepAsLatexWithTag,
        getRefLatexWithTag,
        getRuleAtStepAsLatex,
        getRuleNameAtStepAsLatex,
      }}
    >
      {children}
    </DiagnosticsContext.Provider>
  );
}
