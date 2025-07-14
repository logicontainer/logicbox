"use client";
import { useProofStore } from "@/store/proofStore";
import GalleryItem from "./GalleryItem";
import { ProofWithMetadata } from "@/types/types";
import NewProofButton from "./NewProofButton";

export default function Gallery() {
  const proofs = useProofStore((state) => state.proofs);

  return (
    <div className="pt-8 lg:px-4 w-screen">
      <div className="flex gap-2 px-4">
        <p className="text text-2xl font-bold">My proofs</p>
        <NewProofButton />
      </div>
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 w-full p-4">
        {proofs.map((proof: ProofWithMetadata) => {
          return <GalleryItem key={proof.id} proof={proof} />;
        })}
      </div>
    </div>
  );
}
