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

const CONTEXT_MENU_ITEM_HEIGHT = 40
const CONTEXT_MENU_WIDTH = 192

export function ProofStepContextMenu() {
  const {
    contextMenuShouldBeVisible,
    contextMenuPosition: { x, y },
  } = useContextMenu();

  const { interactionState, doTransition } = useInteractionState();

  const handleItemClick = (id: string) => {
    switch (id) {
      case "edit-formula":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.EDIT_FORMULA,
        });
        break;
      case "edit-fresh-var":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.EDIT_FRESH_VAR,
        });
        break;
      case "remove-fresh-var":
        doTransition({
          enum: TransitionEnum.CLICK_CONTEXT_MENU_OPTION,
          option: ContextMenuOptions.REMOVE_FRESH_VAR,
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

  const numItemsShown = interactionState.isBox ? 5 : 4

  const [boundedX, boundedY] = (() => {
    let X = x;
    let Y = y;

    const rightEdge = X + CONTEXT_MENU_WIDTH
    if (rightEdge >= window.innerWidth) {
      X += window.innerWidth - rightEdge
    }

    const bottomEdge = Y + (numItemsShown * CONTEXT_MENU_ITEM_HEIGHT)
    if (bottomEdge >= window.innerHeight) {
      Y += window.innerHeight - bottomEdge
    }

    return [X, Y]
  })()

  return (
    <div
      className={
        `z-50 bg-white w-[${CONTEXT_MENU_WIDTH}px] rounded-md shadow-md shadow-slate-400 overflow-hidden select-none`
      }
      style={{
        position: "fixed",
        left: boundedX,
        top: boundedY,
      }}
    >
      {!interactionState.isBox && (
        <Item id="edit-formula" onClick={handleItemClick}>
          Edit
        </Item>
      )}
      {interactionState.isBox && (
        <>
        <Item id="edit-fresh-var" onClick={handleItemClick}>
          Edit fresh variable
        </Item>
        <Item id="remove-fresh-var" className="text-red-500" onClick={handleItemClick}>
          Remove fresh variable
        </Item>
        </>
      )}
      <hr />
      <Item className="flex justify-between gap-2 items-center">
        <div className="text"> Add line</div>
        <div className={"flex items-center"}>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={() => handleItemClick("line-above")}
          >
            <ArrowUpIcon className="inline-block" />
          </Button>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={() => handleItemClick("line-below")}
          >
            <ArrowDownIcon className="inline-block" />
          </Button>
        </div>
      </Item>
      <Item className="flex justify-between gap-2 items-center">
        <div className="text"> Add box</div>
        <div className={"flex items-center"}>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={() => handleItemClick("box-above")}
          >
            <ArrowUpIcon className="inline-block" />
          </Button>
          <Button
            variant={"ghost"}
            size="icon"
            className="flex justify-center items-center h-7 w-7"
            onClick={() => handleItemClick("box-below")}
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
      className={cn(`p-2 hover:bg-slate-200 cursor-pointer h-[${CONTEXT_MENU_ITEM_HEIGHT}px] select-none`, className)}
      onClick={(e) => {
        e.stopPropagation();
        if (id) onClick?.(id);
      }}
    >
      {children}
    </div>
  );
}
