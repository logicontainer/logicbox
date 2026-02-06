"use client";


import { Proof } from "@/components/Proof";
import { ProofStepContextMenu } from "@/components/ProofStepContextMenu";
import React from "react";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";

export default function ProofEditor({ proofId }: { proofId: string | null }) {
  const proofContext = useProof();
  const { proofDiagnostics } = useServer();
  const { lines } = useLines();

  React.useEffect(() => {
    if (proofId) proofContext.loadProofFromId(proofId);
  }, [proofId, proofContext]);

  return (
    <div>
      <ProofStepContextMenu />
      <div
        className="max-h-[calc(100vh-282px)] md:max-h-max grid items-start w-full  overflow-auto md:h-screen md:gap-2"
      >
        <div className="flex flex-col items-center md:grid md:grid-cols-[0.5fr_auto_4fr] w-full">
          <div></div>
          <div className="relative pl-16 p-4 flex flex-col justify-between items-center rounded-sm">
            <div
              className="flex box-content gap-2 w-full select-none"
              onClick={(e) => e.stopPropagation()}
            >
              <Proof
                proof={proofContext.proof.proof}
                lines={lines}
                diagnostics={proofDiagnostics}
                isOuterProof
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
