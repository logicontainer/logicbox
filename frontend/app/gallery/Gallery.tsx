"use client";

import { useProofStore } from "@/store/proofStore";
import { GalleryItemSkeleton, GalleryItem } from "./GalleryItem";
import { ProofWithMetadata } from "@/types/types";
import NewProofDialog from "./NewProofButton";
import UploadProofButton from "@/components/UploadProofButton";
import React from "react";
import _ from "lodash";

export default function Gallery() {
  const proofs = useProofStore((state) => state.proofs);

  const [isHydrated, setIsHydrated] = React.useState<boolean>(false)

  React.useEffect(() => {
    // if we are already hydrated
    if (useProofStore.persist.hasHydrated()) {
      setIsHydrated(true);
      return;
    }

    // subscribe to hydration event
    const unsub = useProofStore.persist.onFinishHydration(() => {
      setIsHydrated(true);
    });
    return unsub;
  }, []);

  // Or check immediately if already hydrated
  React.useEffect(() => {
  }, []);

  return (
    <div className="pt-8 lg:px-4 w-screen">
      <div className="flex gap-2 px-4">
        <p className="text text-2xl font-bold">My proofs</p>
        <NewProofDialog />
        <UploadProofButton />
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-4 w-full p-4">
        {isHydrated && proofs.map((proof: ProofWithMetadata) => {
          return <GalleryItem key={proof.id} proof={proof} />;
        })}
        {!isHydrated && _.range(0, 4 * 3).map(i => { // 4 rows
          return <GalleryItemSkeleton key={i}/>
        })}
      </div>
    </div>
  );
}
