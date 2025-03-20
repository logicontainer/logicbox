"use client";

import { Diagnostic, Proof, ValidationResponse } from "@/types/types";
import React, { useState } from "react";

import _ from "lodash";
import proofExample1 from "@/examples/proof-example-1";

export interface ServerContextProps {
  proof: Proof;
  syncingStatus: string;
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: Proof) => Promise<boolean>;
}

const ServerContext = React.createContext<ServerContextProps>({
  proof: [],
  proofDiagnostics: [],
  syncingStatus: "idle",
  validateProof: async () => false,
});

export function useServer() {
  const context = React.useContext(ServerContext);
  if (!context) {
    throw new Error("useServer must be used within a ServerProvider");
  }
  return context;
}

export function ServerProvider({ children }: React.PropsWithChildren<object>) {
  const [syncingStatus, setServerSyncingStatus] = useState<string>("idle");
  const [proof, setProof] = useState<Proof>(proofExample1);
  const [proofDiagnostics, setProofDiagnostics] = useState<Diagnostic[]>([]);

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
