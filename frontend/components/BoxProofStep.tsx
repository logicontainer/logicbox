import { BoxProofStep as TBoxProofStep, TLineNumber } from "@/types/types";

import { Proof } from "./Proof";
import { useContextMenu } from "react-contexify";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep({
  ...props
}: TBoxProofStep & { lines: TLineNumber[] }) {
  const { setLineInFocus } = useProof();

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
      onClick={handleContextMenu}
    >
      <Proof proof={props.proof} lines={props.lines} uuid={props.uuid} />
    </div>
  );
}
