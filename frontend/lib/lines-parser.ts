import { BoxProofStep, ProofStep, TLineNumber } from "@/types/types";

function extractLineUuids(
  proof: ProofStep[],
  currLineNumber: number = 1
): {
  lines: TLineNumber[];
  boxes: TLineNumber[];
  currLineNumber: number;
} {
  let lines = [] as TLineNumber[];
  let boxes = [] as TLineNumber[];

  proof.forEach((proofStep: ProofStep) => {
    if (proofStep.stepType == "line") {
      lines.push({
        uuid: proofStep.uuid,
        stepType: "line",
        lineNumber: currLineNumber,
      });
      currLineNumber++;
    } else if (proofStep.stepType === "box") {
      proofStep = proofStep as BoxProofStep;
      const {
        lines: subProofLines,
        boxes: subProofBoxes,
        currLineNumber: subCurrLineNumber,
      } = extractLineUuids(proofStep.proof, currLineNumber);
      const newLine = {
        uuid: proofStep.uuid,
        stepType: "box" as "box",
        boxStartLine: currLineNumber,
        boxEndLine: subCurrLineNumber - 1,
      };
      currLineNumber = subCurrLineNumber;
      lines = [...lines, ...subProofLines];
      boxes = [...boxes, ...subProofBoxes, newLine];
    } else throw new Error("unreachable");
  });

  return { lines, boxes, currLineNumber };
}
export function parseLinesFromProof(proof: ProofStep[]): TLineNumber[] {
  const { lines, boxes } = extractLineUuids(proof);
  return [...lines, ...boxes];
}
