"use client";

import { BoxProofStep, ProofStep } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import proofExample1 from "@/examples/proof-example-1";

export interface ProofContextProps {
  proof: ProofStep[];
  lineInFocus: string | null,
  latestLineInFocus: string | null,
  isFocused: (uuid: string) => boolean,
  isUnfocused: (uuid: string) => boolean,
  setLineInFocus: (uuid: string) => unknown,
  removeFocusFromLine: (uuid: string) => unknown
  setStringProof: (proof: string) => unknown
  addLine: (proofStep: ProofStep, uuid: string, prepend?: boolean) => unknown
  removeLine: (uuid: string) => unknown
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps>({
  proof: [],
  lineInFocus: null,
  latestLineInFocus: null,
  isFocused: () => false,
  isUnfocused: () => false,
  setLineInFocus: () => { },
  removeFocusFromLine: () => { },
  setStringProof: () => { },
  addLine: () => { },
  removeLine: () => { }
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
  const [latestLineInFocus, setLatestLineInFocus] = useState<string | null>(null);
  useEffect(() => {
    if (lineInFocus) { setLatestLineInFocus(lineInFocus) }
  }, [lineInFocus])
  const removeFocusFromLine = (uuid: string) => {
    if (lineInFocus == uuid) {
      setLineInFocus(null);
    }
  }

  const changeProofNearUuid = (
    proof: ProofStep[],
    uuid: string,
    actionAtIndex: (layer: ProofStep[], indexInCurrLayer: number) => void
  ): boolean => {
    const indexInCurrentLayer = proof.findIndex((proofStep) => proofStep.uuid == uuid);
    if (indexInCurrentLayer != -1) {
      actionAtIndex(proof, indexInCurrentLayer);
      return true;
    }
    const boxProofSteps: BoxProofStep[] = proof.filter((proofStep) => proofStep.stepType == "box") as unknown as BoxProofStep[];
    for (const boxProofStep of boxProofSteps) {
      if (changeProofNearUuid(boxProofStep.proof, uuid, actionAtIndex)) return true;
    }
    return false;
  }

  const addLine = (proofStep: ProofStep, uuid: string, prepend: boolean = false) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const insertProofStepAtUuid = (proof: ProofStep[], indexInCurrLayer: number) => {
        return proof.splice(indexInCurrLayer + (prepend ? 0 : 1), 0, proofStep);
      }
      changeProofNearUuid(
        newProof,
        uuid,
        insertProofStepAtUuid
      )
      return newProof;
    })
  }

  const removeLine = (uuid: string) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const removeProofStepAtUuid = (proof: ProofStep[], indexInCurrLayer: number) => {
        return proof.splice(indexInCurrLayer, 1);
      }
      changeProofNearUuid(
        newProof,
        uuid,
        removeProofStepAtUuid
      )
      return newProof;
    })
  }


  const isFocused = (uuid: string) => {
    if (!uuid) return false;
    return uuid == lineInFocus
  }

  const isUnfocused = (uuid: string) => {
    if (!uuid) return false;
    if (!lineInFocus) return false;
    return uuid != lineInFocus
  }

  return (
    <ProofContext.Provider value={{ proof, lineInFocus, latestLineInFocus, isFocused, isUnfocused, setStringProof, setLineInFocus, removeFocusFromLine, addLine, removeLine }}>
      {children}
    </ProofContext.Provider>
  );
}

// isDraft?: boolean,
// hasChanges ?: boolean
// isActiveEdit?: boolean
// Should use Command pattern and history
