import { BoxProofStep as TBoxProofStep, TLineNumber } from "@/types/types";
import {
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { Proof } from "./Proof";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep({
  ...props
}: TBoxProofStep & { lines: TLineNumber[] }) {
  const { setLineInFocus } = useProof();
  const { doTransition } = useInteractionState();

  const { setContextMenuPosition } = useContextMenu();

  return (
    <div
      className="relative"
      onMouseOverCapture={() => setLineInFocus(props.uuid)}
      onContextMenuCapture={(e) => {
        e.preventDefault();
        setContextMenuPosition({ x: e.clientX, y: e.clientY });
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
      <Proof proof={props.proof} lines={props.lines} uuid={props.uuid} />
    </div>
  );
}
