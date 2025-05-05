"use client";

import { ContextMenuProvider } from "@/contexts/ContextMenuProvider";
import { DiagnosticsProvider } from "@/contexts/DiagnosticsProvider";
import { HistoryProvider } from "@/contexts/HistoryProvider";
import { InteractionStateProvider } from "@/contexts/InteractionStateProvider";
import { LinesProvider } from "@/contexts/LinesProvider";
import { ProofProvider, useProof } from "@/contexts/ProofProvider";
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
            <DiagnosticsProvider>
              <HistoryProvider>
                <InteractionStateProvider>
                  <ContextMenuProvider>{children}</ContextMenuProvider>
                </InteractionStateProvider>
              </HistoryProvider>
            </DiagnosticsProvider>
          </LinesProvider>
        </ProofProvider>
      </RulesetProvider>
    </ServerProvider>
  );
}
