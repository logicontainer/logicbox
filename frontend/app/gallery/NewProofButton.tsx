"use client";
import { Button } from "@/components/ui/button";
import { LogicName } from "@/types/types";
import { useRouter } from "next/navigation";
import { v4 as uuid } from "uuid";
import { useProofStore } from "@/store/proofStore";
import { PlusIcon } from "lucide-react";

export default function NewProofButton() {
  const addProof = useProofStore((state) => state.addProof);
  const router = useRouter();
  return (
    <Button
      size="icon"
      variant="outline"
      onClick={() => {
        const newProofId = uuid();
        let logicName: LogicName | null = null;

        while (logicName === null) {
          const p = prompt(
            "Which logic should your proof be in?\nMust be either \n - 'prop' (propositional logic),\n - 'pred' (predicate logic),\n - 'arith' (arithmetic)",
          );
          if (p === null) return;
          switch (p) {
            case "prop":
              logicName = "propositionalLogic";
              break;
            case "pred":
              logicName = "predicateLogic";
              break;
            case "arith":
              logicName = "arithmetic";
              break;
          }
        }

        addProof({
          id: newProofId,
          title: "New proof",
          logicName: logicName,
          createdAt: new Date().toISOString(),
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
  );
}
