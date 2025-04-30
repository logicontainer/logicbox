"use client";

import { HistoryProvider } from "@/contexts/HistoryProvider";
import { LinesProvider } from "@/contexts/LinesProvider";
import { ProofProvider } from "@/contexts/ProofProvider";
import { RulesetProvider } from "@/contexts/RulesetProvider";
import { ServerProvider } from "@/contexts/ServerProvider";

type ProviderProps = {
  children: React.ReactNode;
};

export function Providers({ children }: ProviderProps) {
  return (
    <ServerProvider>
      <RulesetProvider>
        <ProofProvider>
          <LinesProvider>
            <HistoryProvider>{children}</HistoryProvider>
          </LinesProvider>
        </ProofProvider>
      </RulesetProvider>
    </ServerProvider>
  );
}
