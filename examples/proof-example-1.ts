import { Proof, ProofStep } from "@/types/types";

export const linesAndBox = {
  "uuid": "12oshjfl2",
  "proof": [
    {
      "stepType": "line",
      "uuid": "a",
      "formula": "p -> q",
      "latexFormula": "p \\rightarrow q",
      "justification": {
        "ruleName": "premise",
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
            "ruleName": "assumption",
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
                "ruleName": "assumption",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "f",
              "formula": "p to r",
              "latexFormula": "p \\rightarrow q",
              "justification": {
                "ruleName": "and_elim_2",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "g",
              "formula": "p",
              "latexFormula": "p",
              "justification": {
                "ruleName": "and_elim_1",
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
            "ruleName": "and_elim_2",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "i",
          "formula": "p",
          "latexFormula": "p",
          "justification": {
            "ruleName": "and_elim_1",
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
        "ruleName": "implies_elim",
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
        "ruleName": "premise",
        "refs": []
      }
    },
    {
      "stepType": "line",
      "uuid": "asdfkhjkl13",
      "formula": "p and r -> q and s",
      "latexFormula": "p \\land r \\rightarrow q \\land s",
      "justification": {
        "ruleName": "implies-intro",
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
        "ruleName": "premise",
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
            "ruleName": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "dfsafdfsf",
          "formula": "p and r",
          "latexFormula": "p \\land q",
          "justification": {
            "ruleName": "assumption",
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
                "ruleName": "assumption",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "dfsafdfsfsdfsdf",
              "formula": "p to r",
              "latexFormula": "p \\rightarrow q",
              "justification": {
                "ruleName": "and_elim_2",
                "refs": []
              }
            },
            {
              "stepType": "line",
              "uuid": "awfjkl123r19sdfsdfs",
              "formula": "p",
              "latexFormula": "p",
              "justification": {
                "ruleName": "and_elim_1",
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
            "ruleName": "and_elim_1",
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
            "ruleName": "assumption",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "dfsafdfsfsdfsdf",
          "formula": "p to r",
          "latexFormula": "p \\rightarrow q",
          "justification": {
            "ruleName": "and_elim_2",
            "refs": []
          }
        },
        {
          "stepType": "line",
          "uuid": "awfjkl123r19sdfsdfs",
          "formula": "p",
          "latexFormula": "p",
          "justification": {
            "ruleName": "and_elim_1",
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
        "ruleName": "implies_elim",
        "refs": [
          "124124124",
          "awfjkl123r19s",
        ]
      }
    }
  ] as ProofStep[]
};


const realExample = [{
  "formula": "((p -> q) -> r)",
  "justification": {
    "ruleName": "premise",
    "refs": []
  },
  "latexFormula": "p \\rightarrow q \\rightarrow r",
  "stepType": "line",
  "uuid": "1"
}, {
  "formula": "(s -> ¬p)",
  "justification": {
    "ruleName": "premise",
    "refs": []
  },
  "latexFormula": "s \\rightarrow \\lnot p",
  "stepType": "line",
  "uuid": "2"
}, {
  "formula": "t",
  "justification": {
    "ruleName": "premise",
    "refs": []
  },
  "latexFormula": "t",
  "stepType": "line",
  "uuid": "3"
}, {
  "formula": "((¬s ∧ t) -> q)",
  "justification": {
    "ruleName": "premise",
    "refs": []
  },
  "latexFormula": "\\lnot s \\land t \\rightarrow q",
  "stepType": "line",
  "uuid": "4"
}, {
  "formula": "(p ∨ ¬p)",
  "justification": {
    "ruleName": "law_of_excluded_middle",
    "refs": []
  },
  "latexFormula": "p \\lor \\lnot p",
  "stepType": "line",
  "uuid": "5"
}, {
  "proof": [{
    "formula": "p",
    "justification": {
      "ruleName": "assumption",
      "refs": []
    },
    "latexFormula": "p",
    "stepType": "line",
    "uuid": "11"
  }, {
    "formula": "q",
    "justification": {
      "ruleName": "copy",
      "refs": ["10"]
    },
    "latexFormula": "q",
    "stepType": "line",
    "uuid": "12"
  }],
  "stepType": "box",
  "uuid": "b1"
}, {
  "proof": [{
    "formula": "¬p",
    "justification": {
      "ruleName": "assumption",
      "refs": []
    },
    "latexFormula": "\\lnot p",
    "stepType": "line",
    "uuid": "14"
  }, {
    "proof": [{
      "formula": "p",
      "justification": {
        "ruleName": "assumption",
        "refs": []
      },
      "latexFormula": "p",
      "stepType": "line",
      "uuid": "15"
    }, {
      "formula": "⊥",
      "justification": {
        "ruleName": "not_elim",
        "refs": ["15", "14"]
      },
      "latexFormula": "\\bot",
      "stepType": "line",
      "uuid": "16"
    }, {
      "formula": "q",
      "justification": {
        "ruleName": "contradition_elim",
        "refs": ["16"]
      },
      "latexFormula": "q",
      "stepType": "line",
      "uuid": "17"
    }],
    "stepType": "box",
    "uuid": "b3"
  }, {
    "formula": "(p -> q)",
    "justification": {
      "ruleName": "implies_intro",
      "refs": ["b3"]
    },
    "latexFormula": "p \\rightarrow q",
    "stepType": "line",
    "uuid": "18"
  }],
  "stepType": "box",
  "uuid": "b4"
}, {
  "formula": "(p -> q)",
  "justification": {
    "ruleName": "or_elim",
    "refs": ["5", "b2", "b4"]
  },
  "latexFormula": "p \\rightarrow q",
  "stepType": "line",
  "uuid": "19"
}, {
  "formula": "r",
  "justification": {
    "ruleName": "implies_elim",
    "refs": ["19", "1"]
  },
  "latexFormula": "r",
  "stepType": "line",
  "uuid": "20"
}] as Proof;


export default realExample