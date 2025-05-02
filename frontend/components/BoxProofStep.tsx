import { BoxProofStep as TBoxProofStep, TLineNumber } from "@/types/types";
import {
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { Proof } from "./Proof";
import { useContextMenu } from "react-contexify";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep({
  ...props
}: TBoxProofStep & { lines: TLineNumber[] }) {
  const { setLineInFocus } = useProof();
  const { doTransition } = useInteractionState();

  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });

  function handleContextMenu(
    event:
      | React.MouseEvent<HTMLElement>
      | React.TouchEvent<HTMLElement>
      | React.KeyboardEvent<HTMLElement>
      | KeyboardEvent
  ) {
    show({
      event,
      props: {
        uuid: props.uuid,
      },
    });
  }
  return (
    <div
      className="relative"
      onMouseOverCapture={() => setLineInFocus(props.uuid)}
      onContextMenuCapture={handleContextMenu}
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
