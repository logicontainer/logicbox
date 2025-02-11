"use client";

import { BoxProofStep as TBoxProofStep, LineProofStep as TLineProofStep } from "@/types/types";

import { BoxProofStep } from "@/components/BoxProofStep";
import { LineProofStep } from "@/components/LineProofStep";
import { ProofStep } from "@/types/types";

export function Proof ({ ...props }: { proof: ProofStep[] }) {
  return <div className=" outline-2 outline outline-slate-800 text-slate-800 px-4 ">
    {props.proof.map((proofStep) => {
      if (proofStep.stepType == "line") {
        const props = proofStep as TLineProofStep;
        return <LineProofStep key={props.uuid} {...props} />
      } else {
        const props = proofStep as TBoxProofStep;
        return <BoxProofStep key={props.uuid} {...props} />
      }
    })}</div>
}