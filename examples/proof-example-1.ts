import { ProofStep } from "@/types/Proof";

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
          "uuid": "awfjkl123r19s",
          "formula": "p",
          "latexFormula": "p",
          "justification": {
            "rule": "and-elim1",
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
          "124124124"
        ]
      }
    }
  ] as ProofStep[]
};