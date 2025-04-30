import { useLines } from "@/contexts/LinesProvider";

export function RefSelect({
  value,
  onChange,
}: {
  value: string | null;
  onChange: (uuid: string) => void;
}) {
  const { getReferenceString } = useLines();
  return (
    <div className="p-2 rounded rounded-md bg-slate-200 text-slate-800">
      {getReferenceString(value)}
    </div>
  );
}
