"use client";

import { InlineMath } from "react-katex";
import { LineNumbers } from "@/components/LineNumbers";
import { Proof } from "@/components/Proof";
import { ProofStepContextMenu } from "@/components/ProofStepContextMenu";
import Toolbar from "@/components/Toolbar";
import { Tooltip } from "react-tooltip";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";

export default function Home() {
  const proofContext = useProof();
  const { lines } = useLines();
  return (
    <div className="flex justify-center">
      <main className="flex flex-col  row-start-2 items-center sm:items-start">
        <div className="p-4 flex flex-col justify-between items-center rounded-sm">
          <div className="fixed z-50">
            <Toolbar />
          </div>
          <div
            className="flex box-content gap-2 mt-20"
            onMouseLeave={() => {
              proofContext.setLineInFocus("");
              return;
            }}
          >
            <LineNumbers lines={lines} />
            <Proof proof={proofContext.proof} lines={lines} />
            <Tooltip
              id={`tooltip-id-${proofContext.lineInFocus}`}
              place="right"
              render={({ content }) => (
                <p className="text-lg">
                  <InlineMath math={content || ""}></InlineMath>
                </p>
              )}
            ></Tooltip>
            <ProofStepContextMenu />
          </div>
        </div>
      </main>
    </div>
  );
}
