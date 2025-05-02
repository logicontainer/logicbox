import { ArrowDownIcon, ArrowUpIcon } from "@radix-ui/react-icons";
import {
  ContextMenuOptions,
  useContextMenu,
} from "@/contexts/ContextMenuProvider";
import {
  InteractionStateEnum,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { Button } from "./ui/button";
import { cn } from "@/lib/utils";

export function ProofStepContextMenu() {
  const {
    contextMenuShouldBeVisible,
    contextMenuPosition: { x, y },
  } = useContextMenu();

  const { interactionState, doTransition } = useInteractionState();

  const handleItemClick = (id: string) => {
    switch (id) {
      case "edit":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.EDIT,
        });
        break;
      case "line-above":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.LINE_ABOVE,
        });
        break;
      case "line-below":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.LINE_BELOW,
        });
        break;
      case "box-above":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.BOX_ABOVE,
        });
        break;
      case "box-below":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.BOX_BELOW,
        });
        break;
      case "delete":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.DELETE,
        });
        break;
    }
  };

  if (interactionState.enum !== InteractionStateEnum.VIEWING_CONTEXT_MENU) {
    return null;
  }

  if (!contextMenuShouldBeVisible) {
    return null;
  }

  return (
    <div
      className={
        "absolute z-50 bg-white min-w-48 rounded-md shadow-md shadow-slate-400 overflow-hidden"
      }
      style={{ top: y, left: x }}
    >
      {!interactionState.isBox && (
        <Item id="edit" onClick={handleItemClick}>
          Edit
        </Item>
      )}
      <hr />
      <Item
        className="flex justify-between gap-2 items-center"
      >
        <div className="text"> Add line</div>
        <div className={"flex items-center"}>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={_ => handleItemClick("line-above")}
          >
            <ArrowUpIcon className="inline-block" />
          </Button>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={_ => handleItemClick("line-below")}
          >
            <ArrowDownIcon className="inline-block" />
          </Button>
        </div>
      </Item>
      <Item
        className="flex justify-between gap-2 items-center"
      >
        <div className="text"> Add box</div>
        <div className={"flex items-center"}>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={_ => handleItemClick("box-above")}
          >
            <ArrowUpIcon className="inline-block" />
          </Button>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={_ => handleItemClick("box-below")}
          >
            <ArrowDownIcon className="inline-block" />
          </Button>
        </div>
      </Item>
      <hr />
      <Item id="delete" className="text-red-500" onClick={handleItemClick}>
        Delete
      </Item>
    </div>
  );
}

function Item({
  onClick,
  className,
  children,
  id,
}: {
  children: React.ReactNode;
  id?: string;
  className?: string;
  onClick?: (id: string) => void;
}) {
  return (
    <div
      className={cn("p-2 hover:bg-slate-200 cursor-pointer h-10", className)}
      onClick={(e) => {
        e.stopPropagation();
        id && onClick?.(id);
      }}
    >
      {children}
    </div>
  );
}
