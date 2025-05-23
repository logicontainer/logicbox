import { cn } from "@/lib/utils";
import { useLines } from "@/contexts/LinesProvider";

type RefSelectProps = {
  value: string | null;
  onClick: (e: React.MouseEvent<HTMLDivElement>) => void;
  isCurrentlyBeingChanged: boolean;
};

export function RefSelect({
  value,
  onClick,
  isCurrentlyBeingChanged,
}: RefSelectProps) {
  const { getReferenceString } = useLines();

  const bg = isCurrentlyBeingChanged
    ? "bg-blue-400 text-white"
    : "bg-slate-200";

  return (
    <div className={cn("px-2 rounded-md text-slate-800", bg)} onClick={onClick}>
      {value ? getReferenceString(value) : "?"}
    </div>
  );
}
