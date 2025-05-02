import { cn } from "@/lib/utils";
import { useLines } from "@/contexts/LinesProvider";

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

  const bg = isCurrentlyBeingChanged
    ? "bg-blue-400 text-white"
    : "bg-slate-200";

  return (
    <div
      className={cn("px-2 rounded-md text-slate-800", bg)}
      onClick={() => onClick()}
    >
      {value ? getReferenceString(value) : "?"}
    </div>
  );
}
