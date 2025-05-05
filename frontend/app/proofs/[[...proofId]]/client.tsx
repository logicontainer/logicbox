"use client";

import {
  InteractionStateEnum,
  Transition,
  TransitionEnum,
  useInteractionState,
} from "@/contexts/InteractionStateProvider";

import { InlineMath } from "react-katex";
import { LineNumbers } from "@/components/LineNumbers";
import { Proof } from "@/components/Proof";
import { ProofStepContextMenu } from "@/components/ProofStepContextMenu";
import React from "react";
import Toolbar from "@/components/Toolbar";
import { Tooltip } from "react-tooltip";
import { useCurrentProofId } from "@/contexts/CurrentProofIdProvider";
import { useEffect } from "react";
import { useLines } from "@/contexts/LinesProvider";
import { useProof } from "@/contexts/ProofProvider";

export default function Client({ proofId }: { proofId: string | null }) {
  const proofContext = useProof();
  const { interactionState, doTransition } = useInteractionState();
  const { lines } = useLines();

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
        (e.key === "r" || e.key === "Enter") &&
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
    <div className="relative">
      <ProofStepContextMenu />
      <div
        className="flex justify-center h-screen"
        onClick={() => doTransition({ enum: TransitionEnum.CLICK_OUTSIDE })}
      >
        <main className="flex flex-col  row-start-2 items-center sm:items-start">
          <div className="p-4 flex flex-col justify-between items-center rounded-sm">
            <div className="fixed z-50" onClick={(e) => e.stopPropagation()}>
              <Toolbar />
            </div>
            <div
              className="flex box-content gap-2 mt-20"
              onClick={(e) => e.stopPropagation()}
            >
              <LineNumbers lines={lines} />
              <Proof proof={proofContext.proof} lines={lines} />
              <Tooltip
                id={`tooltip-id-${proofContext.lineInFocus}`}
                place="right"
                render={
                  ({ content }) =>
                    content ? (
                      <p className="text-md">
                        <InlineMath math={content}></InlineMath>
                      </p>
                    ) : null // don't show if content is null
                }
              ></Tooltip>
              {/*<ProofStepContextMenu />*/}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
