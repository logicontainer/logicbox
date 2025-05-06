import {
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { Toolbar } from "radix-ui";
import { UpdateIcon } from "@radix-ui/react-icons";
import { cn } from "@/lib/utils";
import { useServer } from "@/contexts/ServerProvider";

export function ValidateProofButton({
  className = "",
}: {
  className?: string;
}) {
  const { doTransition } = useInteractionState();
  const { syncingStatus } = useServer();

  const handleValidateProof = () => {
    doTransition({ enum: TransitionEnum.VALIDATE_PROOF });
  };

  return (
    <Toolbar.ToolbarButton
      className={cn(className)}
      title="Validate proof by syncing with the server"
      onClick={handleValidateProof}
    >
      <div className={cn(syncingStatus == "syncing" ? "animate-spin" : "")}>
        <UpdateIcon className="h-5 w-5" />
      </div>
    </Toolbar.ToolbarButton>
  );
}
