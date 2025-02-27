import { BoxProofStep, LineNumberLine, ProofStep } from "@/types/types";

function extractLineUuids(
  proof: ProofStep[],
  currLineNumber: number = 1
): {
  lines: LineNumberLine[];
  boxes: LineNumberLine[];
  currLineNumber: number;
} {
  let lines = [] as LineNumberLine[];
  let boxes = [] as LineNumberLine[];
  proof.forEach((proofStep: ProofStep) => {
    const newLine: LineNumberLine = { uuid: proofStep.uuid, isBox: false };
    if (proofStep.stepType == "line") {
      newLine.lineNumber = currLineNumber;
      currLineNumber++;
      lines.push(newLine);
    } else {
      proofStep = proofStep as BoxProofStep;
      const {
        lines: subProofLines,
        boxes: subProofBoxes,
        currLineNumber: subCurrLineNumber,
      } = extractLineUuids(proofStep.proof, currLineNumber);
      newLine.isBox = true;
      newLine.boxStartLine = currLineNumber;
      newLine.boxEndLine = subCurrLineNumber - 1;
      currLineNumber = subCurrLineNumber;
      lines = [...lines, ...subProofLines];
      boxes = [...boxes, ...subProofBoxes, newLine];
    }
  });
  return { lines, boxes, currLineNumber };
}
export function parseLinesFromProof(proof: ProofStep[]): LineNumberLine[] {
  const { lines, boxes } = extractLineUuids(proof);
  return [...lines, ...boxes];
}
