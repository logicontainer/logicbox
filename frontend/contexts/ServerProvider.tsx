"use client";

import { Diagnostic, ValidationRequest, ValidationResponse } from "@/types/types";
import React, { useEffect, useState } from "react";

import _ from "lodash";
import { useProof } from "./ProofProvider";
import Script from "next/script";

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

export function ServerProvider({ children }: React.PropsWithChildren<object>) {
  const [syncingStatus, setServerSyncingStatus] = useState<string>("idle");
  const [proofDiagnostics, setProofDiagnostics] = useState<Diagnostic[]>([]);

  const { proof, setProofContent } = useProof()

  const validateProofAsync = React.useRef<(req: string) => Promise<string>>(null)

  useEffect(() => {
    validateProof({ proof: proof.proof, logicName: proof.logicName });
  }, [proof.id]);

  const validateProof = async (request: ValidationRequest): Promise<boolean> => {
    setServerSyncingStatus("syncing");
    return Promise.resolve()
      .then(async () => {
        if (validateProofAsync.current === null)
          return Promise.resolve("")

        console.time("1")
        const jstr = JSON.stringify(request)
        console.timeEnd("1")
        console.time("2")
        const val = await validateProofAsync.current?.(jstr)
        console.timeEnd("2")

        return validateProofAsync.current?.(JSON.stringify(request))
      })
      .then(async (serverResponse: string) => {
        return JSON.parse(serverResponse);
      })
      .then((serverResponse: ValidationResponse) => {
        console.log("Server response", serverResponse);
        setProofDiagnostics(serverResponse.diagnostics);
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
    <>
      <Script
        src="https://cjrtnc.leaningtech.com/4.2/loader.js"
        strategy="afterInteractive" // or 'afterInteractive', 'lazyOnload'
        onLoad={async () => {
          console.log('Script has loaded');
          // Initialize library here
          const _ = await (window as any).cheerpjInit()
          const lib = await (window as any).cheerpjRunLibrary("/app/logicbox_lib.jar")
          const Main = await lib.logicbox.Main
          validateProofAsync.current = async (str: string) => { return await Main.verify(str) }
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
  );
}
