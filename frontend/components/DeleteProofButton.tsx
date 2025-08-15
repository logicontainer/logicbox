import { PencilIcon, TrashIcon } from "lucide-react";
import { Button } from "./ui/button";
import { useProofStore } from "@/store/proofStore";
import React from "react";
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog";

export default function DeleteProofButton({ proofId, className }: { proofId: string, className?: string }) {
  const getProof = useProofStore(state => state.getProof)
  const deleteProof = useProofStore((state) => state.deleteProof);

  return <Dialog>
    <DialogTrigger asChild>
      <Button variant="outline" className={className}>
        <TrashIcon className="h-4 w-4"></TrashIcon>
      </Button>
    </DialogTrigger>
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Are you sure you want to delete: {getProof(proofId)?.title}?</DialogTitle>
      </DialogHeader>
      <DialogFooter>
        <DialogClose asChild><Button variant="outline">Cancel</Button></DialogClose>
        <DialogClose asChild><Button onClick={_ => deleteProof(proofId)}>Confirm</Button></DialogClose>
      </DialogFooter>
    </DialogContent>
  </Dialog>
}
