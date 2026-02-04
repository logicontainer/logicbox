"use client";

import { useProofStore } from "@/store/proofStore";
import { ProofWithMetadata } from "@/types/types";
import { useSearchParams } from "next/navigation";
import { useRouter } from "next/navigation";
import React from "react";
import { v4 as uuidv4 } from "uuid";

import jsonTemplates from "@/public/templates.json"
const templates: ProofWithMetadata[] = jsonTemplates as ProofWithMetadata[]

export default function Page() {
  const params = useSearchParams()
  const proofs = useProofStore(state => state.proofs)
  const addProof = useProofStore((state) => state.addProof);
  const router = useRouter()
  const templateId = params.get("id")
  if (templateId === null) {
    alert("no template id given")
    return;
  }

  const proofAlreadyThere = proofs.find(proof => proof.id.startsWith(templateId))
  if (proofAlreadyThere) {
    React.useEffect(() => {
      router.push(`/proof?id=${proofAlreadyThere.id}`)
    })
    return `Redirecting to ${proofAlreadyThere.id}`;
  }
  
  const template = templates.find(t => t.id === templateId)
  if (template === undefined) {
    alert(`unknown template "${templateId}"`)
    return;
  }

  React.useEffect(() => {
    template.id = template.id + uuidv4()
    template.createdAt = new Date().toISOString()
    addProof(template)
    router.push(`/proof/?id=${template.id}`);
  })

  return `Creating new proof with id ${template.id}`;
}
