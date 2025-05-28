"use client";

import { PlusIcon, TrashIcon } from "@radix-ui/react-icons";

import { Button } from "./ui/button";
import Card from "./Card";
import Link from "next/link";
import { LogicName, ProofMetadata } from "@/types/types";
import { cn } from "@/lib/utils";
// import examples from "@/examples/proof-examples";
import { useProofStore } from "@/store/proofStore";
import { useRouter } from "next/navigation";
import { v4 as uuid } from "uuid";
import { useProof } from "@/contexts/ProofProvider";
import { log } from "console";

export default function NavigationSidebar() {
  const proofs = useProofStore((state) => state.proofs);
  const addProof = useProofStore((state) => state.addProof);
  const router = useRouter();

  return (
    <div className=" p-2 gap-2 flex flex-col">
      <Card className="flex items-center justify-center gap-2 py-2">
        <Link href={"/"}>
          <img className="w-12 h-12" src="/logicbox-icon.svg"></img>
        </Link>
        <h1 className="text-left text-2xl font-bold py-2">LogicBox</h1>
      </Card>
      <Card className="p-0">
        <div className="p-4 flex items-baseline justify-between">
          <h2 className="text-left text-lg font-bold">Proofs</h2>
          <Button
            size="icon"
            variant="outline"
            onClick={() => {
              const newProofId = uuid();
              let logicName: LogicName | null = null

              while (logicName === null) {
                const p = prompt("Which logic should your proof be in?\nMust be either \n - 'prop' (propositional logic),\n - 'pred' (predicate logic),\n - 'arith' (arithmetic)")
                switch (p) {
                  case 'prop': logicName = "propositionalLogic"; break;
                  case 'pred': logicName = "predicateLogic"; break;
                  case 'arith': logicName = "arithmetic"; break;
                }
              }

              addProof({
                id: newProofId,
                title: "New proof",
                logicName: logicName,
                proof: [
                  {
                    formula: {
                      ascii: null,
                      latex: null,
                      userInput: "",
                    },
                    justification: {
                      refs: [],
                      rule: "premise",
                    },
                    stepType: "line",
                    uuid: uuid(),
                  },
                ],
              });
              router.push(`/proofs/${newProofId}`);
            }}
          >
            <PlusIcon className="h-4 w-4"></PlusIcon>
          </Button>
        </div>
        {proofs.map((proof) => {
          return (
            // Use the proof.id as the key
            <ProofListItem proof={proof} key={proof.id} />
          );
        })}
      </Card>
    </div>
  );
}

function ProofListItem({
  proof,
  deletable = true,
}: {
  proof: ProofMetadata;
  deletable?: boolean;
}) {
  const { proof: { id: proofId } } = useProof();
  const deleteProof = useProofStore((state) => state.deleteProof);
  const updateProofTitle = useProofStore((state) => state.updateProofTitle);
  const router = useRouter();
  return (
    <Link href={`/proofs/${proof.id}`} key={proof.id}>
      <div
        className={cn(
          "flex items-baseline justify-between",
          proof.id == proofId ? "bg-gray-200" : "hover:bg-gray-100"
        )}
      >
        <p
          className={cn(
            "text text-ellipsis truncate px-2 py-1 whitespace-nowrap  text-gray-700 grow self-stretch"
          )}
          title={proof.title}
          onDoubleClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            const newTitle = prompt("Enter new title", proof.title);
            if (newTitle) {
              updateProofTitle(proof.id, newTitle);
            }
          }}
        >
          {proof.title}
        </p>
        {deletable && (
          <Button
            variant="link"
            size="icon"
            className={cn(
              "shrink-0 hover:text-red-700",
              proof.id == proofId ? "" : "hover:opacity-100 opacity-10"
            )}
            onClick={(e) => {
              e.preventDefault();
              deleteProof(proof.id);
              if (proofId == proof.id) {
                router.push("/");
              }
            }}
          >
            <TrashIcon />
          </Button>
        )}
      </div>
    </Link>
  );
}
