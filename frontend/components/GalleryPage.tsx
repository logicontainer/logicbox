"use client";

import { useProofStore } from "@/store/proofStore";
import { GalleryItemSkeleton, GalleryItem } from "@/components/GalleryItem";
import { ProofWithMetadata } from "@/types/types";
import NewProofDialog from "@/components/NewProofButton";
import UploadProofButton from "@/components/UploadProofButton";
import React from "react";
import _ from "lodash";
import Footer from "./Footer";
import Link from "next/link";

export default function GalleryPage() {
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

  return (
    <div className="pt-8 lg:px-4 w-screen min-h-screen flex flex-col justify-between">
      <div className="flex w-full items-center justify-between gap-2 px-4">
        <Link href={"/gallery"} title="Go to your proof gallery" className="flex items-center justify-end gap-1">
          <img className="min-w-16 w-16 h-16" src="/logicbox-icon.svg"></img>
          <p className="text text-3xl font-bold">LogicBox</p>
        </Link>
        <div className="flex gap-2 items-center justify-start">
          <p className="text text-2xl font-bold">My proofs</p>
          <NewProofDialog />
          <UploadProofButton />
        </div>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3 2xl:grid-cols-4 gap-4 w-full p-4">
        {isHydrated && proofs.map((proof: ProofWithMetadata) => {
          return <GalleryItem key={proof.id} proof={proof} />;
        })}
        {!isHydrated && _.range(0, 4 * 3).map(i => { // 4 rows
          return <GalleryItemSkeleton key={i} />
        })}
      </div>
      <div className="w-full grow flex flex-col justify-end">
        <Footer />
      </div>
    </div>
  );
}
