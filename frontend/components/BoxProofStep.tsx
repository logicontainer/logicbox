import {
  Diagnostic,
  BoxProofStep as TBoxProofStep,
  TLineNumber,
} from "@/types/types";
import {
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { Proof } from "./Proof";
import { ProofStepWrapper } from "./ProofStepWrapper";
import { cn } from "@/lib/utils";
import { useContextMenu } from "@/contexts/ContextMenuProvider";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep({
  ...props
}: TBoxProofStep & {
  lines: TLineNumber[];
  diagnostics: Diagnostic[];
  isOuterProofStep?: boolean;
}) {
  const { setStepInFocus, isFocused } = useProof();
  const { doTransition } = useInteractionState();

  const { setContextMenuPosition } = useContextMenu();

  const currentlyBeingHovered = isFocused(props.uuid);

  return (
    <ProofStepWrapper
      currentlyBeingHovered={currentlyBeingHovered}
      isOuterProofStep={props.isOuterProofStep}
      isBox={true}
    >
      <div
        className={cn(
          "pointer-events-auto relative border-2 border-black my-[1px] overflow-hidden",
          currentlyBeingHovered && "bg-slate-50"
        )}
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
        <Proof
          proof={props.proof}
          lines={props.lines}
          uuid={props.uuid}
          diagnostics={props.diagnostics}
        />
      </div>
    </ProofStepWrapper>
  );
}
