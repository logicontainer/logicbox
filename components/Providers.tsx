"use client";

import { HistoryProvider } from "@/contexts/HistoryProvider";
import { ProofProvider } from "@/contexts/ProofProvider";
import { RulesetProvider } from "@/contexts/RulesetProvider";

type ProviderProps = {
  children: React.ReactNode;
};

export function Providers ({ children }: ProviderProps) {
  return (
    <RulesetProvider>
      <ProofProvider>
        <HistoryProvider>
          {children}
        </HistoryProvider>
      </ProofProvider>
    </RulesetProvider>
  );
}
