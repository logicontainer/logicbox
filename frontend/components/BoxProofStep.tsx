import { Diagnostic, BoxProofStep as TBoxProofStep, TLineNumber } from "@/types/types";
import {
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { Proof } from "./Proof";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useProof } from "@/contexts/ProofProvider";
import { cn } from "@/lib/utils";

export function BoxProofStep({
  ...props
}: TBoxProofStep & { lines: TLineNumber[]; diagnostics: Diagnostic[] }) {
  const { setStepInFocus, isFocused } = useProof();
  const { doTransition } = useInteractionState();

  const { setContextMenuPosition } = useContextMenu();

  const currentlyBeingHovered = isFocused(props.uuid)

  return (
    <div
      className={cn("relative px-3 outline outline-slate-800", currentlyBeingHovered && "bg-slate-50")}
      onMouseOverCapture={() => setStepInFocus(props.uuid)}
      onContextMenuCapture={(e) => {
        e.preventDefault();
        setContextMenuPosition({ x: e.pageX, y: e.pageY });
        doTransition({
          enum: TransitionEnum.RIGHT_CLICK_STEP,
          proofStepUuid: props.uuid,
          isBox: true,
        });
      }}
      onClick={() =>
        doTransition({
          enum: TransitionEnum.CLICK_BOX,
          boxUuid: props.uuid,
        })
      }
    >
      <Proof proof={props.proof} lines={props.lines} uuid={props.uuid} diagnostics={props.diagnostics}/>
    </div>
  );
}
