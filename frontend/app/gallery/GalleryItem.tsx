"use client";
import Card from "@/components/Card";
import { Button } from "@/components/ui/button";
import { ProofWithMetadata } from "@/types/types";
import { TrashIcon } from "lucide-react";
import Link from "next/link";
import { useProofStore } from "@/store/proofStore";

export default function GalleryItem({ proof }: { proof: ProofWithMetadata }) {
  const deleteProof = useProofStore((state) => state.deleteProof);
  const createdAtString = () => {
    let isoString = proof.createdAt;
    if (!isoString) isoString = new Date().toISOString();
    const createdAtDate = new Date(isoString)
    return createdAtDate.toLocaleString();
  }
  return <Link href={`/proofs/${proof.id}`}>
    <Card className="flex gap-2 p-2 items-center justify-between  w-full cursor-pointer  hover:bg-gray-100">
      <div className="flex flex-col gap-2">
        <p className="text text-xl font-bold">{proof.title}</p>
        <p className="text-sm text-gray-500">{createdAtString()}</p>
      </div>
      <Button variant="outline" className="hover:text-red-500"
        onMouseOver={(e) => e.stopPropagation()}
        onClick={(e) => {
          e.preventDefault();
          if (window.confirm(`Are you sure you want to delete the proof: ${proof.title}`)) {
            deleteProof(proof.id);
          }
        }}
      >
        <TrashIcon />
      </Button>
    </Card>
  </Link>;
}

