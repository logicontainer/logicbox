import { useBackend } from "@/contexts/BackendProvider";
import { useLaTeX } from "@/contexts/LaTeXProvider";
import { useProof } from "@/contexts/ProofProvider";
import { CheckIcon, XIcon } from "lucide-react";

export default function ProofValidityIcon() {
  const { proofDiagnostics } = useBackend();
  const hasErrors = proofDiagnostics.length != 0;
  return (
    <div className="flex items-center justify-center self-stretch min-w-8 p-2 h-full">
      {hasErrors ? (
        <div
          className="flex items-center justify-around text-red-500 gap-1"
          title="The proof is invalid"
        >
          <p className="hidden md:block">Invalid</p>
          <XIcon />
        </div>
      ) : (
        <div
          className="flex items-center justify-around text-green-500 gap-1"
          title="The proof is valid"
        >
          <p className="hidden md:block">Valid</p>
          <CheckIcon />
        </div>
      )}
    </div>
  );
}
