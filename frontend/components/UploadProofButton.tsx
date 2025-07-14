import { Button } from "./ui/button";
import { UploadIcon } from "lucide-react";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { useState } from "react";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { useProofStore } from "@/store/proofStore";
import { ProofWithMetadata } from "@/types/types";

export default function UploadProofButton() {
  const [open, setOpen] = useState(false);
  const [jsonContent, setJsonContent] = useState(null);
  const [error, setError] = useState('');
  const addProofToStore = useProofStore(state => state.addProof);
  const proofs = useProofStore(state => state.proofs);

  const handleFileChange = async (e: any) => {
    setError('');
    const file = e.target.files[0];

    if (!file) return;
    if (file.type !== 'application/json') {
      setError('Please upload a valid JSON file.');
      return;
    }

    try {
      const text = await file.text();
      const parsed = JSON.parse(text);  // will throw if invalid JSON
      setJsonContent(parsed);
    } catch {
      setError('Invalid JSON file.');
    }
  };

  function addProof(event: MouseEvent<HTMLButtonElement, MouseEvent>): void {
    if (jsonContent == null) {
      window.alert("No proof provided");
      return;
    }
    const uploadedProof = jsonContent as ProofWithMetadata;

    const postfixProofId = (proofId: string) => {
      const existingProofsWithId = proofs.find(proof => proof.id == proofId);

      if (existingProofsWithId) {
        return postfixProofId(proofId + "_copy");
      } else {
        return proofId;
      }
    }
    uploadedProof.id = postfixProofId(uploadedProof.id);
    addProofToStore(uploadedProof);
    setOpen(false);
  }

  return (
    <>
      <Button variant="outline" onClick={() => setOpen(true)}>
        <UploadIcon />
      </Button>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Upload proof</DialogTitle>
            <DialogDescription>
              The proof must be in the format of LogicBox compatible JSON.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 w-full">
            <div className="cursor-pointer grid w-full items-center gap-3">
              <Label htmlFor="proof-file">Proof file</Label>
              <Input id="proof-file" type="file"
                accept=".json,application/json"
                onChange={handleFileChange}
                className="w-full"
              />
            </div>
            {error && <p className="text-red-600">{error}</p>}
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Cancel</Button>
            </DialogClose>
            <Button type="submit" disabled={!!error || jsonContent == null}
              onClick={addProof}>Upload proof</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  )
}
