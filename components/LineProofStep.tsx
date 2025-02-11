import { LineProofStep as TLineProofStep } from "@/types/Proof";

export function LineProofStep ({ ...props }: TLineProofStep) {
  return (<p className="text-lg/10">{props.latexFormula}</p>)
}