import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";

export function ValidateProofButton() {
  const proofContext = useProof();
  const serverContext = useServer();
  const handleValidateProof = () => {
    serverContext.validateProof(proofContext.proof);
  };
  return (
    <button
      className="btn btn-primary border-2 border-slate-800 px-2"
      onClick={handleValidateProof}
    >
      Validate Proof (Syncing status: {serverContext.syncingStatus})
    </button>
  );
}
