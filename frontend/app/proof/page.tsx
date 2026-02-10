"use client"

import ProofEditorPage from "@/components/ProofEditorPage";
import { InteractionStateEnum, TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { useSearchParams } from "next/navigation";
import React from "react";
import { Suspense } from "react";

function PageImpl() {
  const params = useSearchParams()
  const proofId = params.get("id")

  if (proofId === null)
    return "No id provided"

  return <ProofEditorPage proofId={proofId} />
}

export default function Page() {
  const { doTransition } = useInteractionState()
  React.useEffect(() => {
    const listener = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && !e.shiftKey && e.key === "z") {
        e.preventDefault()
        doTransition({ enum: TransitionEnum.UNDO })
      } else if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === "z") {
        e.preventDefault()
        doTransition({ enum: TransitionEnum.REDO })
      } else if (e.key === "Escape") {
        doTransition({ enum: TransitionEnum.CLOSE })
      }
    }

    document.addEventListener('keydown', listener, { capture: true })

    return () => document.removeEventListener('keydown', listener, { capture: true })
  }, [doTransition])

  return <Suspense> {/* needed so next doesn't just fucking ignore "use client" */}
    <PageImpl />
  </Suspense>
}
