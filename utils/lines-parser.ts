import { BoxProofStep, LineNumberLine, ProofStep } from "@/types/types"
function extractLineUuids (proof: ProofStep[]): LineNumberLine[] {
  let lines = [] as LineNumberLine[];
  proof.forEach((proofStep: ProofStep) => {
    const newLine: LineNumberLine = { uuid: proofStep.uuid, isBox: false }
    if (proofStep.stepType == "line") {
      lines.push(newLine)
    } else {
      proofStep = proofStep as BoxProofStep;
      const subProofLines = extractLineUuids(proofStep.proof)
      newLine.isBox = true;
      newLine.boxStartLine = lines.length + 1;
      newLine.boxEndLine = newLine.boxStartLine + subProofLines.length - 1
      lines = [...lines, { ...newLine }, ...subProofLines]
    }
  })
  return lines
}
export function parseLinesFromProof (proof: ProofStep[]): LineNumberLine[] {
  const lines = extractLineUuids(proof)
  let num = 1;
  return lines.map((line) => {
    if (line.isBox) {
      return line;
    }
    return { ...line, lineNumber: num++ }
  })
} 
