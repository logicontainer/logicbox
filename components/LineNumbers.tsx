import { LineNumber } from "./LineNumber";
import { LineNumberLine } from "@/types/types";

export function LineNumbers ({ lines }: { lines: LineNumberLine[] }) {
  if (!lines) return;

  return (
    <div className="flex-col items-start">
      {lines.filter((line) => !line.isBox).map((line) => {
        return (
          <LineNumber key={line.uuid} line={line} />);
      })}
    </div>
  )
}