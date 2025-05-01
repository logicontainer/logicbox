import { Toolbar } from "radix-ui";
import { UpdateIcon } from "@radix-ui/react-icons";
import { cn } from "@/lib/utils";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";
import { TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";

export function ValidateProofButton() {
  const proofContext = useProof();
  const serverContext = useServer();
  const { doTransition } = useInteractionState()

  const handleValidateProof = () => {
    serverContext.validateProof(proofContext.proof);
    doTransition({ enum: TransitionEnum.CLICK_OUTSIDE })
    throw Error("THIS DOESN'T WORK, RACES WITH THINGS WHEN YOU UPDATE THEM, NEEDS TO BE HANDLED BY INTERACTIONSTATE")
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
