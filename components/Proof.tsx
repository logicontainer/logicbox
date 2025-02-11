"use client";

import { BoxProofStep as TBoxProofStep, LineProofStep as TLineProofStep } from "@/types/Proof";

import { BoxProofStep } from "@/components/BoxProofStep";
import { LineProofStep } from "@/components/LineProofStep";
import { ProofStep } from "@/types/Proof";

export function Proof ({ ...props }: { proof: ProofStep[] }) {
  return <div className=" outline-2 outline outline-gray-800 bg-gray-100 text-gray-800 px-4 ">
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