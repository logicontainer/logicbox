import { BoxProofStep, LineNumberLine, ProofStep } from "@/types/types"
function extractLineUuids (proof: ProofStep[]): { uuid: string }[] {
  let lines = [] as { uuid: string }[];
  proof.forEach((proofStep: ProofStep) => {
    if (proofStep.stepType == "line") {
      lines.push({ uuid: proofStep.uuid })
    } else {
      proofStep = proofStep as BoxProofStep;
      lines = [...lines, ...extractLineUuids(proofStep.proof)]
    }
  })
  return lines
}
export function parseLinesFromProof (proof: ProofStep[]): LineNumberLine[] {
  const lines = extractLineUuids(proof)
  return lines.map((line, i) => {
    return { ...line, lineNumber: i + 1 }
  })
} 
