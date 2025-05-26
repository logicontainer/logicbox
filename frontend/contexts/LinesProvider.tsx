"use client";
import {
  TLineNumber,
} from "@/types/types";
import React from "react";

import _ from "lodash";
import { parseLinesFromProof } from "@/lib/lines-parser";
import { useProof } from "./ProofProvider";

export interface LinesContextProps {
  lines: TLineNumber[];
  getReferenceString: (uuid: string) => string | null;
}
// Context Setup
const LinesContext = React.createContext<LinesContextProps>({
  lines: [],
  getReferenceString: () => null,
});

export function useLines() {
  const context = React.useContext(LinesContext);
  if (!context) {
    throw new Error("useLines must be used within a LinesProvider");
  }
  return context;
}

export function LinesProvider({ children }: React.PropsWithChildren<object>) {
  const { proof } = useProof();
  const lines = parseLinesFromProof(proof.proof);
  const getReferenceString = (uuid: string) => {
    const line = lines.find((line) => line.uuid === uuid);
    if (line) {
      if (line.stepType === "box") {
        return `${line.boxStartLine}-${line.boxEndLine}`;
      } else {
        return JSON.stringify(line.lineNumber);
      }
    }
    return "?";
  };

  return (
    <LinesContext.Provider
      value={{
        lines,
        getReferenceString,
      }}
    >
      {children}
    </LinesContext.Provider>
  );
}
