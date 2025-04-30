import { useInteractionState } from "@/contexts/InteractionStateProvider";
import { useLines } from "@/contexts/LinesProvider";

export function RefSelect({
  value,
  onClick,
}: {
  value: string | null;
  onClick: () => void;
}) {
  const { getReferenceString } = useLines();

  return (
    <div className="p-2 rounded rounded-md bg-slate-200 text-slate-800" onClick={_ => onClick()}>
      {value ? getReferenceString(value) : "?"}
    </div>
  );
}
