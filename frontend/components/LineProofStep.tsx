"use client";

import "katex/dist/katex.min.css";

import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";
import Select, { SingleValue, Theme } from "react-select";
import { TLineNumber, LineProofStep as TLineProofStep } from "@/types/types";

import AutosizeInput from "react-input-autosize";
import { InlineMath } from "react-katex";
import { Justification } from "./Justification";
import React from "react";
import { RefSelect } from "./RefSelect";
import { cn } from "@/lib/utils";
import { lineIsBeingEdited } from "@/lib/state-helpers";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useRuleset } from "@/contexts/RulesetProvider";
import { useState } from "react";

export function LineProofStep({
  ...props
}: TLineProofStep & { lines: TLineNumber[] }) {
  const { interactionState } = useInteractionState();
  const isTheActiveEdit = lineIsBeingEdited(props.uuid, interactionState);
  return <LineProofStepEdit {...props} />;
}

// export function LineProofStepView({
//   ...props
// }: TLineProofStep & { lines: TLineNumber[] }) {
//   const { setLineInFocus, isFocused } = useProof();

//   const { doTransition } = useInteractionState();
//   const { setContextMenuPosition } = useContextMenu();

//   const [tooltipContent, setTooltipContent] = useState<string>();
//   const isInFocus = isFocused(props.uuid);

//   const handleOnHoverJustification = (highlightedLatex: string) => {
//     setTooltipContent(highlightedLatex);
//   };

//   return (
//     <div
//       className={cn(
//         "flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors",
//         isInFocus ? "text-blue-400" : ""
//       )}
//       onMouseOver={() => setLineInFocus(props.uuid)}
//       onContextMenuCapture={(e) => {
//         e.preventDefault();
//         setContextMenuPosition({ x: e.clientX, y: e.clientY });
//         doTransition({
//           enum: TransitionEnum.RIGHT_CLICK_STEP,
//           proofStepUuid: props.uuid,
//           isBox: false,
//         });
//       }}
//       onClick={() =>
//         doTransition({ enum: TransitionEnum.CLICK_LINE, lineUuid: props.uuid })
//       }
//     >
//       <p className="shrink">
//         {props.formula.unsynced ? (
//           props.formula.userInput
//         ) : (
//           <InlineMath math={props.formula.latex || ""} />
//         )}
//       </p>
//       <div
//         data-tooltip-id={`tooltip-id-${props.uuid}`}
//         data-tooltip-content={tooltipContent}
//       >
//         <Justification
//           justification={props.justification}
//           lines={props.lines}
//           onHover={handleOnHoverJustification}
//           onClickRule={() =>
//             doTransition({
//               enum: TransitionEnum.CLICK_RULE,
//               lineUuid: props.uuid,
//             })
//           }
//           onClickRef={(refIdx) =>
//             doTransition({
//               enum: TransitionEnum.CLICK_REF,
//               lineUuid: props.uuid,
//               refIdx,
//             })
//           }
//         />
//       </div>
//     </div>
//   );
// }

export function LineProofStepEdit({
  ...props
}: TLineProofStep & { lines: TLineNumber[] }) {
  const { setLineInFocus } = useProof();
  const { interactionState, doTransition } = useInteractionState();

  const proofContext = useProof();
  const rulesetContext = useRuleset();

  const { setContextMenuPosition } = useContextMenu();

  const [tooltipContent, setTooltipContent] = useState<string>("");

  const formulaInputRef = React.useRef<HTMLInputElement>(null);

  const currentlyEditingRule =
    interactionState.enum === InteractionStateEnum.EDITING_RULE &&
    interactionState.lineUuid === props.uuid;
  const currentlyEditingFormula =
    interactionState.enum === InteractionStateEnum.EDITING_FORMULA &&
    interactionState.lineUuid === props.uuid;

  const currLineProofStepDetails = proofContext.getProofStepDetails(props.uuid);
  if (currLineProofStepDetails?.proofStep.stepType !== "line") {
    return null;
  }
  const currLineProofStep =
    currLineProofStepDetails.proofStep as TLineProofStep;

  const formulaContent = currentlyEditingFormula
    ? interactionState.currentFormula
    : currLineProofStep.formula.userInput;

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

  const handleInputRefChange = (ref: HTMLInputElement | null) => {
    formulaInputRef.current = ref;
  };

  const onKeyDownAutoSizeInput = (key: string) => {
    if (currentlyEditingFormula && key === "Enter") {
      doTransition({ enum: TransitionEnum.CLOSE });
      formulaInputRef.current?.blur();
    }
  };

  const handleOnHoverJustification = (highlightedLatex: string) => {
    setTooltipContent(highlightedLatex);
  };

  const isEditingFormula =
    interactionState.enum === InteractionStateEnum.EDITING_FORMULA &&
    interactionState.lineUuid === props.uuid;

  return (
    <div
      className={cn(
        "flex relative justify-between gap-8 text-lg/10 text-slate-800 pointer px-[-1rem] transition-colors items-stretch"
      )}
      onMouseOver={() => setLineInFocus(props.uuid)}
      onClick={(e) => {
        if (e.target !== e.currentTarget) {
          return;
        }
        return doTransition({
          enum: TransitionEnum.CLICK_LINE,
          lineUuid: props.uuid,
        });
      }}
      onContextMenuCapture={(e) => {
        e.preventDefault();
        setContextMenuPosition({ x: e.clientX, y: e.clientY });
        doTransition({
          enum: TransitionEnum.RIGHT_CLICK_STEP,
          proofStepUuid: props.uuid,
          isBox: false,
        });
      }}
    >
      {isEditingFormula ? (
        <AutosizeInput
          inputRef={handleInputRefChange}
          value={formulaContent}
          onChange={(e) => {
            console.log(e);
            doTransition({
              enum: TransitionEnum.UPDATE_FORMULA,
              formula: e.target.value,
            });
          }}
          autoFocus={currentlyEditingFormula}
          onKeyDown={(e) => onKeyDownAutoSizeInput(e.key)}
          title="Write a formula"
          className="text-slate-800 grow resize shrink"
          inputClassName="px-2"
        />
      ) : (
        <p
          className="shrink"
          onClick={() =>
            doTransition({
              enum: TransitionEnum.CLICK_LINE,
              lineUuid: props.uuid,
            })
          }
        >
          {props.formula.unsynced ? (
            props.formula.userInput
          ) : (
            <InlineMath math={props.formula.latex || ""} />
          )}
        </p>
      )}

      <div
        data-tooltip-id={`tooltip-id-${props.uuid}`}
        data-tooltip-content={tooltipContent}
        title="Select a rule"
        className="flex items-center gap-2 whitespace-nowrap"
      >
        <Justification
          uuid={props.uuid}
          justification={props.justification}
          lines={props.lines}
          onHover={handleOnHoverJustification}
          onClickRule={() =>
            doTransition({
              enum: TransitionEnum.CLICK_RULE,
              lineUuid: props.uuid,
            })
          }
          onClickRef={(refIdx) =>
            doTransition({
              enum: TransitionEnum.CLICK_REF,
              lineUuid: props.uuid,
              refIdx,
            })
          }
        />
      </div>
    </div>
  );
}
