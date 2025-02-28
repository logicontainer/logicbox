import { Toolbar } from "radix-ui";
import { UpdateIcon } from "@radix-ui/react-icons";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";

export function ValidateProofButton() {
  const proofContext = useProof();
  const serverContext = useServer();
  const handleValidateProof = () => {
    serverContext.validateProof(proofContext.proof);
  };
  return (
    <Toolbar.ToolbarButton
      title="Validate proof by syncing with the server"
      onClick={handleValidateProof}
    >
      <div
        className={cn(
          serverContext.syncingStatus == "syncing" ? "animate-spin" : ""
        )}
      >
        <UpdateIcon />
      </div>
    </Toolbar.ToolbarButton>
  );
}
