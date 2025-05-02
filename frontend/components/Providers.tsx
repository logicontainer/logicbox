"use client";

import { HistoryProvider, useHistory } from "@/contexts/HistoryProvider";

import { ContextMenuProvider } from "@/contexts/ContextMenuProvider";
import { InteractionStateProvider } from "@/contexts/InteractionStateProvider";
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
            <HistoryProvider>
              <InteractionStateProvider>
                <ContextMenuProvider>{children}</ContextMenuProvider>
              </InteractionStateProvider>
            </HistoryProvider>
          </LinesProvider>
        </ProofProvider>
      </RulesetProvider>
    </ServerProvider>
  );
}
