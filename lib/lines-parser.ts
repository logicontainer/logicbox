import { BoxProofStep, LineNumberLine, ProofStep } from "@/types/types"
function extractLineUuids (proof: ProofStep[], openedBoxes: number = 0): LineNumberLine[] {
  let lines = [] as LineNumberLine[];
  proof.forEach((proofStep: ProofStep) => {
    const newLine: LineNumberLine = { uuid: proofStep.uuid, isBox: false }
    if (proofStep.stepType == "line") {
      lines.push(newLine)
    } else {
      proofStep = proofStep as BoxProofStep;
      openedBoxes++;
      const subProofLines = extractLineUuids(proofStep.proof, openedBoxes)
      newLine.isBox = true;
      newLine.boxStartLine = lines.length + openedBoxes;
      newLine.boxEndLine = newLine.boxStartLine + subProofLines.filter((line) => !line.isBox).length - 1;
      lines = [...lines, { ...newLine }, ...subProofLines]
    }
  })
  return lines
}
export function parseLinesFromProof (proof: ProofStep[]): LineNumberLine[] {
  const lines = extractLineUuids(proof)
  let lineCount = 1;
  return lines.map((line) => {
    if (line.isBox) {
      return line;
    }
    return { ...line, lineNumber: lineCount++ }
  })
} 
