"use client";

import dynamic from "next/dynamic"

import { useProofStore } from "@/store/proofStore";
import { ProofWithMetadata } from "@/types/types";
import { useSearchParams } from "next/navigation";
import { useRouter } from "next/navigation";
import React from "react";
import { v4 as uuidv4 } from "uuid";

import jsonTemplates from "@/public/templates.json"
const templates = jsonTemplates as ProofWithMetadata[]

function Page() {
  const params = useSearchParams()
  const proofs = useProofStore(state => state.proofs)
  const store = useProofStore()
  const addProof = useProofStore((state) => state.addProof);
  const router = useRouter()

  const templateId = params.get("id")

  React.useEffect(() => {
    if (templateId === null) {
      alert("no template id given")
      return;
    }

    const proofAlreadyThere = proofs.find(proof => proof.id.startsWith(templateId))
    if (proofAlreadyThere) {
      alert("You already have a proof based on this template. You will be redirected to it.")
      router.push(`/proof?id=${proofAlreadyThere.id}`)
    } else {
      const template = templates.find(t => t.id === templateId)
      if (template) {
        template.id = template.id + uuidv4()
        template.createdAt = new Date().toISOString()
        addProof(template)
        router.push(`/proof/?id=${template.id}`);
      } else {
        alert(`No template with id ${templateId}`)
      }
    }
  }, [])

  return `Loading template ${templateId}`
}

export default dynamic(() => Promise.resolve(Page), {
  ssr: false
})
