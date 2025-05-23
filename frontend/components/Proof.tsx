"use client";

import {
  Diagnostic,
  BoxProofStep as TBoxProofStep,
  TLineNumber,
  LineProofStep as TLineProofStep,
} from "@/types/types";

import { BoxProofStep } from "@/components/BoxProofStep";
import { LineProofStep } from "@/components/LineProofStep";
import { ProofStep } from "@/types/types";
import { cn } from "@/lib/utils";

export function Proof({
  ...props
}: {
  proof: ProofStep[];
  diagnostics: Diagnostic[];
  lines: TLineNumber[];
  uuid?: string;
  isOuterProof?: boolean;
}) {
  return (
    <div className={cn("cursor-pointer text-slate-800 w-full -my-[3px]")}>
      {props.proof.map((proofStep) => {
        if (proofStep.stepType == "line") {
          const lineProofStepProps = proofStep as TLineProofStep;
          return (
            <LineProofStep
              key={lineProofStepProps.uuid}
              lines={props.lines}
              diagnosticsForLine={props.diagnostics.filter(
                (d) => d.uuid === proofStep.uuid
              )}
              isOuterProofStep={props.isOuterProof}
              {...lineProofStepProps}
            />
          );
        } else {
          const boxProofStepProps = proofStep as TBoxProofStep;
          return (
            <BoxProofStep
              key={boxProofStepProps.uuid}
              lines={props.lines}
              diagnostics={props.diagnostics}
              isOuterProofStep={props.isOuterProof}
              {...boxProofStepProps}
            />
          );
        }
      })}
    </div>
  );
}
