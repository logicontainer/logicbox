import { PencilIcon } from "lucide-react";
import { Button } from "./ui/button";
import { useProofStore } from "@/store/proofStore";
import { MouseEvent } from "react";

export default function RenameProofButton({ proofId }: { proofId: string }) {
  const getProof = useProofStore(state => state.getProof)
  const updateProofTitle = useProofStore((state) => state.updateProofTitle);
  function handleRenameProof(e: MouseEvent) {
    e.preventDefault();
    e.stopPropagation();
    const oldTitle = getProof(proofId)?.title
    const newTitle = prompt("Enter new title:", oldTitle)
    if (newTitle && newTitle !== oldTitle) {
      updateProofTitle(proofId, newTitle.trim())
    }
  }
  return (
    <Button
      variant="outline"
      title="Rename proof"
      onClick={(e) => handleRenameProof(e)}
    >
      <PencilIcon/>
    </Button>
  );
}
