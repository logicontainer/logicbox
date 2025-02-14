import { MinusIcon } from "./MinusIcon";
import { cn } from "@/lib/utils";

export function RemoveLineTooltip ({ uuid, isVisible }: { uuid: string, isVisible: boolean, prepend?: boolean }) {
  return (
    <>{isVisible && <div data-tooltip-id={`remove-line-${uuid}`} data-tooltip-offset={0} className={cn("h-4 w-4 bg-slate-200 absolute rounded-xs text-slate-600 z-10 left-[-15px] top-1/2 transform -translate-y-1/2 cursor-pointer hover:bg-red-500 hover:text-slate-200")}><MinusIcon />
    </div>}</>
  )
}