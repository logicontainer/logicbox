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
import { useProof } from "@/contexts/ProofProvider";

export function Proof({
  ...props
}: {
  proof: ProofStep[];
  diagnostics: Diagnostic[];
  lines: TLineNumber[];
  uuid?: string;
  isOuterProof?: boolean;
}) {
  const { getParentUuid } = useProof()
  return (
    <div className={cn("cursor-pointer text-slate-800 w-full min-w-40")}>
      {props.proof.map((proofStep) => {
        if (proofStep.stepType == "line") {
          const parentUuid = getParentUuid(proofStep.uuid)
          const diagnosticsForLine = props.diagnostics.filter((d) => d.uuid === proofStep.uuid || d.uuid === parentUuid);
          return (
            <LineProofStep
              key={proofStep.uuid}
              lines={props.lines}
              diagnosticsForLine={diagnosticsForLine}
              isOuterProofStep={props.isOuterProof}
              {...proofStep}
            />
          );
        } else {
          const boxProofStepProps = proofStep;
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
