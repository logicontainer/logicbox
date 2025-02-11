import { ProofStep } from "@/types/types";

export default {
  "uuid": "12oshjfl2",
  "proof": [
    {
      "stepType": "line",
      "uuid": "15211a2",
      "formula": "p -> q",
      "latexFormula": "p \\rightarrow q",
      "justification": {
        "rule": "premise",
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
            "rule": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "dfsafdfsf",
          "formula": "p and r",
          "latexFormula": "p \\land q",
          "justification": {
            "rule": "assumption",
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
                "rule": "assumption",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "dfsafdfsfsdfsdf",
              "formula": "p to r",
              "latexFormula": "p \\rightarrow q",
              "justification": {
                "rule": "and_elim_2",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "awfjkl123r19sdfsdfs",
              "formula": "p",
              "latexFormula": "p",
              "justification": {
                "rule": "and_elim_1",
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
            "rule": "and_elim_1",
            "refs": [
              "124a7189247"
            ]
          }
        }
      ]
    },
    {
      "stepType": "line",
      "uuid": "asdfkhjkl13",
      "formula": "p and r -> q and s",
      "latexFormula": "p \\land r \\rightarrow q \\land s",
      "justification": {
        "rule": "implies-intro",
        "refs": [
          "124124124",
          "awfjkl123r19s",
          "124124124sdfs"
        ]
      }
    }
  ] as ProofStep[]
};