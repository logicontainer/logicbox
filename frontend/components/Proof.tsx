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
}) {
  const { isFocused } = useProof();
  const isInFocus = isFocused(props.uuid || "");

  return (
    <div
      className={cn(
        " text-slate-800 cursor-auto w-full",
        isInFocus && "outline-blue-400"
      )}
      style={{ outlineWidth: "1.5px" }}
    >
      {props.proof.map((proofStep) => {
        if (proofStep.stepType == "line") {
          const lineProofStepProps = proofStep as TLineProofStep;
          return (
            <LineProofStep
              key={lineProofStepProps.uuid}
              lines={props.lines}
              diagnosticsForLine={props.diagnostics.filter(d => d.uuid === proofStep.uuid)}
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
              {...boxProofStepProps}
            />
          );
        }
      })}
    </div>
  );
}
