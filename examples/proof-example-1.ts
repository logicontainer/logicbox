import { ProofStep } from "@/types/types";

export const linesAndBox = {
  "uuid": "12oshjfl2",
  "proof": [
    {
      "stepType": "line",
      "uuid": "a",
      "formula": "p -> q",
      "latexFormula": "p \\rightarrow q",
      "justification": {
        "name": "premise",
        "refs": []
      }
    },
    {
      "stepType": "box",
      "uuid": "b",
      "proof": [
        {
          "stepType": "line",
          "uuid": "c",
          "formula": "p and r",
          "latexFormula": "p \\land q",
          "justification": {
            "name": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "box",
          "uuid": "d",
          "proof": [
            {
              "stepType": "line",
              "uuid": "e",
              "formula": "p and r",
              "latexFormula": "p \\land q",
              "justification": {
                "name": "assumption",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "f",
              "formula": "p to r",
              "latexFormula": "p \\rightarrow q",
              "justification": {
                "name": "and_elim_2",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "g",
              "formula": "p",
              "latexFormula": "p",
              "justification": {
                "name": "and_elim_1",
                "refs": [
                  "c"
                ]
              }
            }
          ]
        },
        {
          "stepType": "line",
          "uuid": "h",
          "formula": "p to r",
          "latexFormula": "p \\rightarrow q",
          "justification": {
            "name": "and_elim_2",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "i",
          "formula": "p",
          "latexFormula": "p",
          "justification": {
            "name": "and_elim_1",
            "refs": [
              "d"
            ]
          }
        }
      ]
    },
    {
      "stepType": "line",
      "uuid": "dsfdsf",
      "formula": "p",
      "latexFormula": "p",
      "justification": {
        "name": "implies_elim",
        "refs": [
          "h",
          "i",
        ]
      }
    }
  ] as ProofStep[]
};

export const linesOnly = {
  "uuid": "12oshjfl2",
  "proof": [
    {
      "stepType": "line",
      "uuid": "15211a2",
      "formula": "p -> q",
      "latexFormula": "p \\rightarrow q",
      "justification": {
        "name": "premise",
        "refs": []
      }
    },
    {
      "stepType": "line",
      "uuid": "asdfkhjkl13",
      "formula": "p and r -> q and s",
      "latexFormula": "p \\land r \\rightarrow q \\land s",
      "justification": {
        "name": "implies-intro",
        "refs": [
          "124124124",
          "awfjkl123r19s",
          "124124124sdfs"
        ]
      }
    }
  ] as ProofStep[]
};
export const biggerExample = {
  "uuid": "12oshjfl2",
  "proof": [
    {
      "stepType": "line",
      "uuid": "15211a2",
      "formula": "p -> q",
      "latexFormula": "p \\rightarrow q",
      "justification": {
        "name": "premise",
        "refs": []
      }
    },
    {
      "stepType": "box",
      "uuid": "124124124",
      "proof": [
        {
          "stepType": "line",
          "uuid": "124a7189247",
          "formula": "p and r",
          "latexFormula": "p \\land q",
          "justification": {
            "name": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "dfsafdfsf",
          "formula": "p and r",
          "latexFormula": "p \\land q",
          "justification": {
            "name": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "box",
          "uuid": "124124124sdfs",
          "proof": [
            {
              "stepType": "line",
              "uuid": "124a718924sdfsdf7",
              "formula": "p and r",
              "latexFormula": "p \\land q",
              "justification": {
                "name": "assumption",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "dfsafdfsfsdfsdf",
              "formula": "p to r",
              "latexFormula": "p \\rightarrow q",
              "justification": {
                "name": "and_elim_2",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "awfjkl123r19sdfsdfs",
              "formula": "p",
              "latexFormula": "p",
              "justification": {
                "name": "and_elim_1",
                "refs": [
                  "124a718924sdfsdf7"
                ]
              }
            }
          ]
        },
        {
          "stepType": "line",
          "uuid": "awfjkl123r19s",
          "formula": "p",
          "latexFormula": "p",
          "justification": {
            "name": "and_elim_1",
            "refs": [
              "124a7189247"
            ]
          }
        }
      ]
    },
    {
      "stepType": "box",
      "uuid": "124124124sdfs",
      "proof": [
        {
          "stepType": "line",
          "uuid": "124a718924sdfsdf7",
          "formula": "p and r",
          "latexFormula": "p \\land q",
          "justification": {
            "name": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "dfsafdfsfsdfsdf",
          "formula": "p to r",
          "latexFormula": "p \\rightarrow q",
          "justification": {
            "name": "and_elim_2",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "awfjkl123r19sdfsdfs",
          "formula": "p",
          "latexFormula": "p",
          "justification": {
            "name": "and_elim_1",
            "refs": [
              "124a718924sdfsdf7"
            ]
          }
        }
      ]
    },
    {
      "stepType": "line",
      "uuid": "dsfdsf",
      "formula": "p",
      "latexFormula": "p",
      "justification": {
        "name": "implies_elim",
        "refs": [
          "124124124",
          "awfjkl123r19s",
        ]
      }
    }
  ] as ProofStep[]
};


export default linesAndBox