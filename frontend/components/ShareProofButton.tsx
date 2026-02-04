import { ShareIcon } from "lucide-react";
import { Button } from "./ui/button";
import { useProofStore } from "@/store/proofStore";
import React from "react";
import { Dialog,  DialogContent,  DialogHeader, DialogTitle } from "./ui/dialog";
import { compressToEncodedURIComponent } from "lz-string";
import { Proof } from "@/types/types";

const minifyProof = (proof: Proof) => {
  let counter = 0;
  const visit1 = (steps: Proof, map: Record<string, string>) => {
    for (const s of steps) {
      const newId = ++counter + "";
        map[s.uuid] = newId;
        s.uuid = newId;

        if (s.stepType === "box") {
          visit1(s.proof, map)
        }
     }
  };

  const visit2 = (steps: Proof, map: Record<string, string>) => {
    for (const s of steps) {
     if (s.stepType === "line") {
       s.justification.refs = s.justification.refs.map(r => map[r])
     } else {
       visit2(s.proof, map)
     }
    }
  };

  const map = {};
  visit1(proof, map)
  visit2(proof, map)
}

export default function DownloadProofButton({ proofId, className }: { proofId: string, className?: string }) {
  const proofs = useProofStore((state) => state.proofs);
  const [open, setOpen] = React.useState(false)
  const [copied, setCopied] = React.useState(false)

  const proof = proofs.find(p => p.id === proofId) ?? null
  if (proof) minifyProof(proof.proof)
  const input = JSON.stringify(proof)
  const compressed = compressToEncodedURIComponent(input)

  return <>
      <Button variant="outline" onClick={() => setOpen(true)} className={className}>
        <ShareIcon />
      </Button>
      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Share proof</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 w-full">
            <div className="cursor-pointer grid w-full items-center gap-3">
              <Button 
                variant="outline"
                disabled={copied}
                onClick={() => {
                  navigator.clipboard.writeText(`${window.location.origin}/load?data=${compressed}`)
                  setCopied(true)
                }}
              >{!copied ? "Click here to copy link" : "Copied. ✅"}</Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </>
}
