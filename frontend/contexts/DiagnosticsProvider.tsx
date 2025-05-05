"use client";

import { Diagnostic} from "@/types/types";
import React from "react";

import _ from "lodash";
import { useLines } from "./LinesProvider";
import { useProof } from "./ProofProvider";
import { useRuleset } from "./RulesetProvider";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useServer } from "./ServerProvider";

export interface DiagnosticsContextProps {
  diagnostics: Diagnostic[]

  getStepAsLatex: (uuid: string) => string | null
  getStepAsLatexWithTag: (uuid: string) => string | null
  getRefLatex: (stepUuid: string, refIdx: number) => string | null
  getLatexForMultipleRefs: (stepUuid: string, refs: number[]) => string | null
  getRefString: (stepUuid: string, refIdx: number) => string | null
  getRefLatexWithTag: (stepUuid: string, refIdx: number) => string | null
  getRuleAtStepAsLatex: (rule: string, highlightedPremises: number[], conclusionIsHighlighted: boolean) => string | null
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
  const { ruleset } = useRuleset()

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

  const getLatexForMultipleRefs = (stepUuid: string, refs: number[]) => {
    const refLatexes = refs.map(r => getRefLatexWithTag(stepUuid, r, true))
    if (refLatexes.find(s => s === null) !== undefined)
      return null
    return `\\begin{aligned}${refLatexes.map(l => `&${l}`).join(' \\\\ ')}\\end{aligned}`
  }

  const getRuleNameAtStepAsLatex = (stepUuid: string) => {
    const step = getStep(stepUuid)
    if (!step || step.stepType !== "line")
      return null
    return ruleset.rules.filter(s => s.ruleName === step.justification.rule).map(s => s.latex.ruleName).at(0) ?? null
  }

  const getRuleAtStepAsLatex = (stepUuid: string, highlightedPremises: number[] = [], conclusionIsHighlighted: boolean = false) => {
    const step = getStep(stepUuid)
    if (!step || step.stepType !== "line")
      return null
    return ruleset.rules
      .filter(s => s.ruleName === step.justification.rule)
      .map(s => createHighlightedLatexRule(s.latex.ruleName, s.latex.premises, s.latex.conclusion, highlightedPremises, conclusionIsHighlighted))
      .at(0) ?? null
  }

  return (
    <DiagnosticsContext.Provider
      value={{
        diagnostics,
        getRefString,
        getRefLatex,
        getLatexForMultipleRefs,
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
