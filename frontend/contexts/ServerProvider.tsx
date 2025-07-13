"use client";

import {
  Diagnostic,
  ValidationRequest,
  ValidationResponse,
  Violation,
} from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import { useProof } from "./ProofProvider";

export interface ServerContextProps {
  syncingStatus: string;
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: ValidationRequest) => Promise<boolean>;
}

const ServerContext = React.createContext<ServerContextProps | null>(null);

export function useServer() {
  const context = React.useContext(ServerContext);
  if (!context) {
    throw new Error("useServer must be used within a ServerProvider");
  }
  return context;
}

function fixDiagnostic(d: Diagnostic) {
  return {
    ...d,
    violation: { ...d.violation, violationType: d.violationType } as Violation,
  };
}

export function ServerProvider({ children }: React.PropsWithChildren<object>) {
  const [syncingStatus, setServerSyncingStatus] = useState<string>("idle");
  const [proofDiagnostics, setProofDiagnostics] = useState<Diagnostic[]>([]);

  const { proof, setProofContent } = useProof();

  useEffect(() => {
    validateProof({ proof: proof.proof, logicName: proof.logicName });
  }, [proof.id]);

  const validateProof = async (
    request: ValidationRequest,
  ): Promise<boolean> => {
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
          body: JSON.stringify(request),
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
        setProofDiagnostics(serverResponse.diagnostics.map(fixDiagnostic));
        setProofContent(serverResponse.proof);
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
        syncingStatus,
        proofDiagnostics,
        validateProof,
      }}
    >
      {children}
    </ServerContext.Provider>
  );
}
