"use client";

import { ContextMenuProvider } from "@/contexts/ContextMenuProvider";
import { LaTeXProvider } from "@/contexts/LaTeXProvider";
import { HistoryProvider } from "@/contexts/HistoryProvider";
import { HoveringProvider } from "@/contexts/HoveringProvider";
import { InteractionStateProvider } from "@/contexts/InteractionStateProvider";
import { LinesProvider } from "@/contexts/LinesProvider";
import { ProofProvider } from "@/contexts/ProofProvider";
import { RulesetProvider } from "@/contexts/RulesetProvider";
import { BackendProvider } from "@/contexts/BackendProvider";
import { StepDragProvider } from "@/contexts/StepDragProvider";

type ProviderProps = {
  children: React.ReactNode;
};

export function Providers({ children }: ProviderProps) {
  return (
    <ProofProvider>
      <BackendProvider>
        <LinesProvider>
          <RulesetProvider>
            <LaTeXProvider>
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
            </LaTeXProvider>
          </RulesetProvider>
        </LinesProvider>
      </BackendProvider>
    </ProofProvider>
  );
}
