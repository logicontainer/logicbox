"use client";

import { BoxProofStep, LineProofStep, ProofStep, ProofStepDetails, ProofStepPosition } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import proofExample1 from "@/examples/proof-example-1";

export interface ProofContextProps {
  proof: ProofStep[];
  lineInFocus: string | null,
  latestLineInFocus: string | null,
  isFocused: (uuid: string) => boolean,
  isUnfocused: (uuid: string) => boolean,
  isActiveEdit: (uuid: string) => boolean,
  removeIsActiveEditFromLine: (uuid: string) => unknown,
  setActiveEdit: (uuid: string) => unknown,
  setLineInFocus: (uuid: string) => unknown,
  removeFocusFromLine: (uuid: string) => unknown
  setStringProof: (proof: string) => unknown
  addLine: (proofStep: ProofStep, position: ProofStepPosition) => unknown
  removeLine: (uuid: string) => unknown
  updateLine: (uuid: string, updatedLineProofStep: LineProofStep) => unknown
  getProofStepDetails: (uuid: string) => (ProofStepDetails & { isOnlyChildInBox: boolean }) | null
  getNearestDeletableProofStep: (uuid: string) => { proofStepDetails: ProofStepDetails | null, cascadeCount: number }
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps>({
  proof: [],
  lineInFocus: null,
  latestLineInFocus: null,
  isFocused: () => false,
  isUnfocused: () => false,
  isActiveEdit: () => false,
  removeIsActiveEditFromLine: () => { },
  setActiveEdit: () => { },
  setLineInFocus: () => { },
  removeFocusFromLine: () => { },
  setStringProof: () => { },
  addLine: () => { },
  removeLine: () => { },
  updateLine: () => { },
  getProofStepDetails: () => null,
  getNearestDeletableProofStep: () => { return { proofStepDetails: null, cascadeCount: 0 } },
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
  const [activeEdit, setActiveEdit] = useState<string | null>("1");
  const [latestLineInFocus, setLatestLineInFocus] = useState<string | null>(null);
  useEffect(() => {
    if (lineInFocus) { setLatestLineInFocus(lineInFocus) }
  }, [lineInFocus])
  const removeFocusFromLine = (uuid: string) => {
    if (lineInFocus == uuid) {
      setLineInFocus(null);
    }
  }
  const removeIsActiveEditFromLine = (uuid: string) => {
    if (activeEdit == uuid) {
      setActiveEdit(null);
    }
  }

  const interactWithProofNearUuid = (
    proof: ProofStep[],
    uuid: string,
    parentBox: BoxProofStep | null,
    actionAtIndex: (layer: ProofStep[], indexInCurrLayer: number, parentBox: BoxProofStep | null) => void
  ): boolean => {
    const indexInCurrentLayer = proof.findIndex((proofStep) => proofStep.uuid == uuid);
    if (indexInCurrentLayer != -1) {
      actionAtIndex(proof, indexInCurrentLayer, parentBox);
      return true;
    }
    const boxProofSteps: BoxProofStep[] = proof.filter((proofStep) => proofStep.stepType == "box") as unknown as BoxProofStep[];
    for (const boxProofStep of boxProofSteps) {
      if (interactWithProofNearUuid(boxProofStep.proof, uuid, boxProofStep, actionAtIndex)) return true;
    }
    return false;
  }

  const addLine = (proofStep: ProofStep, position: ProofStepPosition) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const insertProofStepAtUuid = (proof: ProofStep[], indexInCurrLayer: number, parentBox: BoxProofStep | null) => {
        return proof.splice(indexInCurrLayer + (position.prepend ? 0 : 1), 0, proofStep);
      }
      interactWithProofNearUuid(
        newProof,
        position.nearProofStepWithUuid,
        null,
        insertProofStepAtUuid
      )
      return newProof;
    })
  }

  const removeLine = (uuid: string) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const removeProofStepAtUuid = (proof: ProofStep[], indexInCurrLayer: number, parentBox: BoxProofStep | null) => {
        return proof.splice(indexInCurrLayer, 1);
      }
      interactWithProofNearUuid(
        newProof,
        uuid,
        null,
        removeProofStepAtUuid
      )
      return newProof;
    })
  }

  const updateLine = (uuid: string, updatedLineProofStep: ProofStep) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const updateProofStepAtUuid = (proof: ProofStep[], indexInCurrLayer: number, parentBox: BoxProofStep | null) => {
        proof[indexInCurrLayer] = updatedLineProofStep;
      }
      interactWithProofNearUuid(
        newProof,
        uuid,
        null,
        updateProofStepAtUuid
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

  const isActiveEdit = (uuid: string) => {
    if (!uuid) return false;
    return uuid == activeEdit;
  }

  const getProofStepDetails = (uuid: string): (ProofStepDetails & { isOnlyChildInBox: boolean }) | null => {
    let proofStepDetails = {} as (ProofStepDetails & { isOnlyChildInBox: boolean }) | null
    const extractProofStepDetails = (proof: ProofStep[], indexInCurrLayer: number, parentBox: BoxProofStep | null) => {
      const isOnlyChildInBox = proof ? proof.length == 1 : false;
      if (isOnlyChildInBox) {
        proofStepDetails = {
          proofStep: proof[0],
          parentBoxUuid: parentBox ? parentBox.uuid : "",
          isOnlyChildInBox: true,
          position: {
            prepend: false,
            nearProofStepWithUuid: "",
          }
        }
      } else {
        proofStepDetails = {
          proofStep: proof[indexInCurrLayer],
          parentBoxUuid: parentBox ? parentBox.uuid : "",
          isOnlyChildInBox: false,
          position: {
            prepend: indexInCurrLayer == 0,
            nearProofStepWithUuid: indexInCurrLayer == 0 ? proof[1].uuid : proof[indexInCurrLayer - 1].uuid,
          }
        }
      }
      return proof
    }
    interactWithProofNearUuid(
      proof,
      uuid,
      null,
      extractProofStepDetails
    )
    return proofStepDetails;
  }

  const getNearestDeletableProofStep = (uuid: string): { proofStepDetails: ProofStepDetails | null, cascadeCount: number } => {
    let cascadeCount = 0;
    const findNearestDeletableProofStepCascade = (uuid: string) => {
      let proofStepDetails = getProofStepDetails(uuid);
      if (!proofStepDetails) return null;
      if (proofStepDetails.isOnlyChildInBox) {
        if (proofStepDetails.parentBoxUuid == null) return null;
        cascadeCount++;
        return findNearestDeletableProofStepCascade(proofStepDetails.parentBoxUuid);
      }
      return proofStepDetails;
    }
    const proofStepDetails = findNearestDeletableProofStepCascade(uuid);
    return { proofStepDetails, cascadeCount };
  }

  return (
    <ProofContext.Provider value={{ proof, lineInFocus, latestLineInFocus, isFocused, isUnfocused, isActiveEdit, removeIsActiveEditFromLine, setActiveEdit: setActiveEdit, setStringProof, setLineInFocus, removeFocusFromLine, addLine, removeLine, updateLine, getProofStepDetails, getNearestDeletableProofStep }}>
      {children}
    </ProofContext.Provider>
  );
}

// isDraft?: boolean,
// hasChanges ?: boolean
// isActiveEdit?: boolean
// Should use Command pattern and history
