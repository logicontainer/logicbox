"use client";

import { Diagnostic, ValidationRequest } from "@/types/types";
import React, { useState } from "react";

import _ from "lodash";
import { FALLBACK_PROOF, useProof } from "./ProofProvider";
import Script from "next/script";

export interface BackendContextProps {
  proofDiagnostics: Diagnostic[];
  validateProof: (proof: ValidationRequest) => void;
}

declare const JSLogicboxVerifier: {
  verify: (req: string) => string
}

const BackendContext = React.createContext<BackendContextProps | null>(null);

export function useBackend() {
  const context = React.useContext(BackendContext);
  if (!context) {
    throw new Error("useBackend must be used within a BackendProvider");
  }
  return context;
}

export function BackendProvider({ children }: React.PropsWithChildren<object>) {
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
    <BackendContext.Provider
      value={{
        proofDiagnostics,
        validateProof,
      }}
    >
      {children}
    </BackendContext.Provider>
  </>
}
