"use client";

import { Diagnostic, ValidationRequest, ValidationResponse } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import { FALLBACK_PROOF, useProof } from "./ProofProvider";
import Script from "next/script";

export interface ServerContextProps {
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: ValidationRequest) => void;
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
  const [proofDiagnostics, setProofDiagnostics] = useState<Diagnostic[]>([]);
  const verifyFunction = React.useRef<((req: string) => string) | null>(null)

  const { proof, setProofContent } = useProof()

  const initialValidation = () => {
    if (proof.id !== FALLBACK_PROOF.id && verifyFunction.current !== null) {
      validateProof({ proof: proof.proof, logicName: proof.logicName });
    } else {
      setTimeout(() => initialValidation(), 0)
    }
  }

  React.useEffect(() => {
    initialValidation()
  }, [proof.id])

  const validateProof = async (request: ValidationRequest) => {
    if (verifyFunction.current === null) {
      console.warn("Can't validate proof! Backend is not loaded.")
      return;
    }

    const result = verifyFunction.current(JSON.stringify(request))
    const jsonResult = JSON.parse(result)

    if (jsonResult.message !== undefined) {
      console.warn(jsonResult)
      return;
    }
    
    setProofDiagnostics(jsonResult.diagnostics);
    setProofContent(jsonResult.proof);
  };

  return <>
    <Script
      src="/logicbox_backend.js"
      strategy="afterInteractive"
      onLoad={() => {
        if (!JSLogicboxVerifier)
          console.error("Backend didn't load correctly")

        verifyFunction.current = JSLogicboxVerifier.verify
      }}
    />
    <ServerContext.Provider
      value={{
        proofDiagnostics,
        validateProof,
      }}
    >
      {children}
    </ServerContext.Provider>
  </>
}
