"use client";

import { HistoryProvider } from "@/contexts/HistoryProvider";
import { ProofProvider } from "@/contexts/ProofProvider";
import { RulesetProvider } from "@/contexts/RulesetProvider";
import { ServerProvider } from "@/contexts/ServerProvider";

type ProviderProps = {
  children: React.ReactNode;
};

export function Providers ({ children }: ProviderProps) {
  return (
    <ServerProvider>
      <RulesetProvider>
        <ProofProvider>
          <HistoryProvider>
            {children}
          </HistoryProvider>
        </ProofProvider>
      </RulesetProvider>
    </ServerProvider>
  );
}
