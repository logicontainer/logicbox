import { PlusIcon } from "./PlusIcon";
import { cn } from "@/lib/utils";

export function AddLineTooltip ({ uuid, isVisible, prepend }: { uuid: string, isVisible: boolean, prepend?: boolean }) {
  return (
    <>{isVisible && <div data-tooltip-id={`add-line-${uuid}`} data-tooltip-offset={0} data-tooltip-content={JSON.stringify({ prepend })} className={cn("h-4 w-4 bg-slate-200 absolute rounded-xs text-slate-600 z-10", prepend ? "top-[-8px]" : "bottom-[-8px]")}><PlusIcon />
    </div>}</>
  )
}