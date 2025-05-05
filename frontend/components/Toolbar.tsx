import * as React from "react";

import {
  ActivityLogIcon,
  CaretLeftIcon,
  CaretRightIcon,
} from "@radix-ui/react-icons";

import { Toolbar } from "radix-ui";
import { Tooltip } from "react-tooltip";
import { ValidateProofButton } from "./ValidateProofButton";
import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider";
import { useProof } from "@/contexts/ProofProvider";

function AppToolbar() {
  const historyContext = useHistory();
  const proofContext = useProof();
  const handleUndo = () => {
    historyContext.undo();
  };
  const handleRedo = () => {
    historyContext.redo();
  };
  return (
    <div className="flex gap-2">
      <Toolbar.Root
        className="flex w-full min-w-max rounded-md bg-white p-2.5 shadow-[0_2px_4px] shadow-blackA4 shadow-gray-600"
        aria-label="Formatting options"
      >
        <Toolbar.ToolbarButton
          className={cn(
            !historyContext.canUndo ? "text-slate-500 border-slate-500" : ""
          )}
          title="Undo latest action"
          onClick={handleUndo}
          disabled={!historyContext.canUndo}
        >
          <CaretLeftIcon />
        </Toolbar.ToolbarButton>
        <Toolbar.ToolbarButton
          className={cn(
            !historyContext.canRedo ? "text-slate-500 border-slate-500" : ""
          )}
          title="Redo latest action"
          onClick={handleRedo}
          disabled={!historyContext.canRedo}
        >
          <CaretRightIcon />
        </Toolbar.ToolbarButton>
        <Toolbar.Separator className="mx-2.5 w-px bg-gray-600" />
        <Toolbar.ToolbarButton
          title="Copy or paste proof as JSON"
          data-tooltip-id="proof-text-area"
        >
          <ActivityLogIcon />
        </Toolbar.ToolbarButton>
        <Toolbar.Separator className="mx-2.5 w-px bg-gray-600" />
        <ValidateProofButton />
      </Toolbar.Root>
      <Tooltip className="z-50" id="proof-text-area" clickable place="top">
        <textarea
          className="p-4 text-slate-800 bg-slate-200 opacity-100"
          value={JSON.stringify(proofContext.proof, null, 2)}
          onChange={(e) => proofContext.setStringProof(e.target.value)}
          rows={15}
          cols={50}
        />
      </Tooltip>
    </div>
  );
}

export default AppToolbar;
