"use client";

import { useProofStore } from '@/store/proofStore';
import { ProofWithMetadataSchema } from '@/types/types';
import { decompressFromEncodedURIComponent } from 'lz-string';
import dynamic from 'next/dynamic';
import { useRouter, useSearchParams } from 'next/navigation';
import React from 'react';
import { v4 as uuidv4 } from 'uuid';

function Page() {
  const params = useSearchParams()
  const addProof = useProofStore(state => state.addProof)
  const deleteProof = useProofStore(state => state.deleteProof)
  const proofs = useProofStore(state => state.proofs)
  const data = params.get("data")
  const router = useRouter()

  React.useEffect(() => {
    if (!data) return;

    const identifier = data.substring(0, 16)
    let shouldOverwrite = false

    const proofAlreadyThere = proofs.find(proof => proof.id.startsWith(identifier))
    if (proofAlreadyThere) {
      shouldOverwrite = confirm("You already have a proof created from this link. Do you wish to overwrite it?\n\n(If not, you will be redirected to the existing proof)");
      if (!shouldOverwrite) {
        router.push(`/proof?id=${proofAlreadyThere.id}`)
        return;
      }
    }

    const output = decompressFromEncodedURIComponent(data)
    const obj = JSON.parse(output)
    console.log(obj)
    if (!obj) {
      throw new Error("not a valid JSON object")
    }

    const parseResult = ProofWithMetadataSchema.safeParse(obj)
    if (parseResult.success) {
      const proof = parseResult.data
      proof.id = shouldOverwrite ? (proofAlreadyThere!.id) : identifier + uuidv4()
      if (shouldOverwrite) {
        deleteProof(proof.id)
      }

      addProof(proof)
      router.push(`/proof?id=${proof.id}`)
    } else {
      throw new Error("not a valid Logicbox proof")
    }
  }, [])

  return ""
}

export default dynamic(() => Promise.resolve(Page), {
  ssr: false
})
