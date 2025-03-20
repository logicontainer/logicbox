import { Diagnostic, LineNumberLine } from "@/types/types";

import { Tooltip } from "react-tooltip";
import { cn } from "@/lib/utils";
import { useContextMenu } from "react-contexify";
import { useProof } from "@/contexts/ProofProvider";

export function LineNumber({
  line,
  proofStepDiagnostics,
}: {
  line: LineNumberLine;
  proofStepDiagnostics?: Diagnostic;
}) {
  const { isUnfocused } = useProof();

  const { show } = useContextMenu({
    id: "proof-step-context-menu",
  });

  function handleContextMenu(
    event:
      | React.MouseEvent<HTMLElement>
      | React.TouchEvent<HTMLElement>
      | React.KeyboardEvent<HTMLElement>
      | KeyboardEvent
  ) {
    show({
      event,
      props: {
        uuid: line.uuid,
      },
    });
  }

  return (
    <>
      <p
        className={cn(
          "text-sm/10 text-left text-slate-800 align-baseline cursor-pointer px-2",
          isUnfocused(line.uuid) ? "text-slate-400" : "",
          proofStepDiagnostics
            ? "bg-red-500 text-slate-200"
            : "bg-green-500 text-slate-200"
        )}
        data-tooltip-content={proofStepDiagnostics?.violationType}
        data-tooltip-id={
          proofStepDiagnostics ? `line-number-tooltip-${line.uuid}` : ""
        }
        onClick={handleContextMenu}
      >
        {line.lineNumber}.
      </p>
      <Tooltip className="z-50" id={`line-number-tooltip-${line.uuid}`} />
    </>
  );
}
