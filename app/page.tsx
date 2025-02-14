"use client";

import { AddLinePopover } from "@/components/AddLinePopover";
import { InlineMath } from "react-katex";
import { LineNumbers } from "@/components/LineNumbers";
import { Proof } from "@/components/Proof";
import { RulesDropdown } from "@/components/RulesDropdown";
import { Toolbar } from "@/components/Toolbar";
import { Tooltip } from "react-tooltip";
import { parseLinesFromProof } from "@/lib/lines-parser";
import { useProof } from "@/contexts/ProofProvider";

export default function Home () {
  const proofContext = useProof();
  const lines = parseLinesFromProof(proofContext.proof)
  return (
    <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20 font-[family-name:var(--font-geist-sans)] bg-slate-100">
      <main className="flex flex-col gap-8 row-start-2 items-center sm:items-start">
        <div className="p-8 flex flex-col justify-between items-center gap-4 rounded-sm">
          <Toolbar />
          <div className="flex box-content gap-2" onMouseLeave={() => {
            proofContext.setLineInFocus("")
            return
          }
          }>
            <LineNumbers lines={lines} />
            <Proof proof={proofContext.proof} lines={lines} />
            <Tooltip id={`tooltip-id-${proofContext.lineInFocus}`} place="right" render={({ content }) => (
              <p className="text-lg"><InlineMath math={content || ""}></InlineMath></p>
            )} >
            </Tooltip>
            <AddLinePopover uuid={proofContext.latestLineInFocus || ""} />
          </div>
          <RulesDropdown />
        </div>
      </main>
      {/* <textarea className="absolute top-full w-full p-4" value={JSON.stringify(proofContext.proof, null, 2)} onChange={(e) => proofContext.setStringProof(e.target.value)} rows={15} cols={50} /> */}
    </div>
  );
}
