import { Proof } from "@/types/types";

const examples: Proof[] = [
  [
    {
      formula: {
        ascii: null,
        latex: null,
        userInput: "",
      },
      justification: {
        refs: [],
        rule: "premise",
      },
      stepType: "line",
      uuid: "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
    },
    {
      formula: {
        ascii: "q -> s",
        latex: "q \\rightarrow s",
        userInput: "q -> s",
      },
      justification: {
        refs: [],
        rule: null,
      },
      stepType: "line",
      uuid: "b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e",
    },
    {
      proof: [
        {
          formula: {
            ascii: null,
            latex: null,
            userInput: "",
          },
          justification: {
            refs: [],
            rule: "assumption",
          },
          stepType: "line",
          uuid: "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
        },
        {
          formula: {
            ascii: "q",
            latex: "q",
            userInput: "q",
          },
          justification: {
            refs: [
              "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
              "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
            ],
            rule: null,
          },
          stepType: "line",
          uuid: "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8g",
        },
        {
          formula: {
            ascii: "s",
            latex: "s",
            userInput: "s",
          },
          justification: {
            refs: [
              "d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8g",
              "c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f",
            ],
            rule: "implies_elim",
          },
          stepType: "line",
          uuid: "e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8g9h",
        },
      ],
      stepType: "box",
      uuid: "f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8g9h0i",
    },
    {
      formula: {
        ascii: "s or r",
        latex: "s \\lor r",
        userInput: "s or r",
      },
      justification: {
        refs: ["e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8g9h"],
        rule: "or_intro_0",
      },
      stepType: "line",
      uuid: "7g8h9i0j-1k2l-4m3n-4o5p-6q7r8s9t0u1v",
    },
    {
      formula: {
        ascii: "p -> s",
        latex: "p \\rightarrow s",
        userInput: "p implies  s",
      },
      justification: {
        refs: ["f6a7b8c9-d0e1-4f2a-3b4c-5d6e7f8g9h0i"],
        rule: "implies_intro",
      },
      stepType: "line",
      uuid: "8h9i0j1k-2l3m-4n4o-5p6q-7r8s9t0u1v2w",
    },
  ], [
    {
      "formula": {
        "ascii": "not (not p or not q)",
        "latex": "\\lnot (\\lnot p \\lor \\lnot q)",
        "userInput": "not (not p or not q)"
      },
      "justification": {
        "refs": [],
        "rule": "premise"
      },
      "stepType": "line",
      "uuid": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
    },
    {
      "proof": [
        {
          "formula": {
            "ascii": "not p",
            "latex": "\\lnot p",
            "userInput": "not p"
          },
          "justification": {
            "refs": [],
            "rule": "assumption"
          },
          "stepType": "line",
          "uuid": "be90fd87-e01d-4cb3-aa6a-f01950f5ca58"
        },
        {
          "formula": {
            "ascii": "not p or not q",
            "latex": "\\lnot p \\lor \\lnot q",
            "userInput": "not p or not q"
          },
          "justification": {
            "refs": [
              "be90fd87-e01d-4cb3-aa6a-f01950f5ca58"
            ],
            "rule": "or_intro_1"
          },
          "stepType": "line",
          "uuid": "ae94b166-7a4c-4594-a4d3-ff0c8315b941"
        },
        {
          "formula": {
            "ascii": "false",
            "latex": "\\bot",
            "userInput": "false"
          },
          "justification": {
            "refs": [
              "ae94b166-7a4c-4594-a4d3-ff0c8315b941",
              "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
            ],
            "rule": "not_elim"
          },
          "stepType": "line",
          "uuid": "b06aa72f-2679-4431-9702-c17fb3b11ef6"
        }
      ],
      "stepType": "box",
      "uuid": "27d558d0-26ca-42b0-ab45-f6e6b2a317dc"
    },
    {
      "formula": {
        "ascii": "p",
        "latex": "p",
        "userInput": "p"
      },
      "justification": {
        "refs": [
          "27d558d0-26ca-42b0-ab45-f6e6b2a317dc"
        ],
        "rule": "proof_by_contradiction"
      },
      "stepType": "line",
      "uuid": "6a808abb-720e-47ee-8dfc-cd2b84eea8ee"
    },
    {
      "proof": [
        {
          "formula": {
            "ascii": "not q",
            "latex": "\\lnot q",
            "userInput": "not q"
          },
          "justification": {
            "refs": [],
            "rule": "assumption"
          },
          "stepType": "line",
          "uuid": "5bff22c5-2c0b-45fd-856e-d6663ed55af3"
        },
        {
          "formula": {
            "ascii": "not p or not q",
            "latex": "\\lnot p \\lor \\lnot q",
            "userInput": "not p or not q"
          },
          "justification": {
            "refs": [
              "5bff22c5-2c0b-45fd-856e-d6663ed55af3"
            ],
            "rule": "or_intro_2"
          },
          "stepType": "line",
          "uuid": "944b9baa-74a1-485f-89eb-507f4c424aa9"
        },
        {
          "formula": {
            "ascii": "false",
            "latex": "\\bot",
            "userInput": "false"
          },
          "justification": {
            "refs": [
              "944b9baa-74a1-485f-89eb-507f4c424aa9",
              "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
            ],
            "rule": "not_elim"
          },
          "stepType": "line",
          "uuid": "1ecb02c9-93b0-4fd8-a56d-396f3f4b371f"
        }
      ],
      "stepType": "box",
      "uuid": "2712afba-e726-48a4-919a-cc41085fe690"
    },
    {
      "formula": {
        "ascii": "q",
        "latex": "q",
        "userInput": "q"
      },
      "justification": {
        "refs": [
          "2712afba-e726-48a4-919a-cc41085fe690"
        ],
        "rule": "proof_by_contradiction"
      },
      "stepType": "line",
      "uuid": "fa6243a3-70a5-444e-a002-e176267022b1"
    },
    {
      "formula": {
        "ascii": "p and q",
        "latex": "p \\land q",
        "userInput": "p and q"
      },
      "justification": {
        "refs": [
          "6a808abb-720e-47ee-8dfc-cd2b84eea8ee",
          "fa6243a3-70a5-444e-a002-e176267022b1"
        ],
        "rule": "and_intro"
      },
      "stepType": "line",
      "uuid": "6bbb407a-36c9-4aae-ae22-40bfd459008b"
    }
  ], [
      {
        "formula": {
          "ascii": "q or not q",
          "latex": "q \\lor \\lnot q",
          "userInput": "q or not q"
        },
        "justification": {
          "refs": [],
          "rule": "law_of_excluded_middle"
        },
        "stepType": "line",
        "uuid": "fdff2248-8f2c-47d9-82bf-8df1389e65b8"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "q",
              "latex": "q",
              "userInput": "q"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "5f751399-1d2a-4d38-aa3f-77a7e8b3cf54"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "p",
                  "latex": "p",
                  "userInput": "p"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "0e92a863-60cd-4b5f-8138-41ed7f897353"
              },
              {
                "formula": {
                  "ascii": "q",
                  "latex": "q",
                  "userInput": "q"
                },
                "justification": {
                  "refs": [
                    "5f751399-1d2a-4d38-aa3f-77a7e8b3cf54"
                  ],
                  "rule": "copy"
                },
                "stepType": "line",
                "uuid": "e179f6c7-7c6c-40bd-9674-dea19a249716"
              }
            ],
            "stepType": "box",
            "uuid": "5ce3a68e-06c1-45dc-b960-9b19660fac0d"
          },
          {
            "formula": {
              "ascii": "p -> q",
              "latex": "p \\rightarrow q",
              "userInput": "p -> q"
            },
            "justification": {
              "refs": [
                "5ce3a68e-06c1-45dc-b960-9b19660fac0d"
              ],
              "rule": "implies_intro"
            },
            "stepType": "line",
            "uuid": "6029a38b-1bd8-411d-805e-756f1753f818"
          },
          {
            "formula": {
              "ascii": "(p -> q) or (q -> r)",
              "latex": "(p \\rightarrow q) \\lor (q \\rightarrow r)",
              "userInput": "(p -> q) or (q -> r)"
            },
            "justification": {
              "refs": [
                "6029a38b-1bd8-411d-805e-756f1753f818"
              ],
              "rule": "or_intro_1"
            },
            "stepType": "line",
            "uuid": "268fedd0-36e4-401b-be0f-f6f7bd8cc4e2"
          }
        ],
        "stepType": "box",
        "uuid": "6c132815-cf2a-4a8f-b3b0-034bd0c6e09b"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "not q",
              "latex": "\\lnot q",
              "userInput": "not q"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "c0c1e561-708a-4457-a6b3-98730e8ab3dc"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "q",
                  "latex": "q",
                  "userInput": "q"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "e5414ac3-cc24-409d-ab25-af857e428e02"
              },
              {
                "formula": {
                  "ascii": "false",
                  "latex": "\\bot",
                  "userInput": "bot"
                },
                "justification": {
                  "refs": [
                    "e5414ac3-cc24-409d-ab25-af857e428e02",
                    "c0c1e561-708a-4457-a6b3-98730e8ab3dc"
                  ],
                  "rule": "not_elim"
                },
                "stepType": "line",
                "uuid": "780313ea-161b-4097-a276-6a4021e9e594"
              },
              {
                "formula": {
                  "ascii": "r",
                  "latex": "r",
                  "userInput": "r"
                },
                "justification": {
                  "refs": [
                    "780313ea-161b-4097-a276-6a4021e9e594"
                  ],
                  "rule": "bot_elim"
                },
                "stepType": "line",
                "uuid": "7e416170-eac3-46c0-8b11-937da2c782e4"
              }
            ],
            "stepType": "box",
            "uuid": "de93fbf7-fa0b-4af9-8214-1fc552a4f753"
          },
          {
            "formula": {
              "ascii": "q -> r",
              "latex": "q \\rightarrow r",
              "userInput": "q -> r"
            },
            "justification": {
              "refs": [
                "de93fbf7-fa0b-4af9-8214-1fc552a4f753"
              ],
              "rule": "implies_intro"
            },
            "stepType": "line",
            "uuid": "06c38110-1b7b-42e3-a37b-f6a7cf716b1f"
          },
          {
            "formula": {
              "ascii": "(p -> q) or (q -> r)",
              "latex": "(p \\rightarrow q) \\lor (q \\rightarrow r)",
              "userInput": "(p -> q) or (q -> r)"
            },
            "justification": {
              "refs": [
                "06c38110-1b7b-42e3-a37b-f6a7cf716b1f"
              ],
              "rule": "or_intro_2"
            },
            "stepType": "line",
            "uuid": "c6cc534f-4eb1-439a-a67f-0663ca3fc9e3"
          }
        ],
        "stepType": "box",
        "uuid": "00c5489a-2ce2-4e89-a325-cf1990dc3f48"
      },
      {
        "formula": {
          "ascii": "(p -> q) or (q -> r)",
          "latex": "(p \\rightarrow q) \\lor (q \\rightarrow r)",
          "userInput": "(p -> q) or (q -> r)"
        },
        "justification": {
          "refs": [
            "fdff2248-8f2c-47d9-82bf-8df1389e65b8",
            "6c132815-cf2a-4a8f-b3b0-034bd0c6e09b",
            "00c5489a-2ce2-4e89-a325-cf1990dc3f48"
          ],
          "rule": "or_elim"
        },
        "stepType": "line",
        "uuid": "92fa8c74-77cd-4afa-877c-5481e23927e0"
      }
  ], [
    {
      "formula": {
        "ascii": "p and q",
        "latex": "p \\land q",
        "userInput": "p and q"
      },
      "justification": {
        "refs": [],
        "rule": "premise"
      },
      "stepType": "line",
      "uuid": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
    },
    {
      "proof": [
        {
          "formula": {
            "ascii": "not p or not q",
            "latex": "\\lnot p \\lor \\lnot q",
            "userInput": "not p or not q"
          },
          "justification": {
            "refs": [],
            "rule": "assumption"
          },
          "stepType": "line",
          "uuid": "b37f344a-0b66-4d24-9963-b50414f4060b"
        },
        {
          "proof": [
            {
              "formula": {
                "ascii": "not p",
                "latex": "\\lnot p",
                "userInput": "not p"
              },
              "justification": {
                "refs": [],
                "rule": "assumption"
              },
              "stepType": "line",
              "uuid": "797913eb-e885-4304-a93b-94557166323a"
            },
            {
              "formula": {
                "ascii": "p",
                "latex": "p",
                "userInput": "p"
              },
              "justification": {
                "refs": [
                  "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
                ],
                "rule": "and_elim_1"
              },
              "stepType": "line",
              "uuid": "9cc2100e-6d8e-45f3-869c-6b420025ec90"
            },
            {
              "formula": {
                "ascii": "false",
                "latex": "\\bot",
                "userInput": "false"
              },
              "justification": {
                "refs": [
                  "9cc2100e-6d8e-45f3-869c-6b420025ec90",
                  "797913eb-e885-4304-a93b-94557166323a"
                ],
                "rule": "not_elim"
              },
              "stepType": "line",
              "uuid": "61e2c78e-7cc7-401e-9607-83a7c6c8f337"
            }
          ],
          "stepType": "box",
          "uuid": "d62046aa-1f0f-41e9-90fe-6efd82d43257"
        },
        {
          "proof": [
            {
              "formula": {
                "ascii": "not q",
                "latex": "\\lnot q",
                "userInput": "not q"
              },
              "justification": {
                "refs": [],
                "rule": "assumption"
              },
              "stepType": "line",
              "uuid": "b8af1eae-e3db-4e18-83ad-8344dec4f39c"
            },
            {
              "formula": {
                "ascii": "q",
                "latex": "q",
                "userInput": "q"
              },
              "justification": {
                "refs": [
                  "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
                ],
                "rule": "and_elim_2"
              },
              "stepType": "line",
              "uuid": "c8c94b02-3ec3-4776-83f7-d1815297c18c"
            },
            {
              "formula": {
                "ascii": "false",
                "latex": "\\bot",
                "userInput": "false"
              },
              "justification": {
                "refs": [
                  "c8c94b02-3ec3-4776-83f7-d1815297c18c",
                  "b8af1eae-e3db-4e18-83ad-8344dec4f39c"
                ],
                "rule": "not_elim"
              },
              "stepType": "line",
              "uuid": "898cdcca-de40-4a31-bb47-2bdd5c94a7ca"
            }
          ],
          "stepType": "box",
          "uuid": "4dccce80-d117-483b-be7e-44e5679818bb"
        },
        {
          "formula": {
            "ascii": "false",
            "latex": "\\bot",
            "userInput": "false"
          },
          "justification": {
            "refs": [
              "b37f344a-0b66-4d24-9963-b50414f4060b",
              "d62046aa-1f0f-41e9-90fe-6efd82d43257",
              "4dccce80-d117-483b-be7e-44e5679818bb"
            ],
            "rule": "or_elim"
          },
          "stepType": "line",
          "uuid": "11293057-70ce-4940-a1c6-d0d3bbe721ef"
        }
      ],
      "stepType": "box",
      "uuid": "5603913b-ffe8-423d-b981-3b086dbc05f4"
    },
    {
      "formula": {
        "ascii": "not (not p or not q)",
        "latex": "\\lnot (\\lnot p \\lor \\lnot q)",
        "userInput": "not (not p or not q)"
      },
      "justification": {
        "refs": [
          "5603913b-ffe8-423d-b981-3b086dbc05f4"
        ],
        "rule": "not_intro"
      },
      "stepType": "line",
      "uuid": "8a0e268a-abd4-4729-9dca-9891ac97dfb5"
    }
  ], [
    {
      "formula": {
        "ascii": "p -> q",
        "latex": "p \\rightarrow q",
        "userInput": "p -> q"
      },
      "justification": {
        "refs": [],
        "rule": "premise"
      },
      "stepType": "line",
      "uuid": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
    },
    {
      "proof": [
        {
          "formula": {
            "ascii": "not (not p or q)",
            "latex": "\\lnot (\\lnot p \\lor q)",
            "userInput": "not (not p or q)"
          },
          "justification": {
            "refs": [],
            "rule": "assumption"
          },
          "stepType": "line",
          "uuid": "f7f833ec-ee6f-4219-91c9-6f5957bd0c2b"
        },
        {
          "proof": [
            {
              "formula": {
                "ascii": "not p",
                "latex": "\\lnot p",
                "userInput": "not p"
              },
              "justification": {
                "refs": [],
                "rule": "assumption"
              },
              "stepType": "line",
              "uuid": "74b876a0-006b-482f-94c4-90619f9bf79a"
            },
            {
              "formula": {
                "ascii": "not p or q",
                "latex": "\\lnot p \\lor q",
                "userInput": "not p or q"
              },
              "justification": {
                "refs": [
                  "74b876a0-006b-482f-94c4-90619f9bf79a"
                ],
                "rule": "or_intro_1"
              },
              "stepType": "line",
              "uuid": "5801fc90-2e62-441a-b02f-7073ff21581b"
            },
            {
              "formula": {
                "ascii": "false",
                "latex": "\\bot",
                "userInput": "false"
              },
              "justification": {
                "refs": [
                  "5801fc90-2e62-441a-b02f-7073ff21581b",
                  "f7f833ec-ee6f-4219-91c9-6f5957bd0c2b"
                ],
                "rule": "not_elim"
              },
              "stepType": "line",
              "uuid": "cbc8b30d-1b03-4882-aba8-e06c08546cc2"
            }
          ],
          "stepType": "box",
          "uuid": "f096cb90-0c6c-4c51-aaf6-25efe74762f6"
        },
        {
          "formula": {
            "ascii": "p",
            "latex": "p",
            "userInput": "p"
          },
          "justification": {
            "refs": [
              "f096cb90-0c6c-4c51-aaf6-25efe74762f6"
            ],
            "rule": "proof_by_contradiction"
          },
          "stepType": "line",
          "uuid": "ad5cd950-032e-4238-bfc0-3ebd9b0c6f3f"
        },
        {
          "formula": {
            "ascii": "q",
            "latex": "q",
            "userInput": "q"
          },
          "justification": {
            "refs": [
              "ad5cd950-032e-4238-bfc0-3ebd9b0c6f3f",
              "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
            ],
            "rule": "implies_elim"
          },
          "stepType": "line",
          "uuid": "8435c1e4-4c12-4696-8701-1cb481a5846d"
        },
        {
          "proof": [
            {
              "formula": {
                "ascii": "q",
                "latex": "q",
                "userInput": "q"
              },
              "justification": {
                "refs": [],
                "rule": "assumption"
              },
              "stepType": "line",
              "uuid": "7b648e57-e9b1-43d0-a744-a58e2472af14"
            },
            {
              "formula": {
                "ascii": "not p or q",
                "latex": "\\lnot p \\lor q",
                "userInput": "not p or q"
              },
              "justification": {
                "refs": [
                  "7b648e57-e9b1-43d0-a744-a58e2472af14"
                ],
                "rule": "or_intro_2"
              },
              "stepType": "line",
              "uuid": "bfbd7820-2c5f-4a7f-b9b8-7302c827a423"
            },
            {
              "formula": {
                "ascii": "false",
                "latex": "\\bot",
                "userInput": "false"
              },
              "justification": {
                "refs": [
                  "bfbd7820-2c5f-4a7f-b9b8-7302c827a423",
                  "f7f833ec-ee6f-4219-91c9-6f5957bd0c2b"
                ],
                "rule": "not_elim"
              },
              "stepType": "line",
              "uuid": "032fd0c2-11f1-4eed-b5e8-57bbcfa37ffe"
            }
          ],
          "stepType": "box",
          "uuid": "28c23021-8b01-412d-b4d2-49bb5225a9d0"
        },
        {
          "formula": {
            "ascii": "not q",
            "latex": "\\lnot q",
            "userInput": "not q"
          },
          "justification": {
            "refs": [
              "28c23021-8b01-412d-b4d2-49bb5225a9d0"
            ],
            "rule": "not_intro"
          },
          "stepType": "line",
          "uuid": "80ed2487-f0e9-4be5-b26b-bbdd218b1c47"
        },
        {
          "formula": {
            "ascii": "false",
            "latex": "\\bot",
            "userInput": "false"
          },
          "justification": {
            "refs": [
              "8435c1e4-4c12-4696-8701-1cb481a5846d",
              "80ed2487-f0e9-4be5-b26b-bbdd218b1c47"
            ],
            "rule": "not_elim"
          },
          "stepType": "line",
          "uuid": "8c95f13f-9453-4d32-8d62-b7e16dcab1a2"
        }
      ],
      "stepType": "box",
      "uuid": "540d0506-ee84-413b-9712-a159704af2b6"
    },
    {
      "formula": {
        "ascii": "not p or q",
        "latex": "\\lnot p \\lor q",
        "userInput": "not p or q"
      },
      "justification": {
        "refs": [
          "540d0506-ee84-413b-9712-a159704af2b6"
        ],
        "rule": "proof_by_contradiction"
      },
      "stepType": "line",
      "uuid": "d951d4e0-738f-453a-b356-c5c62635ca72"
    }
  ], [
  {
    "formula": {
      "ascii": "(s -> p) or (t -> q)",
      "latex": "(s \\rightarrow p) \\lor (t \\rightarrow q)",
      "userInput": "(s -> p) or (t -> q)"
    },
    "justification": {
      "refs": [],
      "rule": "premise"
    },
    "stepType": "line",
    "uuid": "fdff2248-8f2c-47d9-82bf-8df1389e65b8"
  },
  {
    "proof": [
      {
        "formula": {
          "ascii": "s -> p",
          "latex": "s \\rightarrow p",
          "userInput": "s -> p"
        },
        "justification": {
          "refs": [],
          "rule": "assumption"
        },
        "stepType": "line",
        "uuid": "a068b379-dea6-475e-b8c4-365dd769634c"
      },
      {
        "formula": {
          "ascii": "p or not p",
          "latex": "p \\lor \\lnot p",
          "userInput": "p or not p"
        },
        "justification": {
          "refs": [],
          "rule": "law_of_excluded_middle"
        },
        "stepType": "line",
        "uuid": "8e03c4e3-ae42-4b4f-a76c-bab4c0ce1509"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "p",
              "latex": "p",
              "userInput": "p"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "7aa1543f-17aa-4492-9ced-e3af683366ee"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "t",
                  "latex": "t",
                  "userInput": "t"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "3a688712-155a-410f-be03-c6a4765b4df7"
              },
              {
                "formula": {
                  "ascii": "p",
                  "latex": "p",
                  "userInput": "p"
                },
                "justification": {
                  "refs": [
                    "7aa1543f-17aa-4492-9ced-e3af683366ee"
                  ],
                  "rule": "copy"
                },
                "stepType": "line",
                "uuid": "9ce6b1eb-f997-4c74-9d50-dd00f744dcfd"
              }
            ],
            "stepType": "box",
            "uuid": "06e5fe7b-c179-4a05-a64a-5487ccc10ed6"
          },
          {
            "formula": {
              "ascii": "t -> p",
              "latex": "t \\rightarrow p",
              "userInput": "t -> p"
            },
            "justification": {
              "refs": [
                "06e5fe7b-c179-4a05-a64a-5487ccc10ed6"
              ],
              "rule": "implies_intro"
            },
            "stepType": "line",
            "uuid": "156e91e6-97ed-4503-b136-ff659f67b110"
          },
          {
            "formula": {
              "ascii": "(s -> q) or (t -> p)",
              "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
              "userInput": "(s -> q) or (t -> p)"
            },
            "justification": {
              "refs": [
                "156e91e6-97ed-4503-b136-ff659f67b110"
              ],
              "rule": "or_intro_2"
            },
            "stepType": "line",
            "uuid": "4de352dd-8030-4d0b-9a23-5e085f5da059"
          }
        ],
        "stepType": "box",
        "uuid": "f6dc188a-ba3d-4100-83b5-e90c6909ce7e"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "not p",
              "latex": "\\lnot p",
              "userInput": "not p"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "4016368e-b7e8-4033-9951-cc6d78e7a8a0"
          },
          {
            "formula": {
              "ascii": "not s",
              "latex": "\\lnot s",
              "userInput": "not s"
            },
            "justification": {
              "refs": [
                "a068b379-dea6-475e-b8c4-365dd769634c",
                "4016368e-b7e8-4033-9951-cc6d78e7a8a0"
              ],
              "rule": "modus_tollens"
            },
            "stepType": "line",
            "uuid": "b354c2e8-efcd-4491-ac5f-770e50b08965"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "s",
                  "latex": "s",
                  "userInput": "s"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "2eff4936-dd98-447a-96a6-c657fe056077"
              },
              {
                "formula": {
                  "ascii": "false",
                  "latex": "\\bot",
                  "userInput": "false"
                },
                "justification": {
                  "refs": [
                    "2eff4936-dd98-447a-96a6-c657fe056077",
                    "b354c2e8-efcd-4491-ac5f-770e50b08965"
                  ],
                  "rule": "not_elim"
                },
                "stepType": "line",
                "uuid": "63743938-1220-46f3-9700-e6434aef324a"
              },
              {
                "formula": {
                  "ascii": "q",
                  "latex": "q",
                  "userInput": "q"
                },
                "justification": {
                  "refs": [
                    "63743938-1220-46f3-9700-e6434aef324a"
                  ],
                  "rule": "bot_elim"
                },
                "stepType": "line",
                "uuid": "f4dbff7e-2662-4855-bdac-f34ca40356b5"
              }
            ],
            "stepType": "box",
            "uuid": "205d6948-bd5e-4018-bd10-c111f36b0556"
          },
          {
            "formula": {
              "ascii": "s -> q",
              "latex": "s \\rightarrow q",
              "userInput": "s -> q"
            },
            "justification": {
              "refs": [
                "205d6948-bd5e-4018-bd10-c111f36b0556"
              ],
              "rule": "implies_intro"
            },
            "stepType": "line",
            "uuid": "5fac02c6-86ef-49bd-a95e-6aa3bdc0b7b7"
          },
          {
            "formula": {
              "ascii": "(s -> q) or (t -> p)",
              "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
              "userInput": "(s -> q) or (t -> p)"
            },
            "justification": {
              "refs": [
                "5fac02c6-86ef-49bd-a95e-6aa3bdc0b7b7"
              ],
              "rule": "or_intro_1"
            },
            "stepType": "line",
            "uuid": "46d839f1-6b5c-4f6c-bf19-67f06496a318"
          }
        ],
        "stepType": "box",
        "uuid": "35568d80-49d7-435a-acc1-77f80af42155"
      },
      {
        "formula": {
          "ascii": "(s -> q) or (t -> p)",
          "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
          "userInput": "(s -> q) or (t -> p)"
        },
        "justification": {
          "refs": [
            "8e03c4e3-ae42-4b4f-a76c-bab4c0ce1509",
            "f6dc188a-ba3d-4100-83b5-e90c6909ce7e",
            "35568d80-49d7-435a-acc1-77f80af42155"
          ],
          "rule": "or_elim"
        },
        "stepType": "line",
        "uuid": "70ba08ee-c9e0-4986-9c86-5aced755839e"
      }
    ],
    "stepType": "box",
    "uuid": "761c4eed-3ac7-4e59-8a95-6fe9d3f21f8c"
  },
  {
    "proof": [
      {
        "formula": {
          "ascii": "t -> q",
          "latex": "t \\rightarrow q",
          "userInput": "t -> q"
        },
        "justification": {
          "refs": [],
          "rule": "assumption"
        },
        "stepType": "line",
        "uuid": "f33ffd2b-bf58-4009-b064-4ebbdd31b4d3"
      },
      {
        "formula": {
          "ascii": "q or not q",
          "latex": "q \\lor \\lnot q",
          "userInput": "q or not q"
        },
        "justification": {
          "refs": [],
          "rule": "law_of_excluded_middle"
        },
        "stepType": "line",
        "uuid": "c8168e57-7a73-4e95-8cbb-9b9ad5806e0c"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "q",
              "latex": "q",
              "userInput": "q"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "a0bb35a5-8434-4170-b4be-cef6cfa032d2"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "s",
                  "latex": "s",
                  "userInput": "s"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "9cb24b55-1fbe-48b1-900a-d048cf9a7e97"
              },
              {
                "formula": {
                  "ascii": "q",
                  "latex": "q",
                  "userInput": "q"
                },
                "justification": {
                  "refs": [
                    "a0bb35a5-8434-4170-b4be-cef6cfa032d2"
                  ],
                  "rule": "copy"
                },
                "stepType": "line",
                "uuid": "092fdb92-80ff-4f2c-b9ae-a0a9c72b236d"
              }
            ],
            "stepType": "box",
            "uuid": "5f71d499-4698-4671-af34-4d57df485b3a"
          },
          {
            "formula": {
              "ascii": "s -> q",
              "latex": "s \\rightarrow q",
              "userInput": "s -> q"
            },
            "justification": {
              "refs": [
                "5f71d499-4698-4671-af34-4d57df485b3a"
              ],
              "rule": "implies_intro"
            },
            "stepType": "line",
            "uuid": "2e52e640-ddaa-469b-a5dc-306b6b498a34"
          },
          {
            "formula": {
              "ascii": "(s -> q) or (t -> p)",
              "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
              "userInput": "(s -> q) or (t -> p)"
            },
            "justification": {
              "refs": [
                "2e52e640-ddaa-469b-a5dc-306b6b498a34"
              ],
              "rule": "or_intro_1"
            },
            "stepType": "line",
            "uuid": "7a7b0703-aa16-4145-a191-b7d84af0b4d3"
          }
        ],
        "stepType": "box",
        "uuid": "ffa62b01-cc2e-47ae-a3b7-b8d8b546679d"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "not q",
              "latex": "\\lnot q",
              "userInput": "not q"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "f24601e1-a158-435d-90bc-9114ea64e25e"
          },
          {
            "formula": {
              "ascii": "not t",
              "latex": "\\lnot t",
              "userInput": "not t"
            },
            "justification": {
              "refs": [
                "f33ffd2b-bf58-4009-b064-4ebbdd31b4d3",
                "f24601e1-a158-435d-90bc-9114ea64e25e"
              ],
              "rule": "modus_tollens"
            },
            "stepType": "line",
            "uuid": "026194b0-0293-4d0e-82a8-68fcfb266c22"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "t",
                  "latex": "t",
                  "userInput": "t"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "2e7fb016-47b4-47e5-9c13-3c9bcf88a3c1"
              },
              {
                "formula": {
                  "ascii": "false",
                  "latex": "\\bot",
                  "userInput": "false"
                },
                "justification": {
                  "refs": [
                    "2e7fb016-47b4-47e5-9c13-3c9bcf88a3c1",
                    "026194b0-0293-4d0e-82a8-68fcfb266c22"
                  ],
                  "rule": "not_elim"
                },
                "stepType": "line",
                "uuid": "a8c8e4dc-14d4-48f0-9338-01fb6e8d54b6"
              },
              {
                "formula": {
                  "ascii": "p",
                  "latex": "p",
                  "userInput": "p"
                },
                "justification": {
                  "refs": [
                    "a8c8e4dc-14d4-48f0-9338-01fb6e8d54b6"
                  ],
                  "rule": "bot_elim"
                },
                "stepType": "line",
                "uuid": "f3ec5a42-0608-4d3e-abe5-aa8246242c73"
              }
            ],
            "stepType": "box",
            "uuid": "211f7763-a91d-4b38-827f-10fbb4498fba"
          },
          {
            "formula": {
              "ascii": "t -> p",
              "latex": "t \\rightarrow p",
              "userInput": "t -> p"
            },
            "justification": {
              "refs": [
                "211f7763-a91d-4b38-827f-10fbb4498fba"
              ],
              "rule": "implies_intro"
            },
            "stepType": "line",
            "uuid": "10f7010e-0ea7-4b0f-8b81-92bc6bfed91d"
          },
          {
            "formula": {
              "ascii": "(s -> q) or (t -> p)",
              "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
              "userInput": "(s -> q) or (t -> p)"
            },
            "justification": {
              "refs": [
                "10f7010e-0ea7-4b0f-8b81-92bc6bfed91d"
              ],
              "rule": "or_intro_2"
            },
            "stepType": "line",
            "uuid": "f375fbc3-eda6-45f2-b1fa-c8d1dd7d00cb"
          }
        ],
        "stepType": "box",
        "uuid": "8336d04f-1cd5-45e3-b0ef-9433195a92ed"
      },
      {
        "formula": {
          "ascii": "(s -> q) or (t -> p)",
          "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
          "userInput": "(s -> q) or (t -> p)"
        },
        "justification": {
          "refs": [
            "c8168e57-7a73-4e95-8cbb-9b9ad5806e0c",
            "ffa62b01-cc2e-47ae-a3b7-b8d8b546679d",
            "8336d04f-1cd5-45e3-b0ef-9433195a92ed"
          ],
          "rule": "or_elim"
        },
        "stepType": "line",
        "uuid": "5a3dc934-38be-4fa9-ab51-b437e1b89072"
      }
    ],
    "stepType": "box",
    "uuid": "9457f8b5-7dc2-4e30-9837-9f8497bf941b"
  },
  {
    "formula": {
      "ascii": "(s -> q) or (t -> p)",
      "latex": "(s \\rightarrow q) \\lor (t \\rightarrow p)",
      "userInput": "(s -> q) or (t -> p)"
    },
    "justification": {
      "refs": [
        "fdff2248-8f2c-47d9-82bf-8df1389e65b8",
        "761c4eed-3ac7-4e59-8a95-6fe9d3f21f8c",
        "9457f8b5-7dc2-4e30-9837-9f8497bf941b"
      ],
      "rule": "or_elim"
    },
    "stepType": "line",
    "uuid": "108ad9e1-d366-4100-bc66-51780f00a7ac"
  }
], [
  {
    "proof": [
      {
        "formula": {
          "ascii": "p -> q",
          "latex": "p \\rightarrow q",
          "userInput": "p -> q"
        },
        "justification": {
          "refs": [],
          "rule": "assumption"
        },
        "stepType": "line",
        "uuid": "cb0e66a5-b602-46ef-8a2d-4e5528d3c879"
      },
      {
        "proof": [
          {
            "formula": {
              "ascii": "not p -> q",
              "latex": "\\lnot p \\rightarrow q",
              "userInput": "not p -> q"
            },
            "justification": {
              "refs": [],
              "rule": "assumption"
            },
            "stepType": "line",
            "uuid": "2435e17f-cca6-4d5d-9322-4c66db601cbc"
          },
          {
            "proof": [
              {
                "formula": {
                  "ascii": "not q",
                  "latex": "\\lnot q",
                  "userInput": "not q"
                },
                "justification": {
                  "refs": [],
                  "rule": "assumption"
                },
                "stepType": "line",
                "uuid": "630887fe-374f-46d9-a7cf-5be170d80ba9"
              },
              {
                "formula": {
                  "ascii": "not not p",
                  "latex": "\\lnot \\lnot p",
                  "userInput": "not not p"
                },
                "justification": {
                  "refs": [
                    "2435e17f-cca6-4d5d-9322-4c66db601cbc",
                    "630887fe-374f-46d9-a7cf-5be170d80ba9"
                  ],
                  "rule": "modus_tollens"
                },
                "stepType": "line",
                "uuid": "a3be3344-6a75-48b1-86d2-896a336f4420"
              },
              {
                "formula": {
                  "ascii": "p",
                  "latex": "p",
                  "userInput": "p"
                },
                "justification": {
                  "refs": [
                    "a3be3344-6a75-48b1-86d2-896a336f4420"
                  ],
                  "rule": "not_not_elim"
                },
                "stepType": "line",
                "uuid": "9c988134-c550-4fc1-8bd2-ec814312db54"
              },
              {
                "formula": {
                  "ascii": "q",
                  "latex": "q",
                  "userInput": "q"
                },
                "justification": {
                  "refs": [
                    "9c988134-c550-4fc1-8bd2-ec814312db54",
                    "cb0e66a5-b602-46ef-8a2d-4e5528d3c879"
                  ],
                  "rule": "implies_elim"
                },
                "stepType": "line",
                "uuid": "323a994a-c81a-46c6-bfa2-f383042258f8"
              },
              {
                "formula": {
                  "ascii": "false",
                  "latex": "\\bot",
                  "userInput": "false"
                },
                "justification": {
                  "refs": [
                    "323a994a-c81a-46c6-bfa2-f383042258f8",
                    "630887fe-374f-46d9-a7cf-5be170d80ba9"
                  ],
                  "rule": "not_elim"
                },
                "stepType": "line",
                "uuid": "0a142918-b4c1-4aae-aa7a-9c4c1c340260"
              }
            ],
            "stepType": "box",
            "uuid": "678269ac-8f46-4cac-a605-fe71034ffdd7"
          },
          {
            "formula": {
              "ascii": "q",
              "latex": "q",
              "userInput": "q"
            },
            "justification": {
              "refs": [
                "678269ac-8f46-4cac-a605-fe71034ffdd7"
              ],
              "rule": "proof_by_contradiction"
            },
            "stepType": "line",
            "uuid": "22d5f824-243b-4939-bbad-49be74a55735"
          }
        ],
        "stepType": "box",
        "uuid": "4389ef78-0d1f-4004-b5fe-1a0897cc81f8"
      },
      {
        "formula": {
          "ascii": "(not p -> q) -> q",
          "latex": "(\\lnot p \\rightarrow q) \\rightarrow q",
          "userInput": "(not p -> q) -> q"
        },
        "justification": {
          "refs": [
            "4389ef78-0d1f-4004-b5fe-1a0897cc81f8"
          ],
          "rule": "implies_intro"
        },
        "stepType": "line",
        "uuid": "1ab33a76-e672-40f3-a685-15105c6dc0d4"
      }
    ],
    "stepType": "box",
    "uuid": "33a5fe1a-497d-4feb-954f-5f2cb2c76288"
  },
  {
    "formula": {
      "ascii": "(p -> q) -> ((not p -> q) -> q)",
      "latex": "(p \\rightarrow q) \\rightarrow ((\\lnot p \\rightarrow q) \\rightarrow q)",
      "userInput": "(p -> q) -> ((not p -> q) -> q)"
    },
    "justification": {
      "refs": [
        "33a5fe1a-497d-4feb-954f-5f2cb2c76288"
      ],
      "rule": "implies_intro"
    },
    "stepType": "line",
    "uuid": "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"
  }
]
]

export default examples;
