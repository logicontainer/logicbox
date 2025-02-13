"use client";

import React, { useState } from "react";

import { Ruleset } from "@/types/types";
import { rulesets } from "@/lib/rules";

export interface RulesetContextProps {
  rulesetName: string,
  ruleset: Ruleset
}

const DEFAULT_RULESET_NAME = "propositional-logic"

const RulesetContext = React.createContext<RulesetContextProps>({
  rulesetName: DEFAULT_RULESET_NAME,
  ruleset: findRulesetFromName(DEFAULT_RULESET_NAME)
})

export function useRuleset () {
  const context = React.useContext(RulesetContext);
  if (!context) {
    throw new Error("useRuleset must be used within a RulesetProvider");
  }
  return context;
}

export function RulesetProvider ({ children }: React.PropsWithChildren<object>) {
  const [rulesetName] = useState(DEFAULT_RULESET_NAME);
  const ruleset = findRulesetFromName(rulesetName);
  return (
    <RulesetContext.Provider value={{ rulesetName, ruleset }}>
      {children}
    </RulesetContext.Provider>
  );
}

function findRulesetFromName (rulesetName: string) {
  return rulesets.find((ruleset) => ruleset.rulesetName == rulesetName)!
}
