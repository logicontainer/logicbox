"use client";

import React, { useState } from "react";

import { ProofStep } from "@/types/types";
import proofExample1 from "@/examples/proof-example-1";

interface ProofContextProps {
  proof: ProofStep[];
  lineInFocus: string | null,
  setLineInFocus: (uuid: string) => unknown,
  removeFocusFromLine: (uuid: string) => unknown
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps>({
  proof: [],
  lineInFocus: null,
  setLineInFocus: () => { },
  removeFocusFromLine: () => { }
});

export function useProof () {
  const context = React.useContext(ProofContext);
  if (!context) {
    throw new Error("useProof must be used within a ProofProvider");
  }
  return context;
}

export function ProofProvider ({ children }: React.PropsWithChildren<object>) {
  const [proof, setProof] = useState(proofExample1.proof);
  const [lineInFocus, setLineInFocus] = useState<string | null>(null);
  const removeFocusFromLine = (uuid: string) => {
    if (lineInFocus == uuid) {
      setLineInFocus(null);
    }
  }
  return (
    <ProofContext.Provider value={{ proof, lineInFocus, setLineInFocus, removeFocusFromLine }}>
      {children}
    </ProofContext.Provider>
  );
}
