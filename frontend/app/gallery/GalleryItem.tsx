"use client";
import Card from "@/components/Card";
import { Button } from "@/components/ui/button";
import { LogicName, ProofWithMetadata } from "@/types/types";
import { TrashIcon } from "lucide-react";
import Link from "next/link";
import { useProofStore } from "@/store/proofStore";
import DownloadProofButton from "@/components/DownloadProofButton";
import { InlineMath } from "react-katex";

function logicNameToString(name: LogicName): string {
  switch (name) {
    case 'propositionalLogic':
      return "Propositional logic"
    case 'predicateLogic':
      return "Predicate logic"
    case 'arithmetic':
      return "Arithmetic"
  }
}

function logicNameToIconLatex(name: LogicName, seed: string): string {
  const options = (() => {
    switch (name) {
      case 'propositionalLogic':
        return ["\\land", "\\lor", "\\rightarrow", "\\lnot", "\\bot"]
      case 'predicateLogic':
        return ["\\forall", "\\exists", "P(x)", "f(x)", "="]
      case 'arithmetic':
        return ["0", "1", "*", "+"]
    }
  })()
  
  if (seed.length === 0) seed = "s" // must not be empty
  const number = seed.length + seed.split('').map(c => c.charCodeAt(0)).reduce((a, b) => a + b)
  return options[number % options.length]
}

export default function GalleryItem({ proof }: { proof: ProofWithMetadata }) {
  const deleteProof = useProofStore((state) => state.deleteProof);
  const createdAtString = () => {
    let isoString = proof.createdAt;
    if (!isoString) isoString = new Date().toISOString();
    const createdAtDate = new Date(isoString);
    return createdAtDate.toLocaleString();
  };
  return (
    <Link href={`/proofs/${proof.id}`}>
      <Card className="grid grid-cols-[100px_auto] hover:brightness-95 p-0 overflow-hidden"> 
        <div className="flex items-center justify-center bg-gray-100">
          <InlineMath math={logicNameToIconLatex(proof.logicName, proof.id)}/>
        </div>
        <div className="flex pl-2 py-2 gap-2 items-center justify-between cursor-pointer">
          <div className="flex flex-col gap-2">
            <p className="text text-xl font-bold">{proof.title}</p>
            <p className="text-sm text-gray-500">{logicNameToString(proof.logicName)}, {createdAtString()}</p>
          </div>
          <div className="flex items-center justify-end gap-1 pr-2">
            <Button
              variant="outline"
              className="hover:text-red-500"
              onMouseOver={(e) => e.stopPropagation()}
              title="Delete proof"
              onClick={(e) => {
                e.preventDefault();
                if (
                  window.confirm(
                    `Are you sure you want to delete the proof: ${proof.title}`,
                  )
                ) {
                  deleteProof(proof.id);
                }
              }}
            >
              <TrashIcon />
            </Button>
            <DownloadProofButton proofId={proof.id} />
          </div>
        </div>
      </Card>
    </Link>
  );
}
