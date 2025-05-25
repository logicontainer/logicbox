import { InlineMath } from "react-katex";
import { cn } from "@/lib/utils";

export default function FreshVars({
  value,
  isCurrentlyBeingChanged,
}: {
  value: string | null;
  isCurrentlyBeingChanged?: boolean;
}) {
  if (!value && !isCurrentlyBeingChanged) {
    return;
  }

  const onClick = (e: React.MouseEvent<HTMLSpanElement>) => {
    if (e.target !== e.currentTarget) {
      return;
    }
    e.stopPropagation();
    e.preventDefault();
  };

  return (
    <span
      className={cn(
        "absolute bg-white px-1 text-lg/tight ml-3 -translate-y-1/2 rounded-sm pointer-events-auto",
        "hover:bg-slate-100",
        isCurrentlyBeingChanged && "bg-blue-400 text-white"
      )}
      // onMouseMoveCapture={}
      onClickCapture={onClick}
    >
      <InlineMath math={value ?? ""}></InlineMath>
    </span>
  );
}
