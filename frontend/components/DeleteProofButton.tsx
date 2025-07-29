import { useProofStore } from "@/store/proofStore";
import { TrashIcon } from "lucide-react";
import { Button } from "./ui/button";

export default function DeleteProofButton({ proofId }: { proofId: string }) {
  const deleteProof = useProofStore((state) => state.deleteProof);
  const title = useProofStore(state => state.getProof)(proofId)?.title ?? ""

  return <Button
    variant="outline"
    className="hover:text-red-500"
    onMouseOver={(e) => e.stopPropagation()}
    title="Delete proof"
    onClick={(e) => {
      e.preventDefault();
      if (
        window.confirm(
          `Are you sure you want to delete the proof: ${title}`,
        )
      ) {
        deleteProof(proofId);
      }
    }}
  >
    <TrashIcon />
  </Button>
}
