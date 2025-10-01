import { UploadIcon } from "lucide-react";
import { Button } from "./ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ChangeEvent, useState } from "react";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { useProofStore } from "@/store/proofStore";
import { ProofWithMetadata, ProofWithMetadataSchema } from "@/types/types";

export default function UploadProofsButton() {
  const [open, setOpen] = useState(false);
  const [uploadedProofs, setUploadedProofs] = useState<ProofWithMetadata[] | null>(null);
  const [error, setError] = useState("");
  const addProofToStore = useProofStore((state) => state.addProof);
  const proofs = useProofStore((state) => state.proofs);

  const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
    const newErrors: string[] = []
    setUploadedProofs(null)
    const newProofs: ProofWithMetadata[] = []
    if (!e?.target?.files || e.target.files.length <= 0) { newErrors.push("No files"); }
    else {
      for (let i = 0; i < e.target.files.length; i++) {
        const file = e.target.files[i];
        if (!file) { newErrors.push("No files"); continue };
        if (file && !file.name.endsWith('.lgbx')) {
          newErrors.push(`File: "${file.name}" must be of type '.lgbx'.`);
          continue;
        }

        try {
          const text = await file.text();
          const parsed = JSON.parse(text);
          const proof = ProofWithMetadataSchema.parse(parsed)
          newProofs.push(proof);
        } catch (e) {
          newErrors.push(`File: "${file.name}" contains invalid LogicBox syntax.`);
        }
      }
    }
    setError(newErrors.join("\n"));
    if (newErrors.length == 0) {
      setUploadedProofs(newProofs)
    }
  };

  function addProof(proofJsonContent: ProofWithMetadata): void {
    if (uploadedProofs == null) {
      window.alert("No proof provided");
      return;
    }
    const postfixProofId = (proofId: string) => {
      const existingProofsWithId = proofs.find((proof) => proof.id == proofId);

      if (existingProofsWithId) {
        return postfixProofId(proofId + "_copy");
      } else {
        return proofId;
      }
    };
    proofJsonContent.id = postfixProofId(proofJsonContent.id);
    addProofToStore(proofJsonContent);
    setOpen(false);
  }

  function addProofs(): void {
    try {
      uploadedProofs?.forEach((proof) => {
        const parsedProofWithMetadata = ProofWithMetadataSchema.parse(proof)
        addProof(parsedProofWithMetadata);
      })
    } catch (e) {
      setError("A file contained a malformed logicbox proof.");
    }
  }

  return (
    <>
      <Button variant="outline" onClick={() => setOpen(true)}>
        <UploadIcon />
      </Button>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Upload proof(s)</DialogTitle>
            <DialogDescription>
              Each proof must be in the format of LogicBox compatible JSON.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 w-full">
            <div className="cursor-pointer grid w-full items-center gap-3">
              <Label htmlFor="proof-files">Proof files</Label>
              <Input
                id="proof-files"
                type="file"
                accept=".lgbx"
                onChange={(e) => handleFileChange(e)}
                className="w-full hover:bg-accent cursor-pointer"
                multiple
              />
            </div>
            {error && <p className="text-red-600 whitespace-pre-line">{error}</p>}
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Cancel</Button>
            </DialogClose>
            <Button
              type="submit"
              disabled={!!error || uploadedProofs == null}
              onClick={() => addProofs()}
            >
              Upload proof(s)
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
