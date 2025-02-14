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
        name: "premise",
        latex: {
          name: "\\text{premise}",
          premises: [],
          conclusion: "\\phi"
        },
        numPremises: 0
      },
      {
        name: "assumption",
        latex: {
          name: "\\text{ass.}",
          premises: [],
          conclusion: "\\phi"
        },
        numPremises: 0
      },
      {
        name: "copy",
        latex: {
          name: "\\text{copy}",
          premises: ["\\phi"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        name: "and_intro",
        latex: {
          name: "\\land i",
          premises: ["\\phi", "\\psi"],
          conclusion: "\\phi \\land \\psi"
        },
        numPremises: 2
      },
      {
        name: "and_elim_1",
        latex: {
          name: "\\land e_1",
          premises: ["\\phi \\land \\psi"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        name: "and_elim_2",
        latex: {
          name: "\\land e_2",
          premises: ["\\phi \\land \\psi"],
          conclusion: "\\psi"
        },
        numPremises: 1
      },
      {
        name: "or_intro_1",
        latex: {
          name: "\\lor i_1",
          premises: ["\\phi"],
          conclusion: "\\phi \\land \\psi"
        },
        numPremises: 1
      },
      {
        name: "or_intro_2",
        latex: {
          name: "\\lor i_2",
          premises: ["\\psi"],
          conclusion: "\\phi \\land \\psi"
        },
        numPremises: 1
      },
      {
        name: "or_elim",
        latex: {
          name: "\\lor e",
          premises: ["\\phi \\lor \\psi", "\\text{HERE SHOULD BE A BOX}", "\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\chi"
        },
        numPremises: 3
      },
      {
        name: "implies_intro",
        latex: {
          name: "\\rightarrow i",
          premises: ["\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\phi \\rightarrow \\psi"
        },
        numPremises: 1
      },
      {
        name: "implies_elim",
        latex: {
          name: "\\rightarrow e",
          premises: ["\\phi", "\\phi \\rightarrow \\psi"],
          conclusion: "\\psi"
        },
        numPremises: 2
      },
      {
        name: "not_intro",
        latex: {
          name: "\\lnot i",
          premises: ["\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\lnot \\phi"
        },
        numPremises: 1
      },
      {
        name: "not_elim",
        latex: {
          name: "\\lnot e",
          premises: ["\\phi", "\\lnot \\phi"],
          conclusion: "\\bot"
        },
        numPremises: 2
      },
      {
        name: "bot_elim",
        latex: {
          name: "\\bot e",
          premises: ["\\bot"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        name: "not_not_elim",
        latex: {
          name: "\\not\\not e",
          premises: ["\\lnot \\lnot \\phi"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        name: "modus_tollens",
        latex: {
          name: "\\text{MT}",
          premises: ["\\phi \\rightarrow \\psi", "\\lnot \\psi"],
          conclusion: "\\lnot \\phi"
        },
        numPremises: 2
      },
      {
        name: "not_not_intro",
        latex: {
          name: "\\not\\not i",
          premises: ["\\phi"],
          conclusion: "\\lnot \\lnot \\phi"
        },
        numPremises: 1
      },
      {
        name: "proof_by_contradiction",
        latex: {
          name: "\\text{PBC}",
          premises: ["\\text{HERE SHOULD BE A BOX}"],
          conclusion: "\\phi"
        },
        numPremises: 1
      },
      {
        name: "law_of_excluded_middle",
        latex: {
          name: "\\text{LEM}",
          premises: [],
          conclusion: "\\phi \\lor \\lnot \\phi"
        },
        numPremises: 0
      }
    ]
  }] as Ruleset[];