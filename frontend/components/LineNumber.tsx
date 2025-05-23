import { Diagnostic, TLineNumber } from "@/types/types";

import { cn } from "@/lib/utils";

export function LineNumber({
  line,
  proofStepDiagnostics,
  className,
}: {
  line: TLineNumber & {
    stepType: "line";
  };
  proofStepDiagnostics?: Diagnostic;
  className?: string;
}) {
  return (
    <>
      <p
        className={cn(
          "text-sm/10 text-center text-slate-800 align-baseline cursor-pointer px-2",
          proofStepDiagnostics
            ? "bg-red-500 text-slate-200"
            : "bg-green-500 text-slate-200",
          className
        )}
        data-tooltip-content={proofStepDiagnostics?.violationType}
        data-tooltip-id={
          proofStepDiagnostics ? `line-number-tooltip-${line.uuid}` : ""
        }
      >
        <span>{line.stepType === "line" && line.lineNumber}.</span>
      </p>
    </>
  );
}
