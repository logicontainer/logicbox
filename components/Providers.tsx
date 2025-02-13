"use client";

import { HistoryProvider } from "@/contexts/HistoryProvider";
import { ProofProvider } from "@/contexts/ProofProvider";

type ProviderProps = {
  children: React.ReactNode;
};

export function Providers ({ children }: ProviderProps) {
  return (
    <ProofProvider>
      <HistoryProvider>
        {children}
      </HistoryProvider>
    </ProofProvider>
  );
}
