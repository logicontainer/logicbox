import { LineNumberLine, BoxProofStep as TBoxProofStep } from "@/types/types";

import { AddLineTooltip } from "./AddLineTooltip";
import { Proof } from "./Proof";
import { useContextMenu } from "react-contexify";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep ({ ...props }: TBoxProofStep & { lines: LineNumberLine[] }) {
  const { setLineInFocus, isFocused } = useProof();
  const isInFocus = isFocused(props.uuid)

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
    <div
      className="relative"
      onMouseOverCapture={() => setLineInFocus(props.uuid)}
      onContextMenuCapture={handleContextMenu}
    >
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} prepend />
      <Proof proof={props.proof} lines={props.lines} uuid={props.uuid} />
      <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} />
    </div>
  )
}