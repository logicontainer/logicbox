"use client";

import { LineNumbers } from "@/components/LineNumbers";
import { Proof } from "@/components/Proof";
import { parseLinesFromProof } from "@/utils/proof-parser";
import { useProof } from "@/contexts/ProofProvider";

export default function Home () {
  const proofContext = useProof();
  const lines = parseLinesFromProof(proofContext.proof)
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)] bg-slate-100">
      <main className="flex flex-col gap-8 row-start-2 items-center sm:items-start">
        <div className="p-8 flex flex-col justify-between gap-4 rounded-sm">
          <div className="flex box-content gap-2">
            <LineNumbers lines={lines} />
            < Proof proof={proofContext.proof} />
          </div>
        </div>
      </main>
    </div>
  );
}
