"use client";
import Card from "@/components/Card";
import { Button } from "@/components/ui/button";
import { ProofWithMetadata } from "@/types/types";
import { TrashIcon } from "lucide-react";
import Link from "next/link";
import { useProofStore } from "@/store/proofStore";
import DownloadProofButton from "@/components/DownloadProofButton";
import RenameProofButton from "@/components/RenameProofButton";
import DeleteProofButton from "@/components/DeleteProofButton";
import { Skeleton } from "@/components/ui/skeleton";

export function GalleryItem({ proof }: { proof: ProofWithMetadata }) {
  const createdAtString = () => {
    let isoString = proof.createdAt;
    if (!isoString) isoString = new Date().toISOString();
    const createdAtDate = new Date(isoString);
    return createdAtDate.toLocaleString();
  };
  return (
    <Link href={`/proofs/${proof.id}`}>
      <Card className="flex gap-2 p-2 items-center justify-between h-20 w-full cursor-pointer  hover:bg-gray-100">
        <div className="flex flex-col gap-2 overflow-hidden">
          <p className="text text-xl font-bold text-nowrap overflow-scroll">{proof.title}</p>
          <p className="text-sm text-gray-500">{createdAtString()}</p>
        </div>
        <div className="flex items-center justify-end gap-1">
          <RenameProofButton proofId={proof.id} />
          <DeleteProofButton proofId={proof.id}/>
          <DownloadProofButton proofId={proof.id} />
        </div>
      </Card>
    </Link>
  );
}

export function GalleryItemSkeleton() {
  return (
    <div className="flex gap-2 p-2 items-center justify-between h-20 w-full cursor-pointer">
      <div className="flex flex-col gap-2 overflow-hidden">
        <Skeleton className="h-5 w-72"/>
        <Skeleton className="h-3 w-36"/>
        <p className="text-sm text-gray-500"></p>
      </div>
      <div className="flex items-center justify-end gap-1">
      </div>
    </div>
  );
}
