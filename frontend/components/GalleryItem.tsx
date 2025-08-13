"use client";
import Card from "@/components/Card";
import { LogicName, ProofWithMetadata } from "@/types/types";
import DownloadProofButton from "@/components/DownloadProofButton";
import { InlineMath } from "react-katex";
import DeleteProofButton from "@/components/DeleteProofButton";
import RenameProofButton from "@/components/RenameProofButton";
import { Skeleton } from "@/components/ui/skeleton";
import { useRouter } from "next/navigation";
import { ButtonGroup } from "./ui/button-group";
import { Edit } from "lucide-react";
import AutosizeInput from "react-input-autosize";
import { cn } from "@/lib/utils";
import React from "react";
import { useProofStore } from "@/store/proofStore";
import { EditableProofTitle } from "./EditableProofTitle";

export function logicNameToString(name: LogicName): string {
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

export function GalleryItem({ proof }: { proof: ProofWithMetadata }) {
  const router = useRouter()

  const createdAtString = () => {
    let isoString = proof.createdAt;
    if (!isoString) isoString = new Date().toISOString();
    const createdAtDate = new Date(isoString);
    return createdAtDate.toLocaleString();
  };

  return (
    <div onClick={_ => {
      router.push(`/proof?id=${proof.id}`)
    }}>
      <Card
        className="grid grid-cols-[50px_auto_auto] sm:grid-cols-[75px_auto_auto] h-24 gap-2 hover:brightness-95 p-0 overflow-hidden cursor-pointer"
      >
        <div className="flex items-center justify-center bg-gray-100">
          <InlineMath math={logicNameToIconLatex(proof.logicName, proof.id)} />
        </div>
        <div className="flex flex-col gap-2 justify-center overflow-hidden">
          <EditableProofTitle proofId={proof.id}/>
          <div className="text-xs text-gray-500">
            {logicNameToString(proof.logicName)}<br />
            {createdAtString()}
          </div>
        </div>
        <div className="flex items-center justify-end gap-1 pr-2">
          <div
            onClick={e => e.stopPropagation() /* don't let button clicks through to selecting this */}
          >
            <ButtonGroup orientation="vertical">
              <DeleteProofButton proofId={proof.id} />
              <DownloadProofButton proofId={proof.id} />
            </ButtonGroup>
          </div>
        </div>

      </Card>
    </div>
  );
}

export function GalleryItemSkeleton() {
  return (
    <Card className="grid grid-cols-[75px_auto_auto] h-24 gap-2 p-0 overflow-hidden">
      <div className="flex items-center justify-center bg-gray-100">
        <Skeleton className="size-8" />
      </div>
      <div className="flex flex-col gap-2 justify-center overflow-hidden">
        <Skeleton className="w-24 h-4" />
        <div className="flex flex-col gap-1">
          <Skeleton className="w-32 h-2" />
          <Skeleton className="w-40 h-2" />
        </div>
      </div>
      <div className="flex items-center justify-end gap-1 pr-2">
        <Skeleton className="w-12 h-8" />
        <Skeleton className="w-12 h-8" />
        <Skeleton className="w-12 h-8" />
      </div>
    </Card>
  );
}
