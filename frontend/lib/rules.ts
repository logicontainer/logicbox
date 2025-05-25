import { Ruleset } from "@/types/types";

export function createHighlightedLatexRule(
  name: string,
  premises: string[],
  conclusion: string,
  highlightedPremises: number[] = [],
  conclusionIsHighlighted: boolean = false,
): string {
  const premisesWithHighlights = premises.map((p, idx) => {
    const content = highlightedPremises.includes(idx)
      ? `{\\color{red}\\underline{${p}}}`
      : p;
    return content
  });
  const premiseLine = `{\\begin{array}{${premises.map(_ => "c").join("")}}${premisesWithHighlights.join("&") || "\\quad\\quad"}\\end{array}}`;
  const conclusionLine = conclusionIsHighlighted
    ? `\\color{red}\\underline{${conclusion}}`
    : conclusion;
  return `\\cfrac{${premiseLine}}{${conclusionLine}}\\small{${name}}`;
}

const box = (str: string, columns: number = 1) => `\\begin{array}{|${"c".repeat(columns)}|} \\hline ${str} \\\\ \\hline \\end{array}`

export const rulesets = [
  {
    rulesetName: "propositionalLogicRules",
    rules: [
      {
        ruleName: "premise",
        latex: {
          ruleName: "\\text{premise}",
          premises: [],
          conclusion: "\\varphi",
        },
        numPremises: 0,
      },
      {
        ruleName: "assumption",
        latex: {
          ruleName: "\\text{ass.}",
          premises: [],
          conclusion: "\\varphi",
        },
        numPremises: 0,
      },
      {
        ruleName: "copy",
        latex: {
          ruleName: "\\text{copy}",
          premises: ["\\varphi"],
          conclusion: "\\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "and_intro",
        latex: {
          ruleName: "\\land i",
          premises: ["\\varphi", "\\psi"],
          conclusion: "\\varphi \\land \\psi",
        },
        numPremises: 2,
      },
      {
        ruleName: "and_elim_1",
        latex: {
          ruleName: "\\land e_1",
          premises: ["\\varphi \\land \\psi"],
          conclusion: "\\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "and_elim_2",
        latex: {
          ruleName: "\\land e_2",
          premises: ["\\varphi \\land \\psi"],
          conclusion: "\\psi",
        },
        numPremises: 1,
      },
      {
        ruleName: "or_intro_1",
        latex: {
          ruleName: "\\lor i_1",
          premises: ["\\varphi"],
          conclusion: "\\varphi \\lor \\psi",
        },
        numPremises: 1,
      },
      {
        ruleName: "or_intro_2",
        latex: {
          ruleName: "\\lor i_2",
          premises: ["\\psi"],
          conclusion: "\\varphi \\lor \\psi",
        },
        numPremises: 1,
      },
      {
        ruleName: "or_elim",
        latex: {
          ruleName: "\\lor e",
          premises: ["\\varphi \\lor \\psi", box("\\varphi\\\\ \\vdots \\\\ \\chi"), box("\\psi\\\\ \\vdots \\\\ \\chi")],
          conclusion: "\\chi",
        },
        numPremises: 3,
      },
      {
        ruleName: "implies_intro",
        latex: {
          ruleName: "\\rightarrow i",
          premises: [box("\\varphi \\\\ \\vdots \\\\ \\psi")],
          conclusion: "\\varphi \\rightarrow \\psi",
        },
        numPremises: 1,
      },
      {
        ruleName: "implies_elim",
        latex: {
          ruleName: "\\rightarrow e",
          premises: ["\\varphi", "\\varphi \\rightarrow \\psi"],
          conclusion: "\\psi",
        },
        numPremises: 2,
      },
      {
        ruleName: "not_intro",
        latex: {
          ruleName: "\\lnot i",
          premises: [box("\\varphi \\\\ \\vdots \\\\ \\bot")],
          conclusion: "\\lnot \\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "not_elim",
        latex: {
          ruleName: "\\lnot e",
          premises: ["\\varphi", "\\lnot \\varphi"],
          conclusion: "\\bot",
        },
        numPremises: 2,
      },
      {
        ruleName: "bot_elim",
        latex: {
          ruleName: "\\bot e",
          premises: ["\\bot"],
          conclusion: "\\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "not_not_elim",
        latex: {
          ruleName: "\\lnot\\lnot e",
          premises: ["\\lnot \\lnot \\varphi"],
          conclusion: "\\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "modus_tollens",
        latex: {
          ruleName: "\\text{MT}",
          premises: ["\\varphi \\rightarrow \\psi", "\\lnot \\psi"],
          conclusion: "\\lnot \\varphi",
        },
        numPremises: 2,
      },
      {
        ruleName: "not_not_intro",
        latex: {
          ruleName: "\\lnot\\lnot i",
          premises: ["\\varphi"],
          conclusion: "\\lnot \\lnot \\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "proof_by_contradiction",
        latex: {
          ruleName: "\\text{PBC}",
          premises: [box("\\lnot \\varphi\\\\ \\vdots \\\\ \\bot")],
          conclusion: "\\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "law_of_excluded_middle",
        latex: {
          ruleName: "\\text{LEM}",
          premises: [],
          conclusion: "\\varphi \\lor \\lnot \\varphi",
        },
        numPremises: 0,
      },
    ],
  },
  {
    rulesetName: "predicateLogicRules",
    rules: [
      {
        ruleName: "forall_intro",
        latex: {
          ruleName: "\\forall i.",
          premises: [
            box("x_0 & \\\\ & \\vdots \\\\ & \\varphi[x_0/x]", 2)
          ],
          conclusion: "\\forall x \\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "forall_elim",
        latex: {
          ruleName: "\\forall e.",
          premises: ["\\forall x \\varphi"],
          conclusion: "\\varphi[t/x]",
        },
        numPremises: 1,
      },
      {
        ruleName: "exists_intro",
        latex: {
          ruleName: "\\exists i.",
          premises: ["\\varphi[x_0/x]"],
          conclusion: "\\exists x \\varphi",
        },
        numPremises: 1,
      },
      {
        ruleName: "exists_elim",
        latex: {
          ruleName: "\\exists e.",
          premises: ["\\exists x \\varphi", box("x_0 & \\varphi[x_0/x] \\\\ & \\vdots \\\\ & \\chi", 2)],
          conclusion: "\\chi",
        },
        numPremises: 2,
      },
      {
        ruleName: "equality_intro",
        latex: {
          ruleName: "= i.",
          premises: [],
          conclusion: "t = t",
        },
        numPremises: 0,
      },
      {
        ruleName: "equality_elim",
        latex: {
          ruleName: "= e.",
          premises: ["t_1 = t_2", "\\varphi[t_1/x]"],
          conclusion: "\\varphi[t_2/x]",
        },
        numPremises: 2,
      },
    ],
  },
  {
    rulesetName: "arithmeticRules",
    rules: [
      {
        ruleName: "peano_1",
        latex: {
          ruleName: "P_1",

          premises: [],
          conclusion: "t + 0 = t",
        },
        numPremises: 0,
      },
      {
        ruleName: "peano_2",
        latex: {
          ruleName: "P_2",

          premises: [],
          conclusion: "t_1 + (t_2 + 1) = (t_1 + t_2) + 1",
        },
        numPremises: 0,
      },
      {
        ruleName: "peano_3",
        latex: {
          ruleName: "P_3",

          premises: [],
          conclusion: "t * 0 = 0",
        },
        numPremises: 0,
      },
      {
        ruleName: "peano_4",
        latex: {
          ruleName: "P_4",

          premises: [],
          conclusion: "t_1 * (t_2 + 1) = (t_1 * t_2) + t_1",
        },
        numPremises: 0,
      },
      {
        ruleName: "peano_5",
        latex: {
          ruleName: "P_5",

          premises: [],
          conclusion: "not (0 = t + 1)",
        },
        numPremises: 0,
      },
      {
        ruleName: "peano_6",
        latex: {
          ruleName: "P_6",

          premises: ["t_1 + 1 = t_2 + 1"],
          conclusion: "t_1 = t_2",
        },
        numPremises: 1,
      },
      {
        ruleName: "induction",
        latex: {
          ruleName: "\\text{ind.}",
          premises: [
            "\\varphi[0/x]",
            box("n & \\varphi[n/x] \\\\ & \\vdots \\\\ & \\varphi[n + 1/x]", 2)
          ],
          conclusion: "\\forall x \\varphi",
        },
        numPremises: 2,
      },
    ]
  }
] as const satisfies Ruleset[]

export type RulesetName = (typeof rulesets)[number]["rulesetName"]
