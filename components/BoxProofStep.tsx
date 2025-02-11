import { LineNumberLine, BoxProofStep as TBoxProofStep } from "@/types/types";

import { Proof } from "./Proof";

export function BoxProofStep ({ ...props }: TBoxProofStep & { lines: LineNumberLine[] }) {
  return (<div><Proof proof={props.proof} lines={props.lines} /></div>)
}