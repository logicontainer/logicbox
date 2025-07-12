import * as React from "react";

import {
  ActivityLogIcon,
  CaretLeftIcon,
  CaretRightIcon,
} from "@radix-ui/react-icons";

import Card from "./Card";
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
      <Card className="p-0">
        <Toolbar.Root
          className="flex w-full min-w-max rounded-md bg-white "
          aria-label="Formatting options"
        >
          <Toolbar.ToolbarButton
            className={cn(
              "py-2 pl-2",
              historyContext.canUndo
                ? "hover:bg-gray-100"
                : "text-slate-500 border-slate-500 ",
            )}
            title="Undo latest action"
            onClick={handleUndo}
            disabled={!historyContext.canUndo}
          >
            <CaretLeftIcon className="w-8 h-8" />
          </Toolbar.ToolbarButton>
          <Toolbar.ToolbarButton
            className={cn(
              "py-2 pr-2",
              historyContext.canRedo
                ? "hover:bg-gray-100"
                : "text-slate-500 border-slate-500 ",
            )}
            title="Redo latest action"
            onClick={handleRedo}
            disabled={!historyContext.canRedo}
          >
            <CaretRightIcon className="w-8 h-8" />
          </Toolbar.ToolbarButton>
          <Toolbar.Separator className="w-px bg-gray-600 my-3 mx-1" />
          <Toolbar.ToolbarButton
            className="py-2 px-4 hover:bg-gray-100"
            title="Copy or paste proof as JSON"
            data-tooltip-id="proof-text-area"
          >
            <ActivityLogIcon className="w-4 h-4" />
          </Toolbar.ToolbarButton>
          <Toolbar.Separator className=" w-px bg-gray-600 my-3 mx-1" />
          <ValidateProofButton className="p-2 hover:bg-gray-100" />
        </Toolbar.Root>
      </Card>
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
