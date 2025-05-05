import { Diagnostic, ViolationType } from "@/types/types";

export function formulaIsWrong(diagnostics: Diagnostic[]) {
  return diagnostics.find(d => d.violationType === "missingFormula") !== undefined
}
