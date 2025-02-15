import { Ruleset } from "@/types/types";

export function createHighlightedLatexRule (
  name: string,
  premises: string[],
  conclusion: string,
  highlightedPremises: number[] = [],
  conclusionIsHighlighted: boolean = false
): string {
  const premisesWithHighlights = premises.map((p, idx) => {
    if (highlightedPremises.includes(idx)) {
      return `\\color{red}\\boxed{${p}}`;
    } else {
      return p;
    }
  });
  const premiseLine = `\\begin{matrix}${premisesWithHighlights.join("&")}\\end{matrix}`;
  const conclusionLine = conclusionIsHighlighted ? `\\color{red}\\boxed{${conclusion}}` : conclusion;
  return `\\frac{${premiseLine}}{${conclusionLine}}\\tiny{${name}}`;
}

export const rulesets = [
  {
    rulesetName: "propositional-logic",
    rules: [
      {
        ruleName: "premise",
        latex: {
          ruleName: "\\text{premise}",
          premises: [],
          conclusion: "\\phi"
        },
        numPremises: 0
      },
      {
        ruleName: "assumption",
        latex: {
          ruleName: "\\text{ass.}",
          premises: [],
          conclusion: "\\phi"
        },
        numPremises: 0
      },
      {
        ruleName: "copy",
        latex: {
          ruleName: "\\text{copy}",
          premises: ["\\phi"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "and_intro",
        latex: {
          ruleName: "\\land i",
          premises: ["\\phi", "\\psi"],
          conclusion: "\\phi \\land \\psi"
        },
        numPremises: 2
      },
      {
        ruleName: "and_elim_1",
        latex: {
          ruleName: "\\land e_1",
          premises: ["\\phi \\land \\psi"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "and_elim_2",
        latex: {
          ruleName: "\\land e_2",
          premises: ["\\phi \\land \\psi"],
          conclusion: "\\psi"
        },
        numPremises: 1
      },
      {
        ruleName: "or_intro_1",
        latex: {
          ruleName: "\\lor i_1",
          premises: ["\\phi"],
          conclusion: "\\phi \\land \\psi"
        },
        numPremises: 1
      },
      {
        ruleName: "or_intro_2",
        latex: {
          ruleName: "\\lor i_2",
          premises: ["\\psi"],
          conclusion: "\\phi \\land \\psi"
        },
        numPremises: 1
      },
      {
        ruleName: "or_elim",
        latex: {
          ruleName: "\\lor e",
          premises: ["\\phi \\lor \\psi", "\\text{HERE SHOULD BE A BOX}", "\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\chi"
        },
        numPremises: 3
      },
      {
        ruleName: "implies_intro",
        latex: {
          ruleName: "\\rightarrow i",
          premises: ["\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\phi \\rightarrow \\psi"
        },
        numPremises: 1
      },
      {
        ruleName: "implies_elim",
        latex: {
          ruleName: "\\rightarrow e",
          premises: ["\\phi", "\\phi \\rightarrow \\psi"],
          conclusion: "\\psi"
        },
        numPremises: 2
      },
      {
        ruleName: "not_intro",
        latex: {
          ruleName: "\\lnot i",
          premises: ["\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\lnot \\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "not_elim",
        latex: {
          ruleName: "\\lnot e",
          premises: ["\\phi", "\\lnot \\phi"],
          conclusion: "\\bot"
        },
        numPremises: 2
      },
      {
        ruleName: "bot_elim",
        latex: {
          ruleName: "\\bot e",
          premises: ["\\bot"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "not_not_elim",
        latex: {
          ruleName: "\\lnot\\lnot e",
          premises: ["\\lnot \\lnot \\phi"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "modus_tollens",
        latex: {
          ruleName: "\\text{MT}",
          premises: ["\\phi \\rightarrow \\psi", "\\lnot \\psi"],
          conclusion: "\\lnot \\phi"
        },
        numPremises: 2
      },
      {
        ruleName: "not_not_intro",
        latex: {
          ruleName: "\\lnot\\lnot i",
          premises: ["\\phi"],
          conclusion: "\\lnot \\lnot \\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "proof_by_contradiction",
        latex: {
          ruleName: "\\text{PBC}",
          premises: ["\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        ruleName: "law_of_excluded_middle",
        latex: {
          ruleName: "\\text{LEM}",
          premises: [],
          conclusion: "\\phi \\lor \\lnot \\phi"
        },
        numPremises: 0
      }
    ]
  }] as Ruleset[];