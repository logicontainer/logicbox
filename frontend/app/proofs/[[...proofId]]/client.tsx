"use client";

import {
  InteractionStateEnum,
  Transition,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import Footer from "@/components/Footer";
import { LineNumbers } from "@/components/LineNumbers";
import { Proof } from "@/components/Proof";
import { ProofStepContextMenu } from "@/components/ProofStepContextMenu";
import React from "react";
import Toolbar from "@/components/Toolbar";
import { useCurrentProofId } from "@/contexts/CurrentProofIdProvider";
import { useEffect } from "react";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";
import { useServer } from "@/contexts/ServerProvider";
import { useHovering } from "@/contexts/HoveringProvider";

export default function Client({ proofId }: { proofId: string | null }) {
  const proofContext = useProof();
  const { proofDiagnostics } = useServer();
  const { interactionState, doTransition } = useInteractionState();
  const { lines } = useLines();
  const { onHoverStep: setCurrentlyHoveredUuid } = useHovering()

  const { setProofId } = useCurrentProofId();

  useEffect(() => {
    console.log("proofId", proofId);
    setProofId(proofId || ""); // Set the proofId in the context
  }, [proofId, setProofId]);

  const [keybindTransition, setKeybindTransition] =
    React.useState<Transition | null>();

  React.useEffect(() => {
    const listener = (e: KeyboardEvent) => {
      if (
        (e.key === "r") &&
        interactionState.enum === InteractionStateEnum.IDLE
      )
        setKeybindTransition({ enum: TransitionEnum.VALIDATE_PROOF });
    };
    window.addEventListener("keydown", listener);
    return () => window.removeEventListener("keydown", listener);
  }, [interactionState]);

  React.useEffect(() => {
    if (keybindTransition) doTransition(keybindTransition);
  }, [keybindTransition]);

  return (
    <>
      <ProofStepContextMenu />
      <div
        className="flex flex-col items-center w-full  max-h-screen overflow-auto justify-between sm:h-screen sm:gap-2"
        onClick={() => doTransition({ enum: TransitionEnum.CLICK_OUTSIDE })}
      >
        <div
          className="absolute mx-auto top-4 z-50"
          onClick={(e) => e.stopPropagation()}
        >
          <Toolbar />
        </div>
        <div className="grid grid-cols-[1fr_auto_1fr] w-full">
          <div></div>
          <div className="p-4 flex flex-col justify-between items-center rounded-sm">
            <div
              className="flex box-content gap-2 mt-[64px] w-full"
              onClick={(e) => e.stopPropagation()}
              onMouseLeave={e => {
                e.stopPropagation()
                doTransition({ enum: TransitionEnum.HOVER, stepUuid: null })
                setCurrentlyHoveredUuid(null)
              }}
            >
              <LineNumbers lines={lines} />
              <Proof
                proof={proofContext.proof}
                lines={lines}
                diagnostics={proofDiagnostics}
                isOuterProof
              />
            </div>
          </div>
        </div>
        <div className="w-full hidden sm:block">
          <Footer />
        </div>
      </div>
    </>
  );
}
