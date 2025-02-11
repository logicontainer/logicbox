import { LineNumberLine } from "@/types/types";

export function LineNumbers ({ lines }: { lines: LineNumberLine[] }) {
  if (!lines) return;

  return (
    <div className="flex-col items-start">
      {lines.filter((line) => !line.isBox).map((line) =>
        <p key={line.uuid} className="text-sm/10 text-left text-slate-800 align-baseline">{line.lineNumber}.</p>)}
    </div>
  )
}