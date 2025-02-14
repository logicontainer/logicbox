import { LineNumberLine, BoxProofStep as TBoxProofStep } from "@/types/types";

import { AddLineTooltip } from "./AddLineTooltip";
import { Proof } from "./Proof";
// import { RemoveLineTooltip } from "./RemoveLineTooltip";
import { useProof } from "@/contexts/ProofProvider";

export function BoxProofStep ({ ...props }: TBoxProofStep & { lines: LineNumberLine[] }) {
  const { setLineInFocus, isFocused } = useProof();
  const isInFocus = isFocused(props.uuid)
  return (<div className="relative" onMouseOverCapture={() => setLineInFocus(props.uuid)}
  >
    {/* <RemoveLineTooltip uuid={props.uuid} isVisible={isInFocus} /> */}
    <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} prepend />
    <Proof proof={props.proof} lines={props.lines} uuid={props.uuid} />
    <AddLineTooltip uuid={props.uuid} isVisible={isInFocus} />
  </div>)
}