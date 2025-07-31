import { PencilIcon } from "lucide-react";
import { Button } from "./ui/button";
import { useProofStore } from "@/store/proofStore";
import React from "react";
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog";
import { Label } from "./ui/label";
import { Input } from "./ui/input";

export default function RenameProofButton({ proofId }: { proofId: string }) {
  const getProof = useProofStore(state => state.getProof)
  const updateProofTitle = useProofStore((state) => state.updateProofTitle);

  const [proofName, setProofName] = React.useState<string | null>(null);

  const onRename = () => {
    if (proofName === null || proofName === "") {
      alert("Proof must have a name")
      return;
    }
    
    if (getProof(proofId)?.title !== proofName) {
      updateProofTitle(proofId, proofName)
    }
  }

  return <Dialog>
    <DialogTrigger asChild>
      <Button variant="outline" onClick={_ => {
        const title = getProof(proofId)?.title ?? null
        if (!title) console.warn(`Renaming proof that doesn't exist ${proofId}...`)
        title && setProofName(title)
      }}>
        <PencilIcon className="h-4 w-4"></PencilIcon>
      </Button>
    </DialogTrigger>
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Rename proof: {getProof(proofId)?.title}</DialogTitle>
      </DialogHeader>

      <div className="flex flex-col gap-6">
        <div className="grid gap-2">
          <Label>Choose a name</Label>
          <Input
            id="proof_name"
            value={proofName ?? ""}
            onChange={e => setProofName(e.target.value)}
            placeholder="Proof of Goldbach's conjecture"
          />
        </div>
      </div>

      <DialogFooter>
        <DialogClose asChild><Button variant="outline">Cancel</Button></DialogClose>
        <DialogClose asChild><Button onClick={_ => onRename()}>Rename</Button></DialogClose>
      </DialogFooter>
    </DialogContent>
  </Dialog>
}
