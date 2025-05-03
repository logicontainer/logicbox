"use client";

import React, { useState } from "react";

import { Ruleset } from "@/types/types";
import { rulesets } from "@/lib/rules";

export interface RulesetContextProps {
  rulesetName: string;
  rulesetDropdownOptions: { value: string; label: string }[];
  ruleset: Ruleset;
}

const DEFAULT_RULESET_NAME = "propositional-logic";

const RulesetContext = React.createContext<RulesetContextProps>({
  rulesetName: DEFAULT_RULESET_NAME,
  rulesetDropdownOptions: [],
  ruleset: findRulesetFromName(DEFAULT_RULESET_NAME),
});

export function useRuleset() {
  const context = React.useContext(RulesetContext);
  if (!context) {
    throw new Error("useRuleset must be used within a RulesetProvider");
  }
  return context;
}

export function RulesetProvider({ children }: React.PropsWithChildren<object>) {
  const [rulesetName] = useState(DEFAULT_RULESET_NAME);
  const ruleset = findRulesetFromName(rulesetName);

  const rulesetDropdownOptions = ruleset.rules.map((rule) => ({
    value: rule.ruleName,
    label: rule.ruleName,
    latexRuleName: rule.latex.ruleName,
  }));

  return (
    <RulesetContext.Provider
      value={{ rulesetName, rulesetDropdownOptions, ruleset }}
    >
      {children}
    </RulesetContext.Provider>
  );
}

function findRulesetFromName(rulesetName: string) {
  return rulesets.find((ruleset) => ruleset.rulesetName == rulesetName)!;
}
