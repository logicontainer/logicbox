"use client";

import {
  BoxProofStep,
  LineProofStep,
  ProofWithMetadata,
  ProofStep,
  ProofStepDetails,
  ProofStepPosition,
  Proof,
} from "@/types/types";
import React from "react";

import _ from "lodash";
import { useProofStore } from "@/store/proofStore";

export interface ProofContextProps {
  proof: ProofWithMetadata;

  loadProofFromId: (id: string) => void;
  setProofContent: (proof: Proof) => void;

  setStringProof: (proof: string) => unknown;
  addStep: (proofStep: ProofStep, position: ProofStepPosition) => unknown;
  removeStep: (uuid: string) => unknown;
  updateLine: (uuid: string, updatedLineProofStep: LineProofStep) => unknown;
  isDescendant: (parentUuid: string, childUuid: string) => boolean;
  isOnlyChild: (stepUuid: string) => boolean;
  getParentUuid: (stepUuid: string) => string | null;
  getNeighbour: (stepUuid: string) => ProofStepPosition | null;
  updateFreshVarOnBox: (uuid: string, freshVar: string | null) => unknown;
  getProofStepDetails: (
    uuid: string,
  ) => (ProofStepDetails & { isOnlyChildInBox: boolean }) | null;
  getNearestDeletableProofStep: (uuid: string) => {
    proofStepDetails: ProofStepDetails | null;
    cascadeCount: number;
  };
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps | null>(null);

export function useProof() {
  const context = React.useContext(ProofContext);
  if (!context) {
    throw new Error("useProof must be used within a ProofProvider");
  }
  return context;
}

const FALLBACK_PROOF: ProofWithMetadata = {
  id: "fallback_proof",
  createdAt: "2025-06-06T00:00:00.000Z",
  title: "YOU SHOULD NOT BE SEEING THIS!",
  logicName: "propositionalLogic",
  proof: [],
};

export function ProofProvider({ children }: React.PropsWithChildren<object>) {
  const [proofId, setProofId] = React.useState<string | null>(null);
  const { updateProofContent, getProof } = useProofStore();
  const proof = (proofId !== null && getProof(proofId)) || FALLBACK_PROOF;

  const setProofContent = (updater: (_: Proof) => Proof) => {
    if (!proofId) {
      console.warn("Can't update proof content, as proofId is null");
      return;
    }
    updateProofContent(proofId, updater);
  };

  const setStringProof = (stringProof: string) => {
    setProofContent(JSON.parse(stringProof));
  };

  const interactWithProofNearUuid = (
    proof: Proof,
    uuid: string,
    parentBox: BoxProofStep | null,
    actionAtIndex: (
      layer: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null,
    ) => void,
  ): boolean => {
    const indexInCurrentLayer = proof.findIndex(
      (proofStep) => proofStep.uuid == uuid,
    );
    if (indexInCurrentLayer != -1) {
      actionAtIndex(proof, indexInCurrentLayer, parentBox);
      return true;
    }
    const boxProofSteps: BoxProofStep[] = proof.filter(
      (proofStep) => proofStep.stepType == "box",
    ) as unknown as BoxProofStep[];
    for (const boxProofStep of boxProofSteps) {
      if (
        interactWithProofNearUuid(
          boxProofStep.proof,
          uuid,
          boxProofStep,
          actionAtIndex,
        )
      )
        return true;
    }
    return false;
  };

  const addStep = (proofStep: ProofStep, position: ProofStepPosition) => {
    setProofContent((prev) => {
      const newProof = _.cloneDeep(prev);
      const insertProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null,
      ) => {
        return proof.splice(
          indexInCurrLayer + (position.prepend ? 0 : 1),
          0,
          proofStep,
        );
      };
      interactWithProofNearUuid(
        newProof,
        position.nearProofStepWithUuid,
        null,
        insertProofStepAtUuid,
      );
      return newProof;
    });
  };

