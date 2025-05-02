import { Toolbar } from "radix-ui";
import { UpdateIcon } from "@radix-ui/react-icons";
import { cn } from "@/lib/utils";
import { TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { useServer } from "@/contexts/ServerProvider";

export function ValidateProofButton() {
  const { doTransition } = useInteractionState()
  const { syncingStatus } = useServer()

  const handleValidateProof = () => {
    doTransition({ enum: TransitionEnum.VALIDATE_PROOF })
  };

  return (
    <Toolbar.ToolbarButton
      title="Validate proof by syncing with the server"
      onClick={handleValidateProof}
    >
      <div
        className={cn(syncingStatus == "syncing" ? "animate-spin" : "")}
      >
        <UpdateIcon />
      </div>
    </Toolbar.ToolbarButton>
  );
}
