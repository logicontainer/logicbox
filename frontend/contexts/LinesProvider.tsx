"use client";
import { TLineNumber } from "@/types/types";
import React from "react";

import _ from "lodash";
import { parseLinesFromProof } from "@/lib/lines-parser";
import { useProof } from "./ProofProvider";

export interface LinesContextProps {
  lines: TLineNumber[];
}
// Context Setup
const LinesContext = React.createContext<LinesContextProps>({
  lines: [],
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

  return (
    <LinesContext.Provider
      value={{
        lines,
      }}
    >
      {children}
    </LinesContext.Provider>
  );
}