  const removeStep = (uuid: string) => {
    setProofContent((prev) => {
      const newProof = _.cloneDeep(prev);
      const removeProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null,
      ) => {
        return proof.splice(indexInCurrLayer, 1);
      };
      interactWithProofNearUuid(newProof, uuid, null, removeProofStepAtUuid);
      return newProof;
    });
  };

  const updateLine = (uuid: string, updatedLineProofStep: ProofStep) => {
    setProofContent((prev) => {
      const newProof = _.cloneDeep(prev);
      const updateProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null,
      ) => {
        proof[indexInCurrLayer] = updatedLineProofStep;
      };
      interactWithProofNearUuid(newProof, uuid, null, updateProofStepAtUuid);
      return newProof;
    });
  };

  const isOnlyChild = (stepUuid: string): boolean => {
    let result = true
    const visit = (
      proof: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null,
    ) => {
      result = proof.length === 1
    };
    interactWithProofNearUuid(proof.proof, stepUuid, null, visit)
    return result;
  }

  const getParentUuid = (stepUuid: string): string | null => {
    let result: string | null = null
    const visit = (
      proof: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null,
    ) => {
      result = parentBox?.uuid ?? null
    };
    interactWithProofNearUuid(proof.proof, stepUuid, null, visit)
    return result
  }

  const isDescendant = (parentUuid: string, targetUuid: string): boolean => {
    const parent = getProofStepDetails(parentUuid)?.proofStep
    if (parent?.stepType !== "box") 
      return false

    return parent.proof.some(
      ({ uuid }) => uuid === targetUuid || isDescendant(uuid, targetUuid)
    )
  }

  const getNeighbour = (stepUuid: string): ProofStepPosition | null => {
    let result: ProofStepPosition | null = null
    const visit = (
      proof: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null,
    ) => {
      if (proof.length <= 1) // no valid nearby position, step is only child
        return;

      if (indexInCurrLayer === proof.length - 1)
        result = { nearProofStepWithUuid: proof[proof.length - 2].uuid, prepend: false }  satisfies ProofStepPosition
      else result = {
        nearProofStepWithUuid: proof[indexInCurrLayer + 1].uuid, prepend: true
      }
    };
    interactWithProofNearUuid(proof.proof, stepUuid, null, visit)
    return result
  }

  const updateFreshVarOnBox = (uuid: string, freshVar: string | null) => {
    setProofContent((prev) => {
      const newProof = _.cloneDeep(prev);
      const updateProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null,
      ) => {
        if (proof[indexInCurrLayer].stepType !== "box")
          throw new Error(
            `Attempted to update fresh var on ${uuid} - not a box`,
          );

        proof[indexInCurrLayer].boxInfo.freshVar = freshVar;
      };
      interactWithProofNearUuid(newProof, uuid, null, updateProofStepAtUuid);
      return newProof;
    });
  };

  const getProofStepDetails = (
    uuid: string,
  ): (ProofStepDetails & { isOnlyChildInBox: boolean }) | null => {
    let proofStepDetails = {} as
      | (ProofStepDetails & { isOnlyChildInBox: boolean })
      | null;
    const extractProofStepDetails = (
      proof: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null,
    ) => {
      const isOnlyChildInBox = proof ? proof.length == 1 : false;
      if (isOnlyChildInBox) {
        proofStepDetails = {
          proofStep: proof[0],
          parentBoxUuid: parentBox ? parentBox.uuid : "",
          isOnlyChildInBox: true,
          position: {
            prepend: false,
            nearProofStepWithUuid: "",
          },
        };
      } else {
        proofStepDetails = {
          proofStep: proof[indexInCurrLayer],
          parentBoxUuid: parentBox ? parentBox.uuid : "",
          isOnlyChildInBox: false,
          position: {
            prepend: indexInCurrLayer == 0,
            nearProofStepWithUuid:
              indexInCurrLayer == 0
                ? proof[1].uuid
                : proof[indexInCurrLayer - 1].uuid,
          },
        };
      }
      return proof;
    };
    interactWithProofNearUuid(proof.proof, uuid, null, extractProofStepDetails);
    return proofStepDetails;
  };

  const getNearestDeletableProofStep = (
    uuid: string,
  ): { proofStepDetails: ProofStepDetails | null; cascadeCount: number } => {
    let cascadeCount = 0;
    const findNearestDeletableProofStepCascade = (uuid: string) => {
      let proofStepDetails = getProofStepDetails(uuid);
      if (!proofStepDetails) return null;
      if (proofStepDetails.isOnlyChildInBox) {
        if (proofStepDetails.parentBoxUuid == null) return null;
        cascadeCount++;
        return findNearestDeletableProofStepCascade(
          proofStepDetails.parentBoxUuid,
        );
      }
      return proofStepDetails;
    };
    const proofStepDetails = findNearestDeletableProofStepCascade(uuid);
    return { proofStepDetails, cascadeCount };
  };

  return (
    <ProofContext.Provider
      value={{
        proof,
        loadProofFromId: setProofId,
        setProofContent: (pf) => setProofContent((_) => pf),
        setStringProof,
        isDescendant,
        isOnlyChild,
        getParentUuid,
        getNeighbour: getNeighbour,
        updateFreshVarOnBox,
        addStep,
        removeStep,
        updateLine,
        getProofStepDetails,
        getNearestDeletableProofStep,
      }}
    >
      {children}
    </ProofContext.Provider>
  );
}

// isDraft?: boolean,
// hasChanges ?: boolean
// isActiveEdit?: boolean
// Should use Command pattern and history
