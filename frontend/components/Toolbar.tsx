import * as React from "react";

import { CaretLeftIcon, CaretRightIcon } from "@radix-ui/react-icons";

import Card from "./Card";
import { Toolbar } from "radix-ui";
import { ValidateProofButton } from "./ValidateProofButton";
import { cn } from "@/lib/utils";
import { useHistory } from "@/contexts/HistoryProvider";
import ProofValidityIcon from "./ProofValidityIcon";

function AppToolbar() {
  const historyContext = useHistory();
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
          <ValidateProofButton className="p-2 hover:bg-gray-100" />
          <Toolbar.ToolbarButton className="cursor-auto">
            <ProofValidityIcon />
          </Toolbar.ToolbarButton>
        </Toolbar.Root>
      </Card>
    </div>
  );
}

export default AppToolbar;
