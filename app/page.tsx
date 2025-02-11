"use client";

import { LineNumbers } from "@/components/LineNumbers";
import { Proof } from "@/components/Proof";
import { useProof } from "@/contexts/ProofProvider";

export default function Home () {
  const proofContext = useProof();
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)]">
      <main className="flex flex-col gap-8 row-start-2 items-center sm:items-start">
        <div className="p-8 bg-white flex flex-col justify-between gap-4 rounded-sm">
          <h2 className="text-2xl text-gray-800 font-medium">Proof:</h2>
          <div className="flex box-content gap-2">
            <LineNumbers />
            <Proof proof={proofContext.proof} />
          </div>
        </div>
      </main>
    </div>
  );
}
