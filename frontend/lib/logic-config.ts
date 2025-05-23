import { LogicName } from "@/types/types";
import { RulesetName, rulesets } from "./rules";

export const logicConfig: { [K in LogicName]: { rulesets: RulesetName[] }} = {
  propositionalLogic: {
    rulesets: ["propositionalLogicRules"]
  },
  predicateLogic: {
    rulesets: ["propositionalLogicRules", "predicateLogicRules"]
  }
}
