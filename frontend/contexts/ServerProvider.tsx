"use client";

import { Diagnostic, Proof, ValidationResponse } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import examples from "@/examples/proof-examples";
import { useCurrentProofId } from "./CurrentProofIdProvider";

export interface ServerContextProps {
  proof: Proof;
  syncingStatus: string;
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: Proof) => Promise<boolean>;
}

const ServerContext = React.createContext<ServerContextProps | null>(null);

const getProofById = (id: string | null): Proof => {
  if (id) {
    const example = examples.find((example) => example.id === id);
    if (example) {
      return example.proof;
    }
  }
  const randomIndex = Math.floor(Math.random() * examples.length);
  return examples[randomIndex].proof;
};

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

  const prevProof = React.useRef<Proof>(proof);
  const prevProofDiagnostics = React.useRef<Diagnostic[]>(proofDiagnostics);

  React.useEffect(() => {
    // This will only run on the client side
    if (examples.length === 0) {
      console.warn("No examples provided, using empty proof");
      setProof({} as Proof);
      return;
    }
    const randomIndex = Math.floor(Math.random() * examples.length);

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
    console.log(proof);
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
