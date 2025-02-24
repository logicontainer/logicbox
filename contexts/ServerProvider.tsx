"use client";

import { Proof, ValidationResponse } from "@/types/types";
import React, { useState } from "react";

import { ServerCommand } from "../lib/server-commands";
import _ from "lodash";
import proofExample1 from "@/examples/proof-example-1";

export interface ServerContextProps {
  proof: Proof,
  syncingStatus: string,
  proofDiagnostics: ValidationResponse,
  serverCommands: ServerCommand[], // TODO: Remove. This is for temporary visualization purposes
  validateProof: (proof: Proof) => Promise<boolean>,
  addCommands: (commands: ServerCommand[]) => unknown,
}

const ServerContext = React.createContext<ServerContextProps>({
  proof: [],
  proofDiagnostics: { isValid: true, diagnostics: [] },
  syncingStatus: "idle",
  serverCommands: [], // TODO: Remove. This is for temporary visualization purposes
  validateProof: async () => false,
  addCommands: () => { },
})

export function useServer () {
  const context = React.useContext(ServerContext);
  if (!context) {
    throw new Error("useServer must be used within a ServerProvider");
  }
  return context;
}

export function ServerProvider ({ children }: React.PropsWithChildren<object>) {
  const [serverCommands, setServerCommands] = useState<ServerCommand[]>([]);
  const [syncingStatus, setServerSyncingStatus] = useState<string>("idle");
  const [syncCheckpoint, setSyncCheckpoint] = useState<number>(0);
  const [proof, setProof] = useState<Proof>(proofExample1.proof);
  const [proofDiagnostics, setProofDiagnostics] = useState<ValidationResponse>({ isValid: true, diagnostics: [] });

  const validateProof = async (proof: Proof): Promise<boolean> => {
    setServerSyncingStatus("syncing")
    try {
      // TODO: Call server here
      return Promise.resolve().then(async () => {
        console.log("Calling server")
        await new Promise(resolve => setTimeout(resolve, 1000));
        return true;
      }).then(() => {
        console.log("Received server response")
        const newProof = _.cloneDeep(proof)
        // newProof.push(newProof[0])
        const serverResponse = {
          proof: newProof,
          diagnostics: {
            isValid: false, diagnostics: [{
              uuid: "1",
              violationType: "wrong_number_of_references",
              violation: {
                explanation: "Sample violation explanation: The number of references is wrong",
                expected: 2,
                actual: 1
              }
            }]
          } as ValidationResponse,
          checkpoint: 1,
        }
        setSyncCheckpoint(serverResponse.checkpoint)
        setProof(serverResponse.proof)
        setProofDiagnostics(serverResponse.diagnostics)
        return true;
      }).finally(() => {
        setServerSyncingStatus("idle")
      })
    } catch (error) {
      setServerSyncingStatus("error")
      return false;
    }
  }

  const addCommands = (serverCommands: ServerCommand[]) => {
    setServerCommands((prev) => {
      return [...prev, ...serverCommands]
    })
  }

  return (
    <ServerContext.Provider value={{
      proof,
      syncingStatus,
      proofDiagnostics,
      serverCommands,// TODO: Remove. This is for temporary visualization purposes
      validateProof,
      addCommands
    }}>
      {children}
    </ServerContext.Provider>
  );
}

