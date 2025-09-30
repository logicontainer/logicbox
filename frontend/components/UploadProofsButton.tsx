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
  const [jsonContent, setJsonContent] = useState<JSON[] | null>(null);
  const [error, setError] = useState("");
  const addProofToStore = useProofStore((state) => state.addProof);
  const proofs = useProofStore((state) => state.proofs);

  const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
    setError("");
    setJsonContent(null)
    const newJsonContent: JSON[] = []
    if (!e?.target?.files || e.target.files.length <= 0) return;
    for (let i = 0; i < e.target.files.length; i++) {
      const file = e.target.files[i];

      if (!file) return;
      if (file.type !== "application/json") {
        setError(`Please upload a valid JSON file (${i}).`);
        return;
      }

      try {
        const text = await file.text();
        const parsed = JSON.parse(text); // will throw if invalid JSON
        newJsonContent.push(parsed);
      } catch {
        setError(`Invalid JSON file (${i}).`);
      }
    }
    setJsonContent(newJsonContent)
  };

  function addProof(proofJsonContent: ProofWithMetadata): void {
    if (jsonContent == null) {
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
      jsonContent?.forEach((proof) => {
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
                accept=".json,application/json"
                onChange={(e) => handleFileChange(e)}
                className="w-full hover:bg-accent cursor-pointer"
                multiple
              />
            </div>
            {error && <p className="text-red-600">{error}</p>}
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline">Cancel</Button>
            </DialogClose>
            <Button
              type="submit"
              disabled={!!error || jsonContent == null}
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
