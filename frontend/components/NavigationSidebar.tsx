"use client";

import Card from "./Card";
import Link from "next/link";
import { ProofMetadata } from "@/types/types";
import { cn } from "@/lib/utils";
import examples from "@/examples/proof-examples";
import { useCurrentProofId } from "@/contexts/CurrentProofIdProvider";

export default function NavigationSidebar() {
  return (
    <div className=" p-2 gap-2 flex flex-col">
      <Card className="flex items-center justify-center gap-2 py-2">
        <Link href={"/"}>
          <img className="w-12 h-12" src="/logicbox-icon.svg"></img>
        </Link>
        <h1 className="text-left text-2xl font-bold py-2">LogicBox</h1>
      </Card>
      <Card className="p-0">
        <h2 className="p-4 text-left text-xl pb-2">Proofs</h2>
        <ProofListItem proof={{ id: "", title: "Random proof" }} />
        {examples.map((proof) => {
          return (
            <ProofListItem proof={proof} key={proof.id} /> // Use the proof.id as the key
          );
        })}
      </Card>
    </div>
  );
}

function ProofListItem({ proof }: { proof: ProofMetadata }) {
  const { proofId } = useCurrentProofId();
  return (
    <Link href={`/proofs/${proof.id}`} key={proof.id}>
      <p
        className={cn(
          "text text-ellipsis truncate px-2 py-1 whitespace-nowrap  text-gray-700 ",
          proof.id == proofId ? "bg-gray-200" : "hover:bg-gray-100"
        )}
        title={proof.title}
      >
        {proof.title}
      </p>
    </Link>
  );
}
