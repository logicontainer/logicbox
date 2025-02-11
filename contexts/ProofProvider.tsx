"use client";

import React, { useState } from "react";

import { ProofStep } from "@/types/types";
import proofExample1 from "@/examples/proof-example-1";

interface ProofContextProps {
  proof: ProofStep[];
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps>({
  proof: []
});

export function useProof () {
  const context = React.useContext(ProofContext);
  if (!context) {
    throw new Error("useProof must be used within a ProofProvider");
  }
  return context;
}

export function ProofProvider ({ children }: React.PropsWithChildren<{}>) {
  const [proof, setproof] = useState(proofExample1.proof);
  return (
    <ProofContext.Provider value={{ proof }}>
      {children}
    </ProofContext.Provider>
  );
}
