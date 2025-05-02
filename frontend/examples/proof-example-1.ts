import { Proof } from "@/types/types";

const realExample: Proof = [
  {
    formula: {
      userInput: "",
    },
    justification: {
      refs: [],
      rule: "premise",
    },
    stepType: "line",
    uuid: "1",
  },
  {
    formula: {
      userInput: "q -> s",
    },
    justification: {
      refs: [],
      rule: null,
    },
    stepType: "line",
    uuid: "2",
  },
  {
    proof: [
      {
        formula: {
          userInput: "",
        },
        justification: {
          refs: [],
          rule: "assumption",
        },
        stepType: "line",
        uuid: "3",
      },
      {
        formula: {
          userInput: "q",
        },
        justification: {
          refs: ["3", "1"],
          rule: null,
        },
        stepType: "line",
        uuid: "4",
      },
      {
        formula: {
          userInput: "s",
        },
        justification: {
          refs: ["4", "3"],
          rule: "implies_elim",
        },
        stepType: "line",
        uuid: "5",
      },
    ],
    stepType: "box",
    uuid: "box",
  },
  {
    formula: {
      userInput: "s or r",
    },
    justification: {
      refs: ["5"],
      rule: "or_intro_0",
    },
    stepType: "line",
    uuid: "6",
  },
  {
    formula: {
      userInput: "p implies  s",
    },
    justification: {
      refs: ["box"],
      rule: "implies_intro",
    },
    stepType: "line",
    uuid: "7",
  },
];

const example2: Proof = [
  {
    "formula": {
      "ascii": null,
      "latex": null,
      "userInput": ""
    },
    "justification": {
      "refs": [],
      "rule": "premise"
    },
    "stepType": "line",
    "uuid": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
  },
  {
    "formula": {
      "ascii": "q -> s",
      "latex": "q \\rightarrow s",
      "userInput": "q -> s"
    },
    "justification": {
      "refs": [],
      "rule": null
    },
    "stepType": "line",
    "uuid": "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e"
  },
  {
    "proof": [
      {
        "formula": {
          "ascii": null,
          "latex": null,
          "userInput": ""
        },
        "justification": {
          "refs": [],
          "rule": "assumption"
        },
        "stepType": "line",
        "uuid": "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"
      },
      {
        "formula": {
          "ascii": "q",
          "latex": "q",
          "userInput": "q"
        },
        "justification": {
          "refs": [
            "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
            "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"
          ],
          "rule": null
        },
        "stepType": "line",
        "uuid": "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8g"
      },
      {
        "formula": {
          "ascii": "s",
          "latex": "s",
          "userInput": "s"
        },
        "justification": {
          "refs": [
            "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8g",
            "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f"
          ],
          "rule": "implies_elim"
        },
        "stepType": "line",
        "uuid": "e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8g9h"
      }
    ],
    "stepType": "box",
    "uuid": "f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8g9h0i"
  },
  {
    "formula": {
      "ascii": "s or r",
      "latex": "s \\lor r",
      "userInput": "s or r"
    },
    "justification": {
      "refs": [
        "e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8g9h"
      ],
      "rule": "or_intro_0"
    },
    "stepType": "line",
    "uuid": "7g8h9i0j-1k2l-4m3n-4o5p-6q7r8s9t0u1v"
  },
  {
    "formula": {
      "ascii": "p -> s",
      "latex": "p \\rightarrow s",
      "userInput": "p implies  s"
    },
    "justification": {
      "refs": [
        "f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8g9h0i"
      ],
      "rule": "implies_intro"
    },
    "stepType": "line",
    "uuid": "8h9i0j1k-2l3m-4n4o-5p6q-7r8s9t0u1v2w"
  }
]

export default example2;
