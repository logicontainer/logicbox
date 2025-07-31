"use client"

import ProofEditorPage from "@/components/ProofEditorPage";
import { useSearchParams } from "next/navigation";
import { Suspense } from "react";

function PageImpl() {
  const params = useSearchParams()
  const proofId = params.get("id")

  if (proofId === null)
    return "bruh"

  return <ProofEditorPage proofId={proofId}/>
}

export default function Page() {
  return <Suspense> {/* needed so next doesn't just fucking ignore "use client" */}
    <PageImpl/>
  </Suspense>
}
