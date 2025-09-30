"use client";

import { useProofStore } from "@/store/proofStore";
import { GalleryItemSkeleton, GalleryItem } from "@/components/GalleryItem";
import { ProofWithMetadata } from "@/types/types";
import NewProofDialog from "@/components/NewProofButton";
import UploadProofsButton from "@/components/UploadProofsButton";
import React from "react";
import _ from "lodash";
import Footer from "./Footer";
import Link from "next/link";

import { isMobile } from 'react-device-detect'
import { HelpDialogButton } from "./HelpDialogButton";

export default function GalleryPage() {
  const [isHydrated, setIsHydrated] = React.useState<boolean>(false)
  const proofs = useProofStore((state) => state.proofs);

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
    <div className="pt-2 sm:pt-4 w-screen min-h-screen flex flex-col justify-between">
      <div className="flex w-full items-center justify-between gap-1 sm:gap-2 px-2 sm:px-4">
        <Link href={"/gallery"} title="Go to your proof gallery" className="flex items-center justify-end gap-1">
          <img src={"logicbox-icon.svg"} width={isMobile ? 40 : 48} height={isMobile ? 40 : 48} alt="LogicBox logo" />
          <p className="text text-xl sm:text-3xl font-bold">LogicBox</p>
        </Link>
        <div className="flex gap-1 sm:gap-2 items-center justify-start">
          <p className="hidden sm:block text text-xl sm:text-xl font-bold">My proofs</p>
          <NewProofDialog />
          <UploadProofsButton />
          <HelpDialogButton />
        </div>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3 2xl:grid-cols-4 gap-4 w-full p-2 sm:p-4">
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
