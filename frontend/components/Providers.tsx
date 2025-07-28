"use client";

import { ContextMenuProvider } from "@/contexts/ContextMenuProvider";
import { DiagnosticsProvider } from "@/contexts/DiagnosticsProvider";
import { HistoryProvider } from "@/contexts/HistoryProvider";
import { HoveringProvider } from "@/contexts/HoveringProvider";
import { InteractionStateProvider } from "@/contexts/InteractionStateProvider";
import { LinesProvider } from "@/contexts/LinesProvider";
import { ProofProvider } from "@/contexts/ProofProvider";
import { RulesetProvider } from "@/contexts/RulesetProvider";
import { ServerProvider } from "@/contexts/ServerProvider";
import { StepDragProvider } from "@/contexts/StepDragProvider";

type ProviderProps = {
  children: React.ReactNode;
};

export function Providers({ children }: ProviderProps) {
  return (
    <ProofProvider>
      <ServerProvider>
        <LinesProvider>
          <RulesetProvider>
            <DiagnosticsProvider>
              <HistoryProvider>
                <InteractionStateProvider>
                  <ContextMenuProvider>
                    <HoveringProvider>
                      <StepDragProvider>
                        {children}
                      </StepDragProvider>
                    </HoveringProvider>
                  </ContextMenuProvider>
                </InteractionStateProvider>
              </HistoryProvider>
            </DiagnosticsProvider>
          </RulesetProvider>
        </LinesProvider>
      </ServerProvider>
    </ProofProvider>
  );
}
