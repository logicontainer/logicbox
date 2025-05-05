"use client";

import Link from "next/link";
import { ProofMetadata } from "@/types/types";
import { cn } from "@/lib/utils";
import examples from "@/examples/proof-examples";
import { useCurrentProofId } from "@/contexts/CurrentProofIdProvider";

export default function NavigationSidebar() {
  return (
    <div className="bg-gray-50 border-r-2">
      <h1 className="text-left px-2 text-2xl font-bold py-2">Proofs</h1>
      <ProofListItem proof={{ id: "", title: "Random proof" }} />
      {examples.map((proof) => {
        return (
          <ProofListItem proof={proof} key={proof.id} /> // Use the proof.id as the key
        );
      })}
    </div>
  );
}

function ProofListItem({ proof }: { proof: ProofMetadata }) {
  const { proofId } = useCurrentProofId();
  return (
    <Link href={`/proofs/${proof.id}`} key={proof.id}>
      <p
        className={cn(
          "px-2 py-1",
          proof.id == proofId ? "bg-gray-200" : "hover:bg-gray-100"
        )}
      >
        {proof.title}
      </p>
    </Link>
  );
}
