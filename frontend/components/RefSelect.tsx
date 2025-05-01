import { useLines } from "@/contexts/LinesProvider";
import { cn } from "@/lib/utils";

export function RefSelect({
  value,
  onClick,
  isCurrentlyBeingChanged,
}: {
  value: string | null;
  onClick: () => void;
  isCurrentlyBeingChanged: boolean;
}) {
  const { getReferenceString } = useLines();

  const bg = isCurrentlyBeingChanged ? "bg-red-500" : "bg-slate-200"

  return (
    <div className={cn("p-2 rounded-md text-slate-800", bg)} onClick={_ => onClick()}>
      {value ? getReferenceString(value) : "?"}
    </div>
  );
}
