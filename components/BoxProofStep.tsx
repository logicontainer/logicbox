import { Proof } from "./Proof";
import { BoxProofStep as TBoxProofStep } from "@/types/Proof";
export function BoxProofStep ({ ...props }: TBoxProofStep) {
  return (<div><Proof proof={props.proof} /></div>)
}