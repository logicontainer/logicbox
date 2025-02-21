import "katex/dist/katex.min.css";

import { LineNumberLine, LineProofStep as TLineProofStep } from "@/types/types";
import Select, { SingleValue, Theme } from 'react-select'

import { AddLineTooltip } from "./AddLineTooltip";
import AutosizeInput from 'react-input-autosize';
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import { UpdateLineProofStepCommand } from "@/lib/commands";
import { cn } from "@/lib/utils";
import { useContextMenu } from "react-contexify";
import { useHistory } from "@/contexts/HistoryProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useRuleset } from "@/contexts/RulesetProvider";
import { useState } from "react";

export function LineProofStep ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { isActiveEdit } = useProof();
  const isTheActiveEdit = isActiveEdit(props.uuid)
  return (
    isTheActiveEdit ? (
      <LineProofStepEdit {...props} />
    ) : (
      <LineProofStepView {...props} />
    )
  );
}


export function LineProofStepView ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { setLineInFocus, isFocused, isActiveEdit, setActiveEdit, removeIsActiveEditFromLine } = useProof();
  const [tooltipContent, setTooltipContent] = useState<string>()
  const isInFocus = isFocused(props.uuid)
  const isTheActiveEdit = isActiveEdit(props.uuid)

  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex)
  }
  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });
  function handleContextMenu (event: React.MouseEvent<HTMLElement> | React.TouchEvent<HTMLElement> | React.KeyboardEvent<HTMLElement> | KeyboardEvent) {
    show({
      event,
      props: {
        uuid: props.uuid,
      }
    })
  }
  return (
    (<div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors", isInFocus ? "text-blue-400" : "")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={() => isTheActiveEdit ? removeIsActiveEditFromLine(props.uuid) : setActiveEdit(props.uuid)}
      onContextMenuCapture={handleContextMenu}
    >
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} prepend />
      <p className="shrink">
        {props.formulaUnsynced ? props.formula : (<InlineMath math={props.latexFormula} />)}
      </p>
      <div data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}>
        <Justification justification={props.justification} lines={props.lines} onHover={handleOnHoverJustification} />
      </div>
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} />
    </div>)
  );
}


export function LineProofStepEdit ({ ...props }: TLineProofStep & { lines: LineNumberLine[] }) {
  const { setLineInFocus, isActiveEdit, removeIsActiveEditFromLine } = useProof();
  const [tooltipContent] = useState<string>()
  const proofContext = useProof();
  const rulesetContext = useRuleset();
  const historyContext = useHistory();
  const isTheActiveEdit = isActiveEdit(props.uuid)
  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });

  const currLineProofStepDetails = proofContext.getProofStepDetails(props.uuid)
  if (currLineProofStepDetails?.proofStep.stepType !== "line") { return null; }
  const currLineProofStep = currLineProofStepDetails.proofStep as TLineProofStep

  const rulesetDropdownValue = rulesetContext.rulesetDropdownOptions.find(option => option.value === currLineProofStep.justification.ruleName)

  const handleChangeRule = (newValue: SingleValue<{ value: string; label: string; }>) => {
    if (newValue == null) { return; }

    const numPremises = rulesetContext.ruleset.rules.find(rule => rule.ruleName === newValue.value)!.numPremises
    let newRefs = currLineProofStep.justification.refs
    console.log("numPremises", numPremises, "newRefs", newRefs)
    if (numPremises > newRefs.length) {
      newRefs = newRefs.concat(Array(numPremises - newRefs.length).fill("?"))
    } else {
      newRefs = newRefs.slice(0, numPremises)
    }

    const updatedLineProofStep: TLineProofStep = {
      ...currLineProofStep,
      justification: {
        ruleName: newValue.value,
        refs: newRefs
      }
    }
    const updateLineCommand = new UpdateLineProofStepCommand(props.uuid, updatedLineProofStep);
    historyContext.addToHistory(updateLineCommand)
  }

  const handleChangeFormula = (event: React.ChangeEvent<HTMLInputElement>) => {
    const updatedLineProofStep: TLineProofStep = {
      ...currLineProofStep,
      formula: event.target.value,
      formulaUnsynced: true,
    }
    const updateLineCommand = new UpdateLineProofStepCommand(props.uuid, updatedLineProofStep);
    historyContext.addToHistory(updateLineCommand)
  }

  const handleChangeRef = (event: React.ChangeEvent<HTMLInputElement>, index: number) => {
    const newRefs = currLineProofStep.justification.refs.map((ref, i) => {
      if (i === index) {
        return event.target.value
      } else {
        return ref
      }
    })
    const updatedLineProofStep: TLineProofStep = {
      ...currLineProofStep,
      justification: {
        ruleName: currLineProofStep.justification.ruleName,
        refs: newRefs
      }
    }
    const updateLineCommand = new UpdateLineProofStepCommand(props.uuid, updatedLineProofStep);
    historyContext.addToHistory(updateLineCommand)
  }

  function handleContextMenu (event: React.MouseEvent<HTMLElement> | React.TouchEvent<HTMLElement> | React.KeyboardEvent<HTMLElement> | KeyboardEvent) {
    show({
      event,
      props: {
        uuid: props.uuid,
      }
    })
  }

  const dropdownTheme = (theme: Theme) => ({
    ...theme,
    spacing: {
      ...theme.spacing,
      controlHeight: 30,
      baseUnit: 0,
    }
  });
  return (
    (<div
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors items-stretch border-blue-400 border-2")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={(e) => {
        if (e.target !== e.currentTarget) { return; }
        return isTheActiveEdit && removeIsActiveEditFromLine(props.uuid)
      }}
      onContextMenuCapture={handleContextMenu}
    >
      <AutosizeInput title="Enter a formula" type="text" value={props.formula} onChange={handleChangeFormula} className="text-slate-800 grow resize shrink" inputClassName="px-2" />
      <div data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}
        title="Select a rule"
        className="flex items-center gap-2 whitespace-nowrap">
        <Select value={rulesetDropdownValue} onChange={handleChangeRule} options={rulesetContext.rulesetDropdownOptions} theme={dropdownTheme} styles={{
          singleValue: (base) => ({ ...base, paddingLeft: "8px", paddingRight: "8px" }),
          input (base) {
            return {
              ...base,
              paddingLeft: "8px",
              paddingRight: "8px",
            }
          },
        }} />
        {currLineProofStep.justification.refs.length > 0 && (
          <div className="flex gap-2">
            {currLineProofStep.justification.refs.map((ref, index) => (
              <AutosizeInput key={index} inputClassName="bg-blue-200 text-blue-800 px-2 rounded text-sm/loose " value={ref} onChange={(e) => handleChangeRef(e, index)} />
            ))}
          </div>
        )}
      </div>
    </div>)
  );
}
