"use client";

import React, { useState } from "react";

import { Ruleset } from "@/types/types";
import { RulesetName, rulesets } from "@/lib/rules";
import { useProof } from "./ProofProvider";
import { logicConfig } from "@/lib/logic-config";

export interface RulesetContextProps {
  rulesets: Ruleset[];
}

const RulesetContext = React.createContext<RulesetContextProps | null>(null);

export function useRuleset() {
  const context = React.useContext(RulesetContext);
  if (!context) {
    throw new Error("useRuleset must be used within a RulesetProvider");
  }
  return context;
}

export function RulesetProvider({ children }: React.PropsWithChildren<object>) {
  const { proof } = useProof()
  const rulesets: Ruleset[] = logicConfig[proof.logicName].rulesets.map(findRulesetFromName)

  return (
    <RulesetContext.Provider
      value={{ rulesets }}
    >
      {children}
    </RulesetContext.Provider>
  );
}

function findRulesetFromName(rulesetName: RulesetName) {
  return rulesets.find((ruleset) => ruleset.rulesetName == rulesetName)!;
}
