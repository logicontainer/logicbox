import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { CheckIcon, XIcon } from "lucide-react";

export default function ProofValidityIcon() {
  const { diagnostics } = useDiagnostics();
  const hasErrors = diagnostics.length != 0;
  return (
    <div className="flex items-center justify-center self-stretch min-w-8 p-2 h-full">
      {hasErrors ? (
        <div
          className="flex items-center justify-around text-red-500 gap-1"
          title="The proof is invalid"
        >
          <p>Invalid</p>
          <XIcon />
        </div>
      ) : (
        <div
          className="flex items-center justify-around text-green-500 gap-1"
          title="The proof is valid"
        >
          <p>Valid</p>
          <CheckIcon />
        </div>
      )}
    </div>
  );
}
