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
  const { setLineInFocus, isFocused } = useProof();
  const [tooltipContent, setTooltipContent] = useState<string>()
  const isInFocus = isFocused(props.uuid)
  const proofContext = useProof();
  const rulesetContext = useRuleset();
  const historyContext = useHistory();


  const currLineProofStepDetails = proofContext.getProofStepDetails(props.uuid)
  if (currLineProofStepDetails?.proofStep.stepType !== "line") { return null; }
  const currLineProofStep = currLineProofStepDetails.proofStep as TLineProofStep

  const rulesetDropdownValue = rulesetContext.rulesetDropdownOptions.find(option => option.value === currLineProofStep.justification.ruleName)

  const handleChangeRule = (newValue: SingleValue<{ value: string; label: string; }>) => {
    if (newValue == null) { return; }

    const updatedLineProofStep: TLineProofStep = {
      ...currLineProofStep,
      justification: {
        ruleName: newValue.value,
        refs: []
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
      className={cn("flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors border-blue-400 border-2 items-stretch")}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onContextMenuCapture={handleContextMenu}
    >
      <AutosizeInput title="Enter a formula" type="text" value={props.formula} onChange={handleChangeFormula} className="text-slate-800 grow resize shrink" inputClassName="px-2" />
      <div data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}
        title="Select a rule">
        <Select value={rulesetDropdownValue} onChange={handleChangeRule} options={rulesetContext.rulesetDropdownOptions} theme={dropdownTheme} styles={{
          singleValue: (base) => ({ ...base, paddingLeft: "8px", paddingRight: "8px" }),
          input (base, props) {
            return {
              ...base,
              paddingLeft: "8px",
              paddingRight: "8px",
            }
          },
        }} />
        {/* <Justification justification={props.justification} lines={props.lines} onHover={handleOnHoverJustification} /> */}
      </div>
      {/* <button className="absolute right-[-100px] bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 rounded">
        Done
      </button> */}
    </div>)
  );
}
