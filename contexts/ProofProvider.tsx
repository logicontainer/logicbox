"use client";

import { BoxProofStep, ProofStep } from "@/types/types";
import React, { useState } from "react";

import _ from "lodash";
import proofExample1 from "@/examples/proof-example-1";

interface ProofContextProps {
  proof: ProofStep[];
  lineInFocus: string | null,
  setLineInFocus: (uuid: string) => unknown,
  removeFocusFromLine: (uuid: string) => unknown
  setStringProof: (proof: string) => unknown
  addLine: (proofStep: ProofStep, uuid: string, append?: boolean) => unknown
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps>({
  proof: [],
  lineInFocus: null,
  setLineInFocus: () => { },
  removeFocusFromLine: () => { },
  setStringProof: () => { },
  addLine: (proofStep: ProofStep, uuid: string, append?: boolean) => { }
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
  const setStringProof = (stringProof: string) => {
    setProof(JSON.parse(stringProof))
  }
  const [lineInFocus, setLineInFocus] = useState<string | null>(null);
  const removeFocusFromLine = (uuid: string) => {
    if (lineInFocus == uuid) {
      setLineInFocus(null);
    }
  }

  const changeProofNearUuid = (
    proof: ProofStep[],
    uuid: string,
    newProofStep: ProofStep,
    actionAtIndex: (layer: ProofStep[], indexInCurrLayer: number) => void
  ): boolean => {
    const indexInCurrentLayer = proof.findIndex((proofStep) => proofStep.uuid == uuid);
    if (indexInCurrentLayer != -1) {
      actionAtIndex(proof, indexInCurrentLayer);
      return true;
    }
    const boxProofSteps: BoxProofStep[] = proof.filter((proofStep) => proofStep.stepType == "box") as unknown as BoxProofStep[];
    for (const boxProofStep of boxProofSteps) {
      if (changeProofNearUuid(boxProofStep.proof, uuid, newProofStep, actionAtIndex)) return true;
    }
    return false;
  }

  const addLine = (proofStep: ProofStep, uuid: string, append: boolean = false) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const insertProofStepAtUuid = (proof: ProofStep[], indexInCurrLayer: number) => {
        return proof.splice(indexInCurrLayer + (append ? 1 : 0), 0, proofStep);
      }
      changeProofNearUuid(
        newProof,
        uuid,
        proofStep,
        insertProofStepAtUuid
      )
      return newProof;
    })
  }

  return (
    <ProofContext.Provider value={{ proof, lineInFocus, setStringProof, setLineInFocus, removeFocusFromLine, addLine }}>
      {children}
    </ProofContext.Provider>
  );
}
