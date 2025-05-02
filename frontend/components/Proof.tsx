"use client";

import {
  BoxProofStep as TBoxProofStep,
  TLineNumber,
  LineProofStep as TLineProofStep,
} from "@/types/types";

import { BoxProofStep } from "@/components/BoxProofStep";
import { LineProofStep } from "@/components/LineProofStep";
import { ProofStep } from "@/types/types";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";

export function Proof({
  ...props
}: {
  proof: ProofStep[];
  lines: TLineNumber[];
  uuid?: string;
}) {
  const { isFocused } = useProof();
  const isInFocus = isFocused(props.uuid || "");

  return (
    <div
      className={cn(
        "outline-2 outline outline-slate-800 text-slate-800 px-4 cursor-auto",
        isInFocus ? "outline-green-400" : ""
      )}
    >
      {props.proof.map((proofStep) => {
        if (proofStep.stepType == "line") {
          const lineProofStepProps = proofStep as TLineProofStep;
          return (
            <LineProofStep
              key={lineProofStepProps.uuid}
              lines={props.lines}
              {...lineProofStepProps}
            />
          );
        } else {
          const boxProofStepProps = proofStep as TBoxProofStep;
          return (
            <BoxProofStep
              key={boxProofStepProps.uuid}
              lines={props.lines}
              {...boxProofStepProps}
            />
          );
        }
      })}
    </div>
  );
}
