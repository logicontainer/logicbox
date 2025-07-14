import { DownloadIcon } from "lucide-react";
import { Button } from "./ui/button";
import { useProofStore } from "@/store/proofStore";
import { MouseEvent } from "react";
import { download } from "@/lib/utils";

export default function DownloadProofButton({ proofId }: { proofId: string }) {
  const proofs = useProofStore((state) => state.proofs);
  function handleDownloadProof(e: MouseEvent) {
    e.preventDefault();
    e.stopPropagation();
    const proof = proofs.find((proof) => proof.id == proofId);
    download(
      JSON.stringify(proof, null, 2),
      `${proof?.title}.json`,
      "text/plain",
    );
  }
  return (
    <Button
      variant="outline"
      title="Download proof as JSON"
      onClick={(e) => handleDownloadProof(e)}
    >
      <DownloadIcon />
    </Button>
  );
}
