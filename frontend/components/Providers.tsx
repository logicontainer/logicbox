"use client";

import { ContextMenuProvider } from "@/contexts/ContextMenuProvider";
import { CurrentProofIdProvider } from "@/contexts/CurrentProofIdProvider";
import { DiagnosticsProvider } from "@/contexts/DiagnosticsProvider";
import { HistoryProvider } from "@/contexts/HistoryProvider";
import { HoveringProvider } from "@/contexts/HoveringProvider";
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
    <CurrentProofIdProvider>
      <ServerProvider>
        <RulesetProvider>
          <ProofProvider>
            <LinesProvider>
              <DiagnosticsProvider>
                <HistoryProvider>
                  <InteractionStateProvider>
                    <ContextMenuProvider>
                      <HoveringProvider>
                        {children}
                      </HoveringProvider>
                    </ContextMenuProvider>
                  </InteractionStateProvider>
                </HistoryProvider>
              </DiagnosticsProvider>
            </LinesProvider>
          </ProofProvider>
        </RulesetProvider>
      </ServerProvider>
    </CurrentProofIdProvider>
  );
}
