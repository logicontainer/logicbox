"use client";

import {
  BoxProofStep,
  LineProofStep,
  Proof,
  ProofStep,
  ProofStepDetails,
  ProofStepPosition,
} from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import { useCurrentProofId } from "./CurrentProofIdProvider";
import { useProofStore } from "@/store/proofStore";
import { useServer } from "./ServerProvider";

export interface ProofContextProps {
  proof: Proof;
  setStringProof: (proof: string) => unknown;
  addLine: (proofStep: ProofStep, position: ProofStepPosition) => unknown;
  removeLine: (uuid: string) => unknown;
  updateLine: (uuid: string, updatedLineProofStep: LineProofStep) => unknown;
  getProofStepDetails: (
    uuid: string
  ) => (ProofStepDetails & { isOnlyChildInBox: boolean }) | null;
  getNearestDeletableProofStep: (uuid: string) => {
    proofStepDetails: ProofStepDetails | null;
    cascadeCount: number;
  };

  // HANDLED BY STATE MACHINE
  // removeIsActiveEditFromLine: (uuid: string) => unknown;
  // setActiveEdit: (uuid: string) => unknown;
  // isActiveEdit: (uuid: string) => boolean;
}
// Context Setup
const ProofContext = React.createContext<ProofContextProps>({
  proof: [],
  setStringProof: () => {},
  addLine: () => {},
  removeLine: () => {},
  updateLine: () => {},
  getProofStepDetails: () => null,
  getNearestDeletableProofStep: () => {
    return { proofStepDetails: null, cascadeCount: 0 };
  },
});

export function useProof() {
  const context = React.useContext(ProofContext);
  if (!context) {
    throw new Error("useProof must be used within a ProofProvider");
  }
  return context;
}

export function ProofProvider({ children }: React.PropsWithChildren<object>) {
  const serverContext = useServer();
  const [proof, setProof] = useState(serverContext.proof);
  const updateProofContent = useProofStore((state) => state.updateProofContent);
  const { proofId } = useCurrentProofId();

  useEffect(() => {
    setProof(serverContext.proof);
  }, [serverContext.proof]);

  useEffect(() => {
    if (!proofId) return;
    updateProofContent(proofId, proof);
  }, [proof]);

  const setStringProof = (stringProof: string) => {
    setProof(JSON.parse(stringProof));
  };

  const interactWithProofNearUuid = (
    proof: ProofStep[],
    uuid: string,
    parentBox: BoxProofStep | null,
    actionAtIndex: (
      layer: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null
    ) => void
  ): boolean => {
    const indexInCurrentLayer = proof.findIndex(
      (proofStep) => proofStep.uuid == uuid
    );
    if (indexInCurrentLayer != -1) {
      actionAtIndex(proof, indexInCurrentLayer, parentBox);
      return true;
    }
    const boxProofSteps: BoxProofStep[] = proof.filter(
      (proofStep) => proofStep.stepType == "box"
    ) as unknown as BoxProofStep[];
    for (const boxProofStep of boxProofSteps) {
      if (
        interactWithProofNearUuid(
          boxProofStep.proof,
          uuid,
          boxProofStep,
          actionAtIndex
        )
      )
        return true;
    }
    return false;
  };

  const addLine = (proofStep: ProofStep, position: ProofStepPosition) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const insertProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null
      ) => {
        return proof.splice(
          indexInCurrLayer + (position.prepend ? 0 : 1),
          0,
          proofStep
        );
      };
      interactWithProofNearUuid(
        newProof,
        position.nearProofStepWithUuid,
        null,
        insertProofStepAtUuid
      );
      return newProof;
    });
  };

  const removeLine = (uuid: string) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const removeProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null
      ) => {
        return proof.splice(indexInCurrLayer, 1);
      };
      interactWithProofNearUuid(newProof, uuid, null, removeProofStepAtUuid);
      return newProof;
    });
  };

  const updateLine = (uuid: string, updatedLineProofStep: ProofStep) => {
    setProof((prev) => {
      const newProof = _.cloneDeep(prev);
      const updateProofStepAtUuid = (
        proof: ProofStep[],
        indexInCurrLayer: number,
        parentBox: BoxProofStep | null
      ) => {
        proof[indexInCurrLayer] = updatedLineProofStep;
      };
      interactWithProofNearUuid(newProof, uuid, null, updateProofStepAtUuid);
      return newProof;
    });
  };

  const getProofStepDetails = (
    uuid: string
  ): (ProofStepDetails & { isOnlyChildInBox: boolean }) | null => {
    let proofStepDetails = {} as
      | (ProofStepDetails & { isOnlyChildInBox: boolean })
      | null;
    const extractProofStepDetails = (
      proof: ProofStep[],
      indexInCurrLayer: number,
      parentBox: BoxProofStep | null
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
    interactWithProofNearUuid(proof, uuid, null, extractProofStepDetails);
    return proofStepDetails;
  };

  const getNearestDeletableProofStep = (
    uuid: string
  ): { proofStepDetails: ProofStepDetails | null; cascadeCount: number } => {
    let cascadeCount = 0;
    const findNearestDeletableProofStepCascade = (uuid: string) => {
      let proofStepDetails = getProofStepDetails(uuid);
      if (!proofStepDetails) return null;
      if (proofStepDetails.isOnlyChildInBox) {
        if (proofStepDetails.parentBoxUuid == null) return null;
        cascadeCount++;
        return findNearestDeletableProofStepCascade(
          proofStepDetails.parentBoxUuid
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
        setStringProof,
        addLine,
        removeLine,
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
