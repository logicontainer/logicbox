"use client";

import { LineNumberLine, BoxProofStep as TBoxProofStep, LineProofStep as TLineProofStep } from "@/types/types";

import { BoxProofStep } from "@/components/BoxProofStep";
import { LineProofStep } from "@/components/LineProofStep";
import { ProofStep } from "@/types/types";

export function Proof ({ ...props }: { proof: ProofStep[], lines: LineNumberLine[] }) {
  return <div className=" outline-2 outline outline-slate-800 text-slate-800 px-4 ">
    {props.proof.map((proofStep) => {
      if (proofStep.stepType == "line") {
        const lineProofStepProps = proofStep as TLineProofStep;
        return <LineProofStep key={lineProofStepProps.uuid} lines={props.lines} {...lineProofStepProps} />
      } else {
        const boxProofStepProps = proofStep as TBoxProofStep;
        return <BoxProofStep key={boxProofStepProps.uuid} lines={props.lines} {...boxProofStepProps} />
      }
    })}</div>
}