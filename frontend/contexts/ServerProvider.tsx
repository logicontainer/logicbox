"use client";

import { Diagnostic, Proof, ValidationResponse } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
// import examples from "@/examples/proof-examples";
import { useCurrentProofId } from "./CurrentProofIdProvider";
import { useProofStore } from "@/store/proofStore";

export interface ServerContextProps {
  proof: Proof;
  syncingStatus: string;
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: Proof) => Promise<boolean>;
}

const ServerContext = React.createContext<ServerContextProps | null>(null);

export function useServer() {
  const context = React.useContext(ServerContext);
  if (!context) {
    throw new Error("useServer must be used within a ServerProvider");
  }
  return context;
}

export function ServerProvider({ children }: React.PropsWithChildren<object>) {
  const [syncingStatus, setServerSyncingStatus] = useState<string>("idle");
  const [proof, setProof] = useState<Proof>([]);
  const [proofDiagnostics, setProofDiagnostics] = useState<Diagnostic[]>([]);
  const { proofId } = useCurrentProofId();
  const proofs = useProofStore((state) => state.proofs);
  const addProof = useProofStore((state) => state.addProof);
  const deleteProof = useProofStore((state) => state.deleteProof);
  const getProof = useProofStore((state) => state.getProof);
  const clearAll = useProofStore((state) => state.clearAll);
  const prevProof = React.useRef<Proof>(proof);
  const prevProofDiagnostics = React.useRef<Diagnostic[]>(proofDiagnostics);

  const getProofById = (id: string | null): Proof => {
    if (id) {
      const proof = getProof(id);
      if (proof) {
        return proof.proof;
      }
    }
    const randomIndex = Math.floor(Math.random() * proofs.length);
    return proofs[randomIndex].proof;
  };

  React.useEffect(() => {
    // This will only run on the client side
    if (proofs.length === 0) {
      console.warn("No existing proofs provided, using empty proof");
      setProof({} as Proof);
      return;
    }
    const randomIndex = Math.floor(Math.random() * proofs.length);

    if (proofId === null) {
      return;
    }
    if (proofId) {
      setProof(getProofById(proofId));
    } else {
      console.warn("No proofId provided, using random example");
      setProof(getProofById(null));
    }
  }, [proofId]);

  useEffect(() => {
    if (proofId == null) setProof([]);
  }, []);

  useEffect(() => {
    if (_.isEmpty(proof)) {
      return;
    }
    if (
      _.isEqual(proof, prevProof.current) &&
      _.isEqual(proofDiagnostics, prevProofDiagnostics.current)
    ) {
      return;
    }
    validateProof(proof);
  }, [proof]);

  const validateProof = async (proof: Proof): Promise<boolean> => {
    setServerSyncingStatus("syncing");
    console.trace(proof);
    return Promise.resolve()
      .then(async () => {
        console.log("Calling server");
        return fetch("https://logicbox.felixberg.dev/verify", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(proof),
        });
      })
      .then(async (serverResponse: Response) => {
        if (!serverResponse.ok) {
          throw new Error(`Server error: ${serverResponse.statusText}`);
        }
        return await serverResponse.json();
      })
      .then((serverResponse: ValidationResponse) => {
        prevProofDiagnostics.current = serverResponse.diagnostics;
        prevProof.current = proof;
        console.log("Server response", serverResponse);
        setProofDiagnostics(serverResponse.diagnostics);
        setProof(serverResponse.proof);
        return true;
      })
      .finally(() => {
        setServerSyncingStatus("idle");
      })
      .catch((error) => {
        console.error("Error", error);
        setServerSyncingStatus("error");
        return false;
      });
  };

  return (
    <ServerContext.Provider
      value={{
        proof,
        syncingStatus,
        proofDiagnostics,
        validateProof,
      }}
    >
      {children}
    </ServerContext.Provider>
  );
}
