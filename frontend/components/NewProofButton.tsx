"use client";
import { Button } from "@/components/ui/button";
import { LogicName, Proof } from "@/types/types";
import { useRouter } from "next/navigation";
import { useProofStore } from "@/store/proofStore";
import { PlusIcon } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { v4 as uuidv4 } from "uuid";
import { cn } from "@/lib/utils";
import { InlineMath } from "react-katex";
import React from "react";
import "katex/dist/katex.min.css";
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { log } from "console";

const NEW_PROOF: Proof = [
  {
    stepType: "line",
    uuid: uuidv4(),
    formula: {
      userInput: "",
      ascii: null,
      latex: null
    },
    justification: {
      rule: null,
      refs: []
    }
  }
]

function LogicOption({
  name,
  latex,
  chosen,
  onClick,
}: {
  name: string
  latex: string
  chosen: boolean
  onClick: () => void
}) {
  return <div 
    className={cn(
      "w-full h-9 flex items-center justify-between px-3 border-[1px] border-solid border-slate-200 rounded  cursor-pointer",
      !chosen && "hover:bg-accent",
      chosen && "bg-slate-200",
      "text-sm",
    )} 
    onClick={onClick}
    tabIndex={0}
    onKeyDown={e => {
      if (["Enter", " "].includes(e.key)) {
        onClick()
      }
    }}
  >
    {name}
    <span className="text-slate-500"><InlineMath math={latex}/></span>
  </div>
}



export default function NewProofButton({ className }: { className?: string }) {
  const addProof = useProofStore((state) => state.addProof);
  const router = useRouter();
  
  const [proofName, setProofName] = React.useState<string>("");
  const [chosenLogic, setChosenLogic] = React.useState<LogicName | null>(null)

  const handleToggleLogicOption = (logicName: LogicName) => {
    setChosenLogic(
      logicName === chosenLogic ? null : logicName
    )
  }

  const onCreate = () => {
    if (proofName === "") {
      alert("Proof must have a name")
      return;
    }

    if (chosenLogic === null) {
      alert("You must choose a logic")
      return;
    }

    const newProofId = uuidv4()
    addProof({
      id: newProofId,
      createdAt: new Date().toISOString(),
      title: proofName,
      logicName: chosenLogic,
      proof: NEW_PROOF
    })
    router.push(`/proof/?id=${newProofId}`)
  }

  return <Dialog>
    <DialogTrigger asChild>
      <Button className={className} variant="outline" onClick={_ => {
        setProofName("")
        setChosenLogic(null)
      }}>
        <PlusIcon className="h-4 w-4"></PlusIcon>
      </Button>
    </DialogTrigger>
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Create a new proof</DialogTitle>
      </DialogHeader>

      <div className="flex flex-col gap-6">
        <div className="grid gap-2">
          <Label>Choose a name</Label>
          <Input
            id="proof_name"
            value={proofName}
            onChange={e => setProofName(e.target.value)}
            placeholder="Proof of Goldbach's conjecture"
          />
        </div>
      </div>
      <div className="flex flex-col gap-1">
        <div className="flex justify-between">
          <Label>Select logic</Label>
          <Label className="text-xs font-normal text-slate-500">Example symbols</Label>
        </div>
        <div className="flex flex-col gap-1">
          <LogicOption name="Propositional logic" latex="p, q, \land, \lor, \rightarrow, \bot" chosen={chosenLogic === "propositionalLogic"} onClick={() => handleToggleLogicOption("propositionalLogic")}/>
          <LogicOption name="Predicate logic" latex="\forall, \exists, Q(a, b), x = y" chosen={chosenLogic === "predicateLogic"} onClick={() => handleToggleLogicOption("predicateLogic")}/>
          <LogicOption name="Arithmetic" latex="0, 1, +, *" chosen={chosenLogic === "arithmetic"} onClick={() => handleToggleLogicOption("arithmetic")}/>
        </div>
      </div>

      <DialogFooter>
        <DialogClose asChild><Button variant="outline">Cancel</Button></DialogClose>
        <Button onClick={onCreate}>Create</Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
}
