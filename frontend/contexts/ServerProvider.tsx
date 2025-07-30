"use client";

import { Diagnostic, ValidationRequest, ValidationResponse } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import { useProof } from "./ProofProvider";
import Script from "next/script";
import { verify } from "crypto";

export interface ServerContextProps {
  syncingStatus: string;
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: ValidationRequest) => Promise<boolean>;
}

declare const JSLogicboxVerifier: {
  verify: (req: string) => string
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
  const [proofDiagnostics, setProofDiagnostics] = useState<Diagnostic[]>([]);
  const verifyFunction = React.useRef<((req: string) => string) | null>(null)


  const { proof, setProofContent } = useProof()

  useEffect(() => {
    validateProof({ proof: proof.proof, logicName: proof.logicName });
  }, [proof.id]);

  const validateProof = async (request: ValidationRequest): Promise<boolean> => {
    setServerSyncingStatus("syncing");

    return Promise.resolve((() => {
      if (verifyFunction.current === null)
        return false

      const result = verifyFunction.current(JSON.stringify(request))
      const jsonResult = JSON.parse(result)
      if (jsonResult.message !== undefined) {
        console.error(jsonResult)
        return false
      }
      
      setProofDiagnostics(jsonResult.diagnostics);
      setProofContent(jsonResult.proof);

      setServerSyncingStatus("idle")

      return true
    })())
  };

  return <>
    <Script
      src="/logicbox_backend.js"
      strategy="afterInteractive"
      onLoad={() => {
        if (!JSLogicboxVerifier)
          console.error("Backend didn't load correctly")

        console.log("Loaded backend")

        verifyFunction.current = JSLogicboxVerifier.verify
      }}
    />
    <ServerContext.Provider
      value={{
        syncingStatus,
        proofDiagnostics,
        validateProof,
      }}
    >
      {children}
    </ServerContext.Provider>
  </>
}
