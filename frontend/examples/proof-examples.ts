import { ProofWithMetadata } from "@/types/types";

const examples: ProofWithMetadata[] = [
  {
    id: "0",
    title: "Proof with errors",
    logicName: "propositionalLogic",
    createdAt: "2025-06-06T00:00:00.000Z",
    proof: [
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
        boxInfo: { freshVar: null },
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
    ],
  },
  {
    id: "2",
    title: "Some example",
    logicName: "propositionalLogic",
    proof: [
      {
        formula: {
          ascii: "not (not p or not q)",
          latex: "\\lnot (\\lnot p \\lor \\lnot q)",
          userInput: "not (not p or not q)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
      },
      {
        proof: [
          {
            formula: {
              ascii: "not p",
              latex: "\\lnot p",
              userInput: "not p",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "be90fd87-e01d-4cb3-aa6a-f01950f5ca58",
          },
          {
            formula: {
              ascii: "not p or not q",
              latex: "\\lnot p \\lor \\lnot q",
              userInput: "not p or not q",
            },
            justification: {
              refs: ["be90fd87-e01d-4cb3-aa6a-f01950f5ca58"],
              rule: "or_intro_1",
            },
            stepType: "line",
            uuid: "ae94b166-7a4c-4594-a4d3-ff0c8315b941",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "ae94b166-7a4c-4594-a4d3-ff0c8315b941",
                "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
              ],
              rule: "not_elim",
            },
            stepType: "line",
            uuid: "b06aa72f-2679-4431-9702-c17fb3b11ef6",
          },
        ],
        boxInfo: { freshVar: null },
        stepType: "box",
        uuid: "27d558d0-26ca-42b0-ab45-f6e6b2a317dc",
      },
      {
        formula: {
          ascii: "p",
          latex: "p",
          userInput: "p",
        },
        justification: {
          refs: ["27d558d0-26ca-42b0-ab45-f6e6b2a317dc"],
          rule: "proof_by_contradiction",
        },
        stepType: "line",
        uuid: "6a808abb-720e-47ee-8dfc-cd2b84eea8ee",
      },
      {
        proof: [
          {
            formula: {
              ascii: "not q",
              latex: "\\lnot q",
              userInput: "not q",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "5bff22c5-2c0b-45fd-856e-d6663ed55af3",
          },
          {
            formula: {
              ascii: "not p or not q",
              latex: "\\lnot p \\lor \\lnot q",
              userInput: "not p or not q",
            },
            justification: {
              refs: ["5bff22c5-2c0b-45fd-856e-d6663ed55af3"],
              rule: "or_intro_2",
            },
            stepType: "line",
            uuid: "944b9baa-74a1-485f-89eb-507f4c424aa9",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "944b9baa-74a1-485f-89eb-507f4c424aa9",
                "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
              ],
              rule: "not_elim",
            },
            stepType: "line",
            uuid: "1ecb02c9-93b0-4fd8-a56d-396f3f4b371f",
          },
        ],
        boxInfo: { freshVar: null },
        stepType: "box",
        uuid: "2712afba-e726-48a4-919a-cc41085fe690",
      },
      {
        formula: {
          ascii: "q",
          latex: "q",
          userInput: "q",
        },
        justification: {
          refs: ["2712afba-e726-48a4-919a-cc41085fe690"],
          rule: "proof_by_contradiction",
        },
        stepType: "line",
        uuid: "fa6243a3-70a5-444e-a002-e176267022b1",
      },
      {
        formula: {
          ascii: "p and q",
          latex: "p \\land q",
          userInput: "p and q",
        },
        justification: {
          refs: [
            "6a808abb-720e-47ee-8dfc-cd2b84eea8ee",
            "fa6243a3-70a5-444e-a002-e176267022b1",
          ],
          rule: "and_intro",
        },
        stepType: "line",
        uuid: "6bbb407a-36c9-4aae-ae22-40bfd459008b",
      },
    ],
  },
  {
    id: "3",
    title: "Example 3",
    logicName: "propositionalLogic",
    proof: [
      {
        formula: {
          ascii: "q or not q",
          latex: "q \\lor \\lnot q",
          userInput: "q or not q",
        },
        justification: {
          refs: [],
          rule: "law_of_excluded_middle",
        },
        stepType: "line",
        uuid: "fdff2248-8f2c-47d9-82bf-8df1389e65b8",
      },
      {
        proof: [
          {
            formula: {
              ascii: "q",
              latex: "q",
              userInput: "q",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "5f751399-1d2a-4d38-aa3f-77a7e8b3cf54",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "p",
                  latex: "p",
                  userInput: "p",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "0e92a863-60cd-4b5f-8138-41ed7f897353",
              },
              {
                formula: {
                  ascii: "q",
                  latex: "q",
                  userInput: "q",
                },
                justification: {
                  refs: ["5f751399-1d2a-4d38-aa3f-77a7e8b3cf54"],
                  rule: "copy",
                },
                stepType: "line",
                uuid: "e179f6c7-7c6c-40bd-9674-dea19a249716",
              },
            ],
            boxInfo: { freshVar: null },
            stepType: "box",
            uuid: "5ce3a68e-06c1-45dc-b960-9b19660fac0d",
          },
          {
            formula: {
              ascii: "p -> q",
              latex: "p \\rightarrow q",
              userInput: "p -> q",
            },
            justification: {
              refs: ["5ce3a68e-06c1-45dc-b960-9b19660fac0d"],
              rule: "implies_intro",
            },
            stepType: "line",
            uuid: "6029a38b-1bd8-411d-805e-756f1753f818",
          },
          {
            formula: {
              ascii: "(p -> q) or (q -> r)",
              latex: "(p \\rightarrow q) \\lor (q \\rightarrow r)",
              userInput: "(p -> q) or (q -> r)",
            },
            justification: {
              refs: ["6029a38b-1bd8-411d-805e-756f1753f818"],
              rule: "or_intro_1",
            },
            stepType: "line",
            uuid: "268fedd0-36e4-401b-be0f-f6f7bd8cc4e2",
          },
        ],
        stepType: "box",
        boxInfo: { freshVar: null },
        uuid: "6c132815-cf2a-4a8f-b3b0-034bd0c6e09b",
      },
      {
        proof: [
          {
            formula: {
              ascii: "not q",
              latex: "\\lnot q",
              userInput: "not q",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "c0c1e561-708a-4457-a6b3-98730e8ab3dc",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "q",
                  latex: "q",
                  userInput: "q",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "e5414ac3-cc24-409d-ab25-af857e428e02",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "bot",
                },
                justification: {
                  refs: [
                    "e5414ac3-cc24-409d-ab25-af857e428e02",
                    "c0c1e561-708a-4457-a6b3-98730e8ab3dc",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "780313ea-161b-4097-a276-6a4021e9e594",
              },
              {
                formula: {
                  ascii: "r",
                  latex: "r",
                  userInput: "r",
                },
                justification: {
                  refs: ["780313ea-161b-4097-a276-6a4021e9e594"],
                  rule: "bot_elim",
                },
                stepType: "line",
                uuid: "7e416170-eac3-46c0-8b11-937da2c782e4",
              },
            ],
            boxInfo: { freshVar: null },
            stepType: "box",
            uuid: "de93fbf7-fa0b-4af9-8214-1fc552a4f753",
          },
          {
            formula: {
              ascii: "q -> r",
              latex: "q \\rightarrow r",
              userInput: "q -> r",
            },
            justification: {
              refs: ["de93fbf7-fa0b-4af9-8214-1fc552a4f753"],
              rule: "implies_intro",
            },
            stepType: "line",
            uuid: "06c38110-1b7b-42e3-a37b-f6a7cf716b1f",
          },
          {
            formula: {
              ascii: "(p -> q) or (q -> r)",
              latex: "(p \\rightarrow q) \\lor (q \\rightarrow r)",
              userInput: "(p -> q) or (q -> r)",
            },
            justification: {
              refs: ["06c38110-1b7b-42e3-a37b-f6a7cf716b1f"],
              rule: "or_intro_2",
            },
            stepType: "line",
            uuid: "c6cc534f-4eb1-439a-a67f-0663ca3fc9e3",
          },
        ],
        boxInfo: { freshVar: null },
        stepType: "box",
        uuid: "00c5489a-2ce2-4e89-a325-cf1990dc3f48",
      },
      {
        formula: {
          ascii: "(p -> q) or (q -> r)",
          latex: "(p \\rightarrow q) \\lor (q \\rightarrow r)",
          userInput: "(p -> q) or (q -> r)",
        },
        justification: {
          refs: [
            "fdff2248-8f2c-47d9-82bf-8df1389e65b8",
            "6c132815-cf2a-4a8f-b3b0-034bd0c6e09b",
            "00c5489a-2ce2-4e89-a325-cf1990dc3f48",
          ],
          rule: "or_elim",
        },
        stepType: "line",
        uuid: "92fa8c74-77cd-4afa-877c-5481e23927e0",
      },
    ],
  },
  {
    id: "4",
    title: "Example 4",
    logicName: "propositionalLogic",
    proof: [
      {
        formula: {
          ascii: "p and q",
          latex: "p \\land q",
          userInput: "p and q",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
      },
      {
        proof: [
          {
            formula: {
              ascii: "not p or not q",
              latex: "\\lnot p \\lor \\lnot q",
              userInput: "not p or not q",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "b37f344a-0b66-4d24-9963-b50414f4060b",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "not p",
                  latex: "\\lnot p",
                  userInput: "not p",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "797913eb-e885-4304-a93b-94557166323a",
              },
              {
                formula: {
                  ascii: "p",
                  latex: "p",
                  userInput: "p",
                },
                justification: {
                  refs: ["a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"],
                  rule: "and_elim_1",
                },
                stepType: "line",
                uuid: "9cc2100e-6d8e-45f3-869c-6b420025ec90",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "9cc2100e-6d8e-45f3-869c-6b420025ec90",
                    "797913eb-e885-4304-a93b-94557166323a",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "61e2c78e-7cc7-401e-9607-83a7c6c8f337",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "d62046aa-1f0f-41e9-90fe-6efd82d43257",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "not q",
                  latex: "\\lnot q",
                  userInput: "not q",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "b8af1eae-e3db-4e18-83ad-8344dec4f39c",
              },
              {
                formula: {
                  ascii: "q",
                  latex: "q",
                  userInput: "q",
                },
                justification: {
                  refs: ["a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"],
                  rule: "and_elim_2",
                },
                stepType: "line",
                uuid: "c8c94b02-3ec3-4776-83f7-d1815297c18c",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "c8c94b02-3ec3-4776-83f7-d1815297c18c",
                    "b8af1eae-e3db-4e18-83ad-8344dec4f39c",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "898cdcca-de40-4a31-bb47-2bdd5c94a7ca",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "4dccce80-d117-483b-be7e-44e5679818bb",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "b37f344a-0b66-4d24-9963-b50414f4060b",
                "d62046aa-1f0f-41e9-90fe-6efd82d43257",
                "4dccce80-d117-483b-be7e-44e5679818bb",
              ],
              rule: "or_elim",
            },
            stepType: "line",
            uuid: "11293057-70ce-4940-a1c6-d0d3bbe721ef",
          },
        ],
        stepType: "box",
        boxInfo: { freshVar: null },
        uuid: "5603913b-ffe8-423d-b981-3b086dbc05f4",
      },
      {
        formula: {
          ascii: "not (not p or not q)",
          latex: "\\lnot (\\lnot p \\lor \\lnot q)",
          userInput: "not (not p or not q)",
        },
        justification: {
          refs: ["5603913b-ffe8-423d-b981-3b086dbc05f4"],
          rule: "not_intro",
        },
        stepType: "line",
        uuid: "8a0e268a-abd4-4729-9dca-9891ac97dfb5",
      },
    ],
  },
  {
    id: "5",
    title: "Another proof",
    logicName: "propositionalLogic",
    proof: [
      {
        formula: {
          ascii: "p -> q",
          latex: "p \\rightarrow q",
          userInput: "p -> q",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
      },
      {
        proof: [
          {
            formula: {
              ascii: "not (not p or q)",
              latex: "\\lnot (\\lnot p \\lor q)",
              userInput: "not (not p or q)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "f7f833ec-ee6f-4219-91c9-6f5957bd0c2b",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "not p",
                  latex: "\\lnot p",
                  userInput: "not p",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "74b876a0-006b-482f-94c4-90619f9bf79a",
              },
              {
                formula: {
                  ascii: "not p or q",
                  latex: "\\lnot p \\lor q",
                  userInput: "not p or q",
                },
                justification: {
                  refs: ["74b876a0-006b-482f-94c4-90619f9bf79a"],
                  rule: "or_intro_1",
                },
                stepType: "line",
                uuid: "5801fc90-2e62-441a-b02f-7073ff21581b",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "5801fc90-2e62-441a-b02f-7073ff21581b",
                    "f7f833ec-ee6f-4219-91c9-6f5957bd0c2b",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "cbc8b30d-1b03-4882-aba8-e06c08546cc2",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "f096cb90-0c6c-4c51-aaf6-25efe74762f6",
          },
          {
            formula: {
              ascii: "p",
              latex: "p",
              userInput: "p",
            },
            justification: {
              refs: ["f096cb90-0c6c-4c51-aaf6-25efe74762f6"],
              rule: "proof_by_contradiction",
            },
            stepType: "line",
            uuid: "ad5cd950-032e-4238-bfc0-3ebd9b0c6f3f",
          },
          {
            formula: {
              ascii: "q",
              latex: "q",
              userInput: "q",
            },
            justification: {
              refs: [
                "ad5cd950-032e-4238-bfc0-3ebd9b0c6f3f",
                "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
              ],
              rule: "implies_elim",
            },
            stepType: "line",
            uuid: "8435c1e4-4c12-4696-8701-1cb481a5846d",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "q",
                  latex: "q",
                  userInput: "q",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "7b648e57-e9b1-43d0-a744-a58e2472af14",
              },
              {
                formula: {
                  ascii: "not p or q",
                  latex: "\\lnot p \\lor q",
                  userInput: "not p or q",
                },
                justification: {
                  refs: ["7b648e57-e9b1-43d0-a744-a58e2472af14"],
                  rule: "or_intro_2",
                },
                stepType: "line",
                uuid: "bfbd7820-2c5f-4a7f-b9b8-7302c827a423",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "bfbd7820-2c5f-4a7f-b9b8-7302c827a423",
                    "f7f833ec-ee6f-4219-91c9-6f5957bd0c2b",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "032fd0c2-11f1-4eed-b5e8-57bbcfa37ffe",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "28c23021-8b01-412d-b4d2-49bb5225a9d0",
          },
          {
            formula: {
              ascii: "not q",
              latex: "\\lnot q",
              userInput: "not q",
            },
            justification: {
              refs: ["28c23021-8b01-412d-b4d2-49bb5225a9d0"],
              rule: "not_intro",
            },
            stepType: "line",
            uuid: "80ed2487-f0e9-4be5-b26b-bbdd218b1c47",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "8435c1e4-4c12-4696-8701-1cb481a5846d",
                "80ed2487-f0e9-4be5-b26b-bbdd218b1c47",
              ],
              rule: "not_elim",
            },
            stepType: "line",
            uuid: "8c95f13f-9453-4d32-8d62-b7e16dcab1a2",
          },
        ],
        stepType: "box",
        boxInfo: { freshVar: null },
        uuid: "540d0506-ee84-413b-9712-a159704af2b6",
      },
      {
        formula: {
          ascii: "not p or q",
          latex: "\\lnot p \\lor q",
          userInput: "not p or q",
        },
        justification: {
          refs: ["540d0506-ee84-413b-9712-a159704af2b6"],
          rule: "proof_by_contradiction",
        },
        stepType: "line",
        uuid: "d951d4e0-738f-453a-b356-c5c62635ca72",
      },
    ],
  },
  {
    id: "6",
    title: "Chunky Example",
    logicName: "propositionalLogic",
    proof: [
      {
        formula: {
          ascii: "(s -> p) or (t -> q)",
          latex: "(s \\rightarrow p) \\lor (t \\rightarrow q)",
          userInput: "(s -> p) or (t -> q)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "fdff2248-8f2c-47d9-82bf-8df1389e65b8",
      },
      {
        proof: [
          {
            formula: {
              ascii: "s -> p",
              latex: "s \\rightarrow p",
              userInput: "s -> p",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "a068b379-dea6-475e-b8c4-365dd769634c",
          },
          {
            formula: {
              ascii: "p or not p",
              latex: "p \\lor \\lnot p",
              userInput: "p or not p",
            },
            justification: {
              refs: [],
              rule: "law_of_excluded_middle",
            },
            stepType: "line",
            uuid: "8e03c4e3-ae42-4b4f-a76c-bab4c0ce1509",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "p",
                  latex: "p",
                  userInput: "p",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "7aa1543f-17aa-4492-9ced-e3af683366ee",
              },
              {
                proof: [
                  {
                    formula: {
                      ascii: "t",
                      latex: "t",
                      userInput: "t",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "3a688712-155a-410f-be03-c6a4765b4df7",
                  },
                  {
                    formula: {
                      ascii: "p",
                      latex: "p",
                      userInput: "p",
                    },
                    justification: {
                      refs: ["7aa1543f-17aa-4492-9ced-e3af683366ee"],
                      rule: "copy",
                    },
                    stepType: "line",
                    uuid: "9ce6b1eb-f997-4c74-9d50-dd00f744dcfd",
                  },
                ],
                stepType: "box",
                boxInfo: { freshVar: null },
                uuid: "06e5fe7b-c179-4a05-a64a-5487ccc10ed6",
              },
              {
                formula: {
                  ascii: "t -> p",
                  latex: "t \\rightarrow p",
                  userInput: "t -> p",
                },
                justification: {
                  refs: ["06e5fe7b-c179-4a05-a64a-5487ccc10ed6"],
                  rule: "implies_intro",
                },
                stepType: "line",
                uuid: "156e91e6-97ed-4503-b136-ff659f67b110",
              },
              {
                formula: {
                  ascii: "(s -> q) or (t -> p)",
                  latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
                  userInput: "(s -> q) or (t -> p)",
                },
                justification: {
                  refs: ["156e91e6-97ed-4503-b136-ff659f67b110"],
                  rule: "or_intro_2",
                },
                stepType: "line",
                uuid: "4de352dd-8030-4d0b-9a23-5e085f5da059",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "f6dc188a-ba3d-4100-83b5-e90c6909ce7e",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "not p",
                  latex: "\\lnot p",
                  userInput: "not p",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "4016368e-b7e8-4033-9951-cc6d78e7a8a0",
              },
              {
                formula: {
                  ascii: "not s",
                  latex: "\\lnot s",
                  userInput: "not s",
                },
                justification: {
                  refs: [
                    "a068b379-dea6-475e-b8c4-365dd769634c",
                    "4016368e-b7e8-4033-9951-cc6d78e7a8a0",
                  ],
                  rule: "modus_tollens",
                },
                stepType: "line",
                uuid: "b354c2e8-efcd-4491-ac5f-770e50b08965",
              },
              {
                proof: [
                  {
                    formula: {
                      ascii: "s",
                      latex: "s",
                      userInput: "s",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "2eff4936-dd98-447a-96a6-c657fe056077",
                  },
                  {
                    formula: {
                      ascii: "false",
                      latex: "\\bot",
                      userInput: "false",
                    },
                    justification: {
                      refs: [
                        "2eff4936-dd98-447a-96a6-c657fe056077",
                        "b354c2e8-efcd-4491-ac5f-770e50b08965",
                      ],
                      rule: "not_elim",
                    },
                    stepType: "line",
                    uuid: "63743938-1220-46f3-9700-e6434aef324a",
                  },
                  {
                    formula: {
                      ascii: "q",
                      latex: "q",
                      userInput: "q",
                    },
                    justification: {
                      refs: ["63743938-1220-46f3-9700-e6434aef324a"],
                      rule: "bot_elim",
                    },
                    stepType: "line",
                    uuid: "f4dbff7e-2662-4855-bdac-f34ca40356b5",
                  },
                ],
                stepType: "box",
                boxInfo: { freshVar: null },
                uuid: "205d6948-bd5e-4018-bd10-c111f36b0556",
              },
              {
                formula: {
                  ascii: "s -> q",
                  latex: "s \\rightarrow q",
                  userInput: "s -> q",
                },
                justification: {
                  refs: ["205d6948-bd5e-4018-bd10-c111f36b0556"],
                  rule: "implies_intro",
                },
                stepType: "line",
                uuid: "5fac02c6-86ef-49bd-a95e-6aa3bdc0b7b7",
              },
              {
                formula: {
                  ascii: "(s -> q) or (t -> p)",
                  latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
                  userInput: "(s -> q) or (t -> p)",
                },
                justification: {
                  refs: ["5fac02c6-86ef-49bd-a95e-6aa3bdc0b7b7"],
                  rule: "or_intro_1",
                },
                stepType: "line",
                uuid: "46d839f1-6b5c-4f6c-bf19-67f06496a318",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "35568d80-49d7-435a-acc1-77f80af42155",
          },
          {
            formula: {
              ascii: "(s -> q) or (t -> p)",
              latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
              userInput: "(s -> q) or (t -> p)",
            },
            justification: {
              refs: [
                "8e03c4e3-ae42-4b4f-a76c-bab4c0ce1509",
                "f6dc188a-ba3d-4100-83b5-e90c6909ce7e",
                "35568d80-49d7-435a-acc1-77f80af42155",
              ],
              rule: "or_elim",
            },
            stepType: "line",
            uuid: "70ba08ee-c9e0-4986-9c86-5aced755839e",
          },
        ],
        stepType: "box",
        boxInfo: { freshVar: null },
        uuid: "761c4eed-3ac7-4e59-8a95-6fe9d3f21f8c",
      },
      {
        proof: [
          {
            formula: {
              ascii: "t -> q",
              latex: "t \\rightarrow q",
              userInput: "t -> q",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "f33ffd2b-bf58-4009-b064-4ebbdd31b4d3",
          },
          {
            formula: {
              ascii: "q or not q",
              latex: "q \\lor \\lnot q",
              userInput: "q or not q",
            },
            justification: {
              refs: [],
              rule: "law_of_excluded_middle",
            },
            stepType: "line",
            uuid: "c8168e57-7a73-4e95-8cbb-9b9ad5806e0c",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "q",
                  latex: "q",
                  userInput: "q",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "a0bb35a5-8434-4170-b4be-cef6cfa032d2",
              },
              {
                proof: [
                  {
                    formula: {
                      ascii: "s",
                      latex: "s",
                      userInput: "s",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "9cb24b55-1fbe-48b1-900a-d048cf9a7e97",
                  },
                  {
                    formula: {
                      ascii: "q",
                      latex: "q",
                      userInput: "q",
                    },
                    justification: {
                      refs: ["a0bb35a5-8434-4170-b4be-cef6cfa032d2"],
                      rule: "copy",
                    },
                    stepType: "line",
                    uuid: "092fdb92-80ff-4f2c-b9ae-a0a9c72b236d",
                  },
                ],
                stepType: "box",
                boxInfo: { freshVar: null },
                uuid: "5f71d499-4698-4671-af34-4d57df485b3a",
              },
              {
                formula: {
                  ascii: "s -> q",
                  latex: "s \\rightarrow q",
                  userInput: "s -> q",
                },
                justification: {
                  refs: ["5f71d499-4698-4671-af34-4d57df485b3a"],
                  rule: "implies_intro",
                },
                stepType: "line",
                uuid: "2e52e640-ddaa-469b-a5dc-306b6b498a34",
              },
              {
                formula: {
                  ascii: "(s -> q) or (t -> p)",
                  latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
                  userInput: "(s -> q) or (t -> p)",
                },
                justification: {
                  refs: ["2e52e640-ddaa-469b-a5dc-306b6b498a34"],
                  rule: "or_intro_1",
                },
                stepType: "line",
                uuid: "7a7b0703-aa16-4145-a191-b7d84af0b4d3",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "ffa62b01-cc2e-47ae-a3b7-b8d8b546679d",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "not q",
                  latex: "\\lnot q",
                  userInput: "not q",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "f24601e1-a158-435d-90bc-9114ea64e25e",
              },
              {
                formula: {
                  ascii: "not t",
                  latex: "\\lnot t",
                  userInput: "not t",
                },
                justification: {
                  refs: [
                    "f33ffd2b-bf58-4009-b064-4ebbdd31b4d3",
                    "f24601e1-a158-435d-90bc-9114ea64e25e",
                  ],
                  rule: "modus_tollens",
                },
                stepType: "line",
                uuid: "026194b0-0293-4d0e-82a8-68fcfb266c22",
              },
              {
                proof: [
                  {
                    formula: {
                      ascii: "t",
                      latex: "t",
                      userInput: "t",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "2e7fb016-47b4-47e5-9c13-3c9bcf88a3c1",
                  },
                  {
                    formula: {
                      ascii: "false",
                      latex: "\\bot",
                      userInput: "false",
                    },
                    justification: {
                      refs: [
                        "2e7fb016-47b4-47e5-9c13-3c9bcf88a3c1",
                        "026194b0-0293-4d0e-82a8-68fcfb266c22",
                      ],
                      rule: "not_elim",
                    },
                    stepType: "line",
                    uuid: "a8c8e4dc-14d4-48f0-9338-01fb6e8d54b6",
                  },
                  {
                    formula: {
                      ascii: "p",
                      latex: "p",
                      userInput: "p",
                    },
                    justification: {
                      refs: ["a8c8e4dc-14d4-48f0-9338-01fb6e8d54b6"],
                      rule: "bot_elim",
                    },
                    stepType: "line",
                    uuid: "f3ec5a42-0608-4d3e-abe5-aa8246242c73",
                  },
                ],
                stepType: "box",
                boxInfo: { freshVar: null },
                uuid: "211f7763-a91d-4b38-827f-10fbb4498fba",
              },
              {
                formula: {
                  ascii: "t -> p",
                  latex: "t \\rightarrow p",
                  userInput: "t -> p",
                },
                justification: {
                  refs: ["211f7763-a91d-4b38-827f-10fbb4498fba"],
                  rule: "implies_intro",
                },
                stepType: "line",
                uuid: "10f7010e-0ea7-4b0f-8b81-92bc6bfed91d",
              },
              {
                formula: {
                  ascii: "(s -> q) or (t -> p)",
                  latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
                  userInput: "(s -> q) or (t -> p)",
                },
                justification: {
                  refs: ["10f7010e-0ea7-4b0f-8b81-92bc6bfed91d"],
                  rule: "or_intro_2",
                },
                stepType: "line",
                uuid: "f375fbc3-eda6-45f2-b1fa-c8d1dd7d00cb",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "8336d04f-1cd5-45e3-b0ef-9433195a92ed",
          },
          {
            formula: {
              ascii: "(s -> q) or (t -> p)",
              latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
              userInput: "(s -> q) or (t -> p)",
            },
            justification: {
              refs: [
                "c8168e57-7a73-4e95-8cbb-9b9ad5806e0c",
                "ffa62b01-cc2e-47ae-a3b7-b8d8b546679d",
                "8336d04f-1cd5-45e3-b0ef-9433195a92ed",
              ],
              rule: "or_elim",
            },
            stepType: "line",
            uuid: "5a3dc934-38be-4fa9-ab51-b437e1b89072",
          },
        ],
        stepType: "box",
        boxInfo: { freshVar: null },
        uuid: "9457f8b5-7dc2-4e30-9837-9f8497bf941b",
      },
      {
        formula: {
          ascii: "(s -> q) or (t -> p)",
          latex: "(s \\rightarrow q) \\lor (t \\rightarrow p)",
          userInput: "(s -> q) or (t -> p)",
        },
        justification: {
          refs: [
            "fdff2248-8f2c-47d9-82bf-8df1389e65b8",
            "761c4eed-3ac7-4e59-8a95-6fe9d3f21f8c",
            "9457f8b5-7dc2-4e30-9837-9f8497bf941b",
          ],
          rule: "or_elim",
        },
        stepType: "line",
        uuid: "108ad9e1-d366-4100-bc66-51780f00a7ac",
      },
    ],
  },
  {
    id: "7",
    title: "Deeply nested example",
    logicName: "propositionalLogic",
    proof: [
      {
        proof: [
          {
            formula: {
              ascii: "p -> q",
              latex: "p \\rightarrow q",
              userInput: "p -> q",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "cb0e66a5-b602-46ef-8a2d-4e5528d3c879",
          },
          {
            proof: [
              {
                formula: {
                  ascii: "not p -> q",
                  latex: "\\lnot p \\rightarrow q",
                  userInput: "not p -> q",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "2435e17f-cca6-4d5d-9322-4c66db601cbc",
              },
              {
                proof: [
                  {
                    formula: {
                      ascii: "not q",
                      latex: "\\lnot q",
                      userInput: "not q",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "630887fe-374f-46d9-a7cf-5be170d80ba9",
                  },
                  {
                    formula: {
                      ascii: "not not p",
                      latex: "\\lnot \\lnot p",
                      userInput: "not not p",
                    },
                    justification: {
                      refs: [
                        "2435e17f-cca6-4d5d-9322-4c66db601cbc",
                        "630887fe-374f-46d9-a7cf-5be170d80ba9",
                      ],
                      rule: "modus_tollens",
                    },
                    stepType: "line",
                    uuid: "a3be3344-6a75-48b1-86d2-896a336f4420",
                  },
                  {
                    formula: {
                      ascii: "p",
                      latex: "p",
                      userInput: "p",
                    },
                    justification: {
                      refs: ["a3be3344-6a75-48b1-86d2-896a336f4420"],
                      rule: "not_not_elim",
                    },
                    stepType: "line",
                    uuid: "9c988134-c550-4fc1-8bd2-ec814312db54",
                  },
                  {
                    formula: {
                      ascii: "q",
                      latex: "q",
                      userInput: "q",
                    },
                    justification: {
                      refs: [
                        "9c988134-c550-4fc1-8bd2-ec814312db54",
                        "cb0e66a5-b602-46ef-8a2d-4e5528d3c879",
                      ],
                      rule: "implies_elim",
                    },
                    stepType: "line",
                    uuid: "323a994a-c81a-46c6-bfa2-f383042258f8",
                  },
                  {
                    formula: {
                      ascii: "false",
                      latex: "\\bot",
                      userInput: "false",
                    },
                    justification: {
                      refs: [
                        "323a994a-c81a-46c6-bfa2-f383042258f8",
                        "630887fe-374f-46d9-a7cf-5be170d80ba9",
                      ],
                      rule: "not_elim",
                    },
                    stepType: "line",
                    uuid: "0a142918-b4c1-4aae-aa7a-9c4c1c340260",
                  },
                ],
                stepType: "box",
                boxInfo: { freshVar: null },
                uuid: "678269ac-8f46-4cac-a605-fe71034ffdd7",
              },
              {
                formula: {
                  ascii: "q",
                  latex: "q",
                  userInput: "q",
                },
                justification: {
                  refs: ["678269ac-8f46-4cac-a605-fe71034ffdd7"],
                  rule: "proof_by_contradiction",
                },
                stepType: "line",
                uuid: "22d5f824-243b-4939-bbad-49be74a55735",
              },
            ],
            stepType: "box",
            boxInfo: { freshVar: null },
            uuid: "4389ef78-0d1f-4004-b5fe-1a0897cc81f8",
          },
          {
            formula: {
              ascii: "(not p -> q) -> q",
              latex: "(\\lnot p \\rightarrow q) \\rightarrow q",
              userInput: "(not p -> q) -> q",
            },
            justification: {
              refs: ["4389ef78-0d1f-4004-b5fe-1a0897cc81f8"],
              rule: "implies_intro",
            },
            stepType: "line",
            uuid: "1ab33a76-e672-40f3-a685-15105c6dc0d4",
          },
        ],
        stepType: "box",
        boxInfo: { freshVar: null },
        uuid: "33a5fe1a-497d-4feb-954f-5f2cb2c76288",
      },
      {
        formula: {
          ascii: "(p -> q) -> ((not p -> q) -> q)",
          latex:
            "(p \\rightarrow q) \\rightarrow ((\\lnot p \\rightarrow q) \\rightarrow q)",
          userInput: "(p -> q) -> ((not p -> q) -> q)",
        },
        justification: {
          refs: ["33a5fe1a-497d-4feb-954f-5f2cb2c76288"],
          rule: "implies_intro",
        },
        stepType: "line",
        uuid: "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
      },
    ],
  },
  {
    id: "some-id",
    title: "predicate logic proof",
    logicName: "predicateLogic",
    proof: [
      {
        formula: {
          ascii: "exists x P(x)",
          latex: "\\exists x P(x)",
          userInput: "exists x P(x)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "225552eb-45ec-4fb3-9287-1b0402946d20",
      },
      {
        formula: {
          ascii: "forall x (P(x) -> Q(x))",
          latex: "\\forall x (P(x) \\rightarrow Q(x))",
          userInput: "forall x (P(x) -> Q(x))",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "4d31bf2e-31f1-495d-a68d-3b71813c1f9d",
      },
      {
        boxInfo: {
          freshVar: "x_0",
        },
        proof: [
          {
            formula: {
              ascii: "P(x_0)",
              latex: "P(x_0)",
              userInput: "P(x_0)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "bb8bd992-3801-4594-9532-4988cd98bafd",
          },
          {
            formula: {
              ascii: "P(x_0) -> Q(x_0)",
              latex: "P(x_0) \\rightarrow Q(x_0)",
              userInput: "P(x_0) -> Q(x_0)",
            },
            justification: {
              refs: ["4d31bf2e-31f1-495d-a68d-3b71813c1f9d"],
              rule: "forall_elim",
            },
            stepType: "line",
            uuid: "a4443463-684d-44db-aa6d-6d166486dd51",
          },
          {
            formula: {
              ascii: "Q(x_0)",
              latex: "Q(x_0)",
              userInput: "Q(x_0)",
            },
            justification: {
              refs: [
                "bb8bd992-3801-4594-9532-4988cd98bafd",
                "a4443463-684d-44db-aa6d-6d166486dd51",
              ],
              rule: "implies_elim",
            },
            stepType: "line",
            uuid: "eb6b17c5-15fa-41b3-994b-da1e61bf284f",
          },
          {
            formula: {
              ascii: "exists x Q(x)",
              latex: "\\exists x Q(x)",
              userInput: "exists x Q(x)",
            },
            justification: {
              refs: ["eb6b17c5-15fa-41b3-994b-da1e61bf284f"],
              rule: "exists_intro",
            },
            stepType: "line",
            uuid: "2fd37b95-e5c7-4ec5-958f-085470f7a6b5",
          },
        ],
        stepType: "box",
        uuid: "bb9e59d0-8f4d-41ad-b43c-657e58ecbed8",
      },
      {
        formula: {
          ascii: "exists x Q(x)",
          latex: "\\exists x Q(x)",
          userInput: "exists x Q(x)",
        },
        justification: {
          refs: [
            "225552eb-45ec-4fb3-9287-1b0402946d20",
            "bb9e59d0-8f4d-41ad-b43c-657e58ecbed8",
          ],
          rule: "exists_elim",
        },
        stepType: "line",
        uuid: "308557d4-6509-43c2-9424-92fdde3be6e8",
      },
    ],
  },
  {
    id: "a05dabc8-0a46-4501-9941-8fa47c8cf482",
    title: "forall/exists duality 1",
    logicName: "predicateLogic",
    proof: [
      {
        formula: {
          ascii: "not forall x P(x)",
          latex: "\\lnot \\forall x P(x)",
          userInput: "not forall x P(x)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "80fec1a4-001a-4c00-8a52-daa4a3959abe",
      },
      {
        boxInfo: { freshVar: null },
        proof: [
          {
            formula: {
              ascii: "not exists x not P(x)",
              latex: "\\lnot \\exists x \\lnot P(x)",
              userInput: "not exists x not P(x)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "6d7118bf-5af8-4260-a19a-19490a622c50",
          },
          {
            boxInfo: {
              freshVar: "x_0",
            },
            proof: [
              {
                boxInfo: { freshVar: null },
                proof: [
                  {
                    formula: {
                      ascii: "not P(x_0)",
                      latex: "\\lnot P(x_0)",
                      userInput: "not P(x_0)",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "3e2f5262-1fa4-4411-9480-8cd172cca749",
                  },
                  {
                    formula: {
                      ascii: "exists x not P(x)",
                      latex: "\\exists x \\lnot P(x)",
                      userInput: "exists x not P(x)",
                    },
                    justification: {
                      refs: ["3e2f5262-1fa4-4411-9480-8cd172cca749"],
                      rule: "exists_intro",
                    },
                    stepType: "line",
                    uuid: "45ac7e84-4b5b-45e6-ba09-f446d1d2a4dc",
                  },
                  {
                    formula: {
                      ascii: "false",
                      latex: "\\bot",
                      userInput: "false",
                    },
                    justification: {
                      refs: [
                        "45ac7e84-4b5b-45e6-ba09-f446d1d2a4dc",
                        "6d7118bf-5af8-4260-a19a-19490a622c50",
                      ],
                      rule: "not_elim",
                    },
                    stepType: "line",
                    uuid: "f68e7f39-3bcd-4587-a428-63097bec2c3f",
                  },
                ],
                stepType: "box",
                uuid: "128719c5-276a-4f9f-9a7c-c73bd061bac4",
              },
              {
                formula: {
                  ascii: "P(x_0)",
                  latex: "P(x_0)",
                  userInput: "P(x_0)",
                },
                justification: {
                  refs: ["128719c5-276a-4f9f-9a7c-c73bd061bac4"],
                  rule: "proof_by_contradiction",
                },
                stepType: "line",
                uuid: "f2c603ce-926a-42c2-9f43-57b58d8f5095",
              },
            ],
            stepType: "box",
            uuid: "454eca5a-332a-4faa-b22f-fb0af218c645",
          },
          {
            formula: {
              ascii: "forall x P(x)",
              latex: "\\forall x P(x)",
              userInput: "forall x P(x)",
            },
            justification: {
              refs: ["454eca5a-332a-4faa-b22f-fb0af218c645"],
              rule: "forall_intro",
            },
            stepType: "line",
            uuid: "cc86c523-f0fa-4985-8cfd-83ab91ab62a7",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "cc86c523-f0fa-4985-8cfd-83ab91ab62a7",
                "80fec1a4-001a-4c00-8a52-daa4a3959abe",
              ],
              rule: "not_elim",
            },
            stepType: "line",
            uuid: "dbedef33-657e-44bc-bca6-168186934651",
          },
        ],
        stepType: "box",
        uuid: "42238cb8-518f-4d76-b5f9-88cd238be9eb",
      },
      {
        formula: {
          ascii: "exists x not P(x)",
          latex: "\\exists x \\lnot P(x)",
          userInput: "exists x not P(x)",
        },
        justification: {
          refs: ["42238cb8-518f-4d76-b5f9-88cd238be9eb"],
          rule: "proof_by_contradiction",
        },
        stepType: "line",
        uuid: "e57b2938-a65c-40d2-b289-a529865c38c9",
      },
    ],
  },
  {
    id: "5d7fb9e7-db81-452a-a784-71970cacd1a5",
    title: "forall/exists duality 2",
    logicName: "predicateLogic",
    proof: [
      {
        formula: {
          ascii: "exists x not P(x)",
          latex: "\\exists x \\lnot P(x)",
          userInput: "exists x not P(x)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "b3ffe773-e798-404b-927f-1bad4968877b",
      },
      {
        boxInfo: { freshVar: null },
        proof: [
          {
            formula: {
              ascii: "forall x P(x)",
              latex: "\\forall x P(x)",
              userInput: "forall x P(x)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "d7605135-720e-48c3-9b5a-4a3af0bf6e58",
          },
          {
            boxInfo: {
              freshVar: "x_0",
            },
            proof: [
              {
                formula: {
                  ascii: "not P(x_0)",
                  latex: "\\lnot P(x_0)",
                  userInput: "not P(x_0)",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "5985e9f7-b12c-4cee-b1e4-bd628735c64a",
              },
              {
                formula: {
                  ascii: "P(x_0)",
                  latex: "P(x_0)",
                  userInput: "P(x_0)",
                },
                justification: {
                  refs: ["d7605135-720e-48c3-9b5a-4a3af0bf6e58"],
                  rule: "forall_elim",
                },
                stepType: "line",
                uuid: "58641507-2fd0-4e9e-87da-7dfd413f82a2",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "58641507-2fd0-4e9e-87da-7dfd413f82a2",
                    "5985e9f7-b12c-4cee-b1e4-bd628735c64a",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "446673b6-352c-4c5b-9993-5b714817818c",
              },
            ],
            stepType: "box",
            uuid: "a06482a8-21e2-4494-b33e-c5ed48fe93c0",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "b3ffe773-e798-404b-927f-1bad4968877b",
                "a06482a8-21e2-4494-b33e-c5ed48fe93c0",
              ],
              rule: "exists_elim",
            },
            stepType: "line",
            uuid: "adff04ea-54fd-4b0e-931e-52f78a0ea531",
          },
        ],
        stepType: "box",
        uuid: "3cd3c426-b387-4d6c-83c5-af01901bf4d0",
      },
      {
        formula: {
          ascii: "not forall x P(x)",
          latex: "\\lnot \\forall x P(x)",
          userInput: "not forall x P(x)",
        },
        justification: {
          refs: ["3cd3c426-b387-4d6c-83c5-af01901bf4d0"],
          rule: "not_intro",
        },
        stepType: "line",
        uuid: "dc45982a-cac7-4a3a-90db-0adc5a5d18d3",
      },
    ],
  },
  {
    id: "17bbbe22-8d24-4163-be29-4afecd21d333",
    title: "forall/exists duality 3",
    logicName: "predicateLogic",
    proof: [
      {
        formula: {
          ascii: "not exists x P(x)",
          latex: "\\lnot \\exists x P(x)",
          userInput: "not exists x P(x)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "c6f6a376-117f-4a00-8dc7-8e93e8da143b",
      },
      {
        boxInfo: {
          freshVar: "x_0",
        },
        proof: [
          {
            boxInfo: { freshVar: null },
            proof: [
              {
                formula: {
                  ascii: "P(x_0)",
                  latex: "P(x_0)",
                  userInput: "P(x_0)",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "8ee79978-922b-41f7-8959-fbd716434160",
              },
              {
                formula: {
                  ascii: "exists x P(x)",
                  latex: "\\exists x P(x)",
                  userInput: "exists x P(x)",
                },
                justification: {
                  refs: ["8ee79978-922b-41f7-8959-fbd716434160"],
                  rule: "exists_intro",
                },
                stepType: "line",
                uuid: "59f3b315-9987-4cfc-bfca-21e2eedd4026",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "59f3b315-9987-4cfc-bfca-21e2eedd4026",
                    "c6f6a376-117f-4a00-8dc7-8e93e8da143b",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "379d80ee-db97-43ab-88bd-4e0e524f913b",
              },
            ],
            stepType: "box",
            uuid: "79ad972e-8605-4545-9bf4-1f4a5b067878",
          },
          {
            formula: {
              ascii: "not P(x_0)",
              latex: "\\lnot P(x_0)",
              userInput: "not P(x_0)",
            },
            justification: {
              refs: ["79ad972e-8605-4545-9bf4-1f4a5b067878"],
              rule: "not_intro",
            },
            stepType: "line",
            uuid: "b26d5d20-2569-4f78-8e13-d561f1621dcc",
          },
        ],
        stepType: "box",
        uuid: "d9fd8dd6-e1bb-41e7-a04e-06e0f5f7f634",
      },
      {
        formula: {
          ascii: "forall x not P(x)",
          latex: "\\forall x \\lnot P(x)",
          userInput: "forall x not P(x)",
        },
        justification: {
          refs: ["d9fd8dd6-e1bb-41e7-a04e-06e0f5f7f634"],
          rule: "forall_intro",
        },
        stepType: "line",
        uuid: "6497cf01-84a6-4004-b03f-98cff3286306",
      },
    ],
  },
  {
    id: "2d7beef5-1983-466a-935a-bfecf9d217c0",
    title: "forall/exists duality 4",
    logicName: "predicateLogic",
    proof: [
      {
        formula: {
          ascii: "forall x not P(x)",
          latex: "\\forall x \\lnot P(x)",
          userInput: "forall x not P(x)",
        },
        justification: {
          refs: [],
          rule: "premise",
        },
        stepType: "line",
        uuid: "c95c6301-4808-479e-b5e6-48ae30fff572",
      },
      {
        boxInfo: { freshVar: null },
        proof: [
          {
            formula: {
              ascii: "exists x P(x)",
              latex: "\\exists x P(x)",
              userInput: "exists x P(x)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "8fed3078-c2f0-41ee-bea8-aa56241e1cc2",
          },
          {
            boxInfo: {
              freshVar: "x_0",
            },
            proof: [
              {
                formula: {
                  ascii: "P(x_0)",
                  latex: "P(x_0)",
                  userInput: "P(x_0)",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "2065b23d-d1a8-41c0-b0b9-a4f0cc90e304",
              },
              {
                formula: {
                  ascii: "not P(x_0)",
                  latex: "\\lnot P(x_0)",
                  userInput: "not P(x_0)",
                },
                justification: {
                  refs: ["c95c6301-4808-479e-b5e6-48ae30fff572"],
                  rule: "forall_elim",
                },
                stepType: "line",
                uuid: "d0aa5f82-dcfc-4363-8f9b-49ac66789456",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "2065b23d-d1a8-41c0-b0b9-a4f0cc90e304",
                    "d0aa5f82-dcfc-4363-8f9b-49ac66789456",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "a514b742-f338-41f8-a2fb-b4282503ea02",
              },
            ],
            stepType: "box",
            uuid: "b756fd22-2adc-41c2-b044-12b1551657c2",
          },
          {
            formula: {
              ascii: "false",
              latex: "\\bot",
              userInput: "false",
            },
            justification: {
              refs: [
                "8fed3078-c2f0-41ee-bea8-aa56241e1cc2",
                "b756fd22-2adc-41c2-b044-12b1551657c2",
              ],
              rule: "exists_elim",
            },
            stepType: "line",
            uuid: "1c6138df-57a0-4e86-abef-2d24c9efbdaa",
          },
        ],
        stepType: "box",
        uuid: "4dd9d26d-4fea-4189-a3cc-19f0c897b75a",
      },
      {
        formula: {
          ascii: "not exists x P(x)",
          latex: "\\lnot \\exists x P(x)",
          userInput: "not exists x P(x)",
        },
        justification: {
          refs: ["4dd9d26d-4fea-4189-a3cc-19f0c897b75a"],
          rule: "not_intro",
        },
        stepType: "line",
        uuid: "1498fb5d-d567-4828-ad5e-214b93182f70",
      },
    ],
  },
  {
    id: "c6d62cf0-dc60-4817-ae0b-6670872cc003",
    title: "Drinker paradox",
    logicName: "predicateLogic",
    proof: [
      {
        formula: {
          ascii: "forall y D(y) or not forall y D(y)",
          latex: "\\forall y D(y) \\lor \\lnot \\forall y D(y)",
          userInput: "forall y D(y) or not forall y D(y)",
        },
        justification: {
          refs: [],
          rule: "law_of_excluded_middle",
        },
        stepType: "line",
        uuid: "d2a52f76-2bda-4e41-b6d4-2286e74250c5",
      },
      {
        boxInfo: { freshVar: null },
        proof: [
          {
            formula: {
              ascii: "forall y D(y)",
              latex: "\\forall y D(y)",
              userInput: "forall y D(y)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "fcc2095e-6e6e-4c5b-bb93-d96fb8184f10",
          },
          {
            boxInfo: { freshVar: null },
            proof: [
              {
                formula: {
                  ascii: "D(x_0)",
                  latex: "D(x_0)",
                  userInput: "D(x_0)",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "9ae6f0fb-22a7-4914-9b70-debc518dd6a4",
              },
              {
                formula: {
                  ascii: "forall y D(y)",
                  latex: "\\forall y D(y)",
                  userInput: "forall y D(y)",
                },
                justification: {
                  refs: ["fcc2095e-6e6e-4c5b-bb93-d96fb8184f10"],
                  rule: "copy",
                },
                stepType: "line",
                uuid: "82c5025d-7e5a-424f-9a6a-9283f758a91c",
              },
            ],
            stepType: "box",
            uuid: "f79565be-3013-4ab7-8c89-6a2e6fdf27f0",
          },
          {
            formula: {
              ascii: "D(x_0) -> forall y D(y)",
              latex: "D(x_0) \\rightarrow \\forall y D(y)",
              userInput: "D(x_0) -> forall y D(y)",
            },
            justification: {
              refs: ["f79565be-3013-4ab7-8c89-6a2e6fdf27f0"],
              rule: "implies_intro",
            },
            stepType: "line",
            uuid: "10d04be4-b32f-45b3-bc24-12d4c1c9959d",
          },
          {
            formula: {
              ascii: "exists x (D(x) -> forall y D(y))",
              latex: "\\exists x (D(x) \\rightarrow \\forall y D(y))",
              userInput: "exists x (D(x) -> forall y D(y))",
            },
            justification: {
              refs: ["10d04be4-b32f-45b3-bc24-12d4c1c9959d"],
              rule: "exists_intro",
            },
            stepType: "line",
            uuid: "b7bd91d9-b3d0-408a-8b14-96b1e257239e",
          },
        ],
        stepType: "box",
        uuid: "0e322c9b-4aa1-4f11-aa36-fc7c221ef588",
      },
      {
        boxInfo: { freshVar: null },
        proof: [
          {
            formula: {
              ascii: "not forall y D(y)",
              latex: "\\lnot \\forall y D(y)",
              userInput: "not forall y D(y)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "a8cf33c1-d032-41e3-99dd-0f70a186bb03",
          },
          {
            boxInfo: { freshVar: null },
            proof: [
              {
                formula: {
                  ascii: "not exists x not D(x)",
                  latex: "\\lnot \\exists x \\lnot D(x)",
                  userInput: "not exists x not D(x)",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "ce303b39-0441-424c-801d-5b0005580ed5",
              },
              {
                boxInfo: {
                  freshVar: "y_0",
                },
                proof: [
                  {
                    boxInfo: { freshVar: null },
                    proof: [
                      {
                        formula: {
                          ascii: "not D(y_0)",
                          latex: "\\lnot D(y_0)",
                          userInput: "not D(y_0)",
                        },
                        justification: {
                          refs: [],
                          rule: "assumption",
                        },
                        stepType: "line",
                        uuid: "689a8d80-4628-4936-a513-d8b374450ae8",
                      },
                      {
                        formula: {
                          ascii: "exists x not D(x)",
                          latex: "\\exists x \\lnot D(x)",
                          userInput: "exists x not D(x)",
                        },
                        justification: {
                          refs: ["689a8d80-4628-4936-a513-d8b374450ae8"],
                          rule: "exists_intro",
                        },
                        stepType: "line",
                        uuid: "71a27271-bec1-48c2-bf61-06e8e1cb92d4",
                      },
                      {
                        formula: {
                          ascii: "false",
                          latex: "\\bot",
                          userInput: "false",
                        },
                        justification: {
                          refs: [
                            "71a27271-bec1-48c2-bf61-06e8e1cb92d4",
                            "ce303b39-0441-424c-801d-5b0005580ed5",
                          ],
                          rule: "not_elim",
                        },
                        stepType: "line",
                        uuid: "2d6c0783-05ca-42cd-9ecd-9c81c7a36f0c",
                      },
                    ],
                    stepType: "box",
                    uuid: "77a31330-80d0-4dab-9b11-42737be973d7",
                  },
                  {
                    formula: {
                      ascii: "D(y_0)",
                      latex: "D(y_0)",
                      userInput: "D(y_0)",
                    },
                    justification: {
                      refs: ["77a31330-80d0-4dab-9b11-42737be973d7"],
                      rule: "proof_by_contradiction",
                    },
                    stepType: "line",
                    uuid: "d3ea30a4-8d6e-4d45-babb-a4686aea332a",
                  },
                ],
                stepType: "box",
                uuid: "46a588a9-4446-409c-ae4c-37f1223a5b47",
              },
              {
                formula: {
                  ascii: "forall y D(y)",
                  latex: "\\forall y D(y)",
                  userInput: "forall y D(y)",
                },
                justification: {
                  refs: ["46a588a9-4446-409c-ae4c-37f1223a5b47"],
                  rule: "forall_intro",
                },
                stepType: "line",
                uuid: "a6324ad6-2580-4002-9e3a-0159b6297400",
              },
              {
                formula: {
                  ascii: "false",
                  latex: "\\bot",
                  userInput: "false",
                },
                justification: {
                  refs: [
                    "a6324ad6-2580-4002-9e3a-0159b6297400",
                    "a8cf33c1-d032-41e3-99dd-0f70a186bb03",
                  ],
                  rule: "not_elim",
                },
                stepType: "line",
                uuid: "1e5aea47-048a-4b9b-b5ce-0432ad41a0a4",
              },
            ],
            stepType: "box",
            uuid: "2b76e60e-e957-4c34-90d1-42dc1063afe6",
          },
          {
            formula: {
              ascii: "exists x not D(x)",
              latex: "\\exists x \\lnot D(x)",
              userInput: "exists x not D(x)",
            },
            justification: {
              refs: ["2b76e60e-e957-4c34-90d1-42dc1063afe6"],
              rule: "proof_by_contradiction",
            },
            stepType: "line",
            uuid: "8e9b5540-9315-4df7-ac32-584d7cfcb5c3",
          },
          {
            boxInfo: {
              freshVar: "x_0",
            },
            proof: [
              {
                formula: {
                  ascii: "not D(x_0)",
                  latex: "\\lnot D(x_0)",
                  userInput: "not D(x_0)",
                },
                justification: {
                  refs: [],
                  rule: "assumption",
                },
                stepType: "line",
                uuid: "0f05950b-cd3a-4fe6-898a-74c636bdb3cf",
              },
              {
                boxInfo: { freshVar: null },
                proof: [
                  {
                    formula: {
                      ascii: "D(x_0)",
                      latex: "D(x_0)",
                      userInput: "D(x_0)",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "9a1656cb-1129-4190-9dd0-1d89f81ce4e5",
                  },
                  {
                    formula: {
                      ascii: "false",
                      latex: "\\bot",
                      userInput: "false",
                    },
                    justification: {
                      refs: [
                        "9a1656cb-1129-4190-9dd0-1d89f81ce4e5",
                        "0f05950b-cd3a-4fe6-898a-74c636bdb3cf",
                      ],
                      rule: "not_elim",
                    },
                    stepType: "line",
                    uuid: "78ed221c-75a6-41b2-9141-3ab7bae2da00",
                  },
                  {
                    formula: {
                      ascii: "forall y D(y)",
                      latex: "\\forall y D(y)",
                      userInput: "forall y D(y)",
                    },
                    justification: {
                      refs: ["78ed221c-75a6-41b2-9141-3ab7bae2da00"],
                      rule: "bot_elim",
                    },
                    stepType: "line",
                    uuid: "dcecdcc0-838c-4d75-ba25-c79d0f886836",
                  },
                ],
                stepType: "box",
                uuid: "f1f57be9-b7db-4ba6-9022-399f54aaff63",
              },
              {
                formula: {
                  ascii: "D(x_0) -> forall y D(y)",
                  latex: "D(x_0) \\rightarrow \\forall y D(y)",
                  userInput: "D(x_0) -> forall y D(y)",
                },
                justification: {
                  refs: ["f1f57be9-b7db-4ba6-9022-399f54aaff63"],
                  rule: "implies_intro",
                },
                stepType: "line",
                uuid: "fe3f8add-f9a8-44bd-91fd-a7d4f9a7d744",
              },
              {
                formula: {
                  ascii: "exists x (D(x) -> forall y D(y))",
                  latex: "\\exists x (D(x) \\rightarrow \\forall y D(y))",
                  userInput: "exists x (D(x) -> forall y D(y))",
                },
                justification: {
                  refs: ["fe3f8add-f9a8-44bd-91fd-a7d4f9a7d744"],
                  rule: "exists_intro",
                },
                stepType: "line",
                uuid: "415b6d3c-c386-4703-b8b0-10f8b7851dfc",
              },
            ],
            stepType: "box",
            uuid: "2f514f72-e025-45ba-be92-79e94d5933c8",
          },
          {
            formula: {
              ascii: "exists x (D(x) -> forall y D(y))",
              latex: "\\exists x (D(x) \\rightarrow \\forall y D(y))",
              userInput: "exists x (D(x) -> forall y D(y))",
            },
            justification: {
              refs: [
                "8e9b5540-9315-4df7-ac32-584d7cfcb5c3",
                "2f514f72-e025-45ba-be92-79e94d5933c8",
              ],
              rule: "exists_elim",
            },
            stepType: "line",
            uuid: "847fe8dc-bbd5-45a2-8199-d29e8e0ddb08",
          },
        ],
        stepType: "box",
        uuid: "8840ab49-e7dc-451d-a3be-427f4e536c86",
      },
      {
        formula: {
          ascii: "exists x (D(x) -> forall y D(y))",
          latex: "\\exists x (D(x) \\rightarrow \\forall y D(y))",
          userInput: "exists x (D(x) -> forall y D(y))",
        },
        justification: {
          refs: [
            "d2a52f76-2bda-4e41-b6d4-2286e74250c5",
            "0e322c9b-4aa1-4f11-aa36-fc7c221ef588",
            "8840ab49-e7dc-451d-a3be-427f4e536c86",
          ],
          rule: "or_elim",
        },
        stepType: "line",
        uuid: "c089ea54-9833-441b-b7e9-fd4dbf66b75a",
      },
    ],
  },
  {
    id: "arith_1",
    title: "k + 0 = 0 + k",
    logicName: "arithmetic",
    proof: [
      {
        formula: {
          ascii: "0 + 0 = 0 + 0",
          latex: "0 + 0 = 0 + 0",
          userInput: "0 + 0 = 0 + 0",
        },
        justification: {
          refs: [],
          rule: "equality_intro",
        },
        stepType: "line",
        uuid: "10",
      },
      {
        boxInfo: {
          freshVar: "n",
        },
        proof: [
          {
            formula: {
              ascii: "n + 0 = 0 + n",
              latex: "n + 0 = 0 + n",
              userInput: "n + 0 = 0 + n",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "20",
          },
          {
            formula: {
              ascii: "n + 0 = n + 0",
              latex: "n + 0 = n + 0",
              userInput: "n + 0 = n + 0",
            },
            justification: {
              refs: [],
              rule: "equality_intro",
            },
            stepType: "line",
            uuid: "22",
          },
          {
            formula: {
              ascii: "0 + n = n + 0",
              latex: "0 + n = n + 0",
              userInput: "0 + n = n + 0",
            },
            justification: {
              refs: ["20", "22"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "24",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = (0 + n) + 1",
              latex: "0 + (n + 1) = (0 + n) + 1",
              userInput: "0 + (n + 1) = (0 + n) + 1",
            },
            justification: {
              refs: [],
              rule: "peano_2",
            },
            stepType: "line",
            uuid: "25",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = (n + 0) + 1",
              latex: "0 + (n + 1) = (n + 0) + 1",
              userInput: "0 + (n + 1) = (n + 0) + 1",
            },
            justification: {
              refs: ["24", "25"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "30",
          },
          {
            formula: {
              ascii: "n + 0 = n",
              latex: "n + 0 = n",
              userInput: "n + 0 = n",
            },
            justification: {
              refs: [],
              rule: "peano_1",
            },
            stepType: "line",
            uuid: "32",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = n + 1",
              latex: "0 + (n + 1) = n + 1",
              userInput: "0 + (n + 1) = n + 1",
            },
            justification: {
              refs: ["32", "30"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "34",
          },
          {
            formula: {
              ascii: "(n + 1) + 0 = n + 1",
              latex: "(n + 1) + 0 = n + 1",
              userInput: "(n + 1) + 0 = n + 1",
            },
            justification: {
              refs: [],
              rule: "peano_1",
            },
            stepType: "line",
            uuid: "36",
          },
          {
            formula: {
              ascii: "(n + 1) + 0 = (n + 1) + 0",
              latex: "(n + 1) + 0 = (n + 1) + 0",
              userInput: "(n + 1) + 0 = (n + 1) + 0",
            },
            justification: {
              refs: [],
              rule: "equality_intro",
            },
            stepType: "line",
            uuid: "38",
          },
          {
            formula: {
              ascii: "n + 1 = (n + 1) + 0",
              latex: "n + 1 = (n + 1) + 0",
              userInput: "n + 1 = (n + 1) + 0",
            },
            justification: {
              refs: ["36", "38"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "40",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = (n + 1) + 0",
              latex: "0 + (n + 1) = (n + 1) + 0",
              userInput: "0 + (n + 1) = (n + 1) + 0",
            },
            justification: {
              refs: ["40", "34"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "42",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = 0 + (n + 1)",
              latex: "0 + (n + 1) = 0 + (n + 1)",
              userInput: "0 + (n + 1) = 0 + (n + 1)",
            },
            justification: {
              refs: [],
              rule: "equality_intro",
            },
            stepType: "line",
            uuid: "44",
          },
          {
            formula: {
              ascii: "(n + 1) + 0 = 0 + (n + 1)",
              latex: "(n + 1) + 0 = 0 + (n + 1)",
              userInput: "(n + 1) + 0 = 0 + (n + 1)",
            },
            justification: {
              refs: ["42", "44"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "60",
          },
        ],
        stepType: "box",
        uuid: "bi",
      },
      {
        formula: {
          ascii: "forall k (k + 0 = 0 + k)",
          latex: "\\forall k (k + 0 = 0 + k)",
          userInput: "forall k (k + 0 = 0 + k)",
        },
        justification: {
          refs: ["10", "bi"],
          rule: "induction",
        },
        stepType: "line",
        uuid: "100",
      },
    ],
  },
  {
    id: "reflexivity_plus",
    title: "b + a = a + b",
    logicName: "arithmetic",
    proof: [
      {
        formula: {
          ascii: "0 + 0 = 0 + 0",
          latex: "0 + 0 = 0 + 0",
          userInput: "0 + 0 = 0 + 0",
        },
        justification: {
          refs: [],
          rule: "equality_intro",
        },
        stepType: "line",
        uuid: "10",
      },
      {
        boxInfo: {
          freshVar: "n",
        },
        proof: [
          {
            formula: {
              ascii: "n + 0 = 0 + n",
              latex: "n + 0 = 0 + n",
              userInput: "n + 0 = 0 + n",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "20",
          },
          {
            formula: {
              ascii: "n + 0 = n + 0",
              latex: "n + 0 = n + 0",
              userInput: "n + 0 = n + 0",
            },
            justification: {
              refs: [],
              rule: "equality_intro",
            },
            stepType: "line",
            uuid: "22",
          },
          {
            formula: {
              ascii: "0 + n = n + 0",
              latex: "0 + n = n + 0",
              userInput: "0 + n = n + 0",
            },
            justification: {
              refs: ["20", "22"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "24",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = (0 + n) + 1",
              latex: "0 + (n + 1) = (0 + n) + 1",
              userInput: "0 + (n + 1) = (0 + n) + 1",
            },
            justification: {
              refs: [],
              rule: "peano_2",
            },
            stepType: "line",
            uuid: "25",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = (n + 0) + 1",
              latex: "0 + (n + 1) = (n + 0) + 1",
              userInput: "0 + (n + 1) = (n + 0) + 1",
            },
            justification: {
              refs: ["24", "25"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "30",
          },
          {
            formula: {
              ascii: "n + 0 = n",
              latex: "n + 0 = n",
              userInput: "n + 0 = n",
            },
            justification: {
              refs: [],
              rule: "peano_1",
            },
            stepType: "line",
            uuid: "32",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = n + 1",
              latex: "0 + (n + 1) = n + 1",
              userInput: "0 + (n + 1) = n + 1",
            },
            justification: {
              refs: ["32", "30"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "34",
          },
          {
            formula: {
              ascii: "(n + 1) + 0 = n + 1",
              latex: "(n + 1) + 0 = n + 1",
              userInput: "(n + 1) + 0 = n + 1",
            },
            justification: {
              refs: [],
              rule: "peano_1",
            },
            stepType: "line",
            uuid: "36",
          },
          {
            formula: {
              ascii: "(n + 1) + 0 = (n + 1) + 0",
              latex: "(n + 1) + 0 = (n + 1) + 0",
              userInput: "(n + 1) + 0 = (n + 1) + 0",
            },
            justification: {
              refs: [],
              rule: "equality_intro",
            },
            stepType: "line",
            uuid: "38",
          },
          {
            formula: {
              ascii: "n + 1 = (n + 1) + 0",
              latex: "n + 1 = (n + 1) + 0",
              userInput: "n + 1 = (n + 1) + 0",
            },
            justification: {
              refs: ["36", "38"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "40",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = (n + 1) + 0",
              latex: "0 + (n + 1) = (n + 1) + 0",
              userInput: "0 + (n + 1) = (n + 1) + 0",
            },
            justification: {
              refs: ["40", "34"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "42",
          },
          {
            formula: {
              ascii: "0 + (n + 1) = 0 + (n + 1)",
              latex: "0 + (n + 1) = 0 + (n + 1)",
              userInput: "0 + (n + 1) = 0 + (n + 1)",
            },
            justification: {
              refs: [],
              rule: "equality_intro",
            },
            stepType: "line",
            uuid: "44",
          },
          {
            formula: {
              ascii: "(n + 1) + 0 = 0 + (n + 1)",
              latex: "(n + 1) + 0 = 0 + (n + 1)",
              userInput: "(n + 1) + 0 = 0 + (n + 1)",
            },
            justification: {
              refs: ["42", "44"],
              rule: "equality_elim",
            },
            stepType: "line",
            uuid: "60",
          },
        ],
        stepType: "box",
        uuid: "bi",
      },
      {
        formula: {
          ascii: "forall b (b + 0 = 0 + b)",
          latex: "\\forall b (b + 0 = 0 + b)",
          userInput: "forall b (b + 0 = 0 + b)",
        },
        justification: {
          refs: ["10", "bi"],
          rule: "induction",
        },
        stepType: "line",
        uuid: "100",
      },
      {
        boxInfo: {
          freshVar: "n",
        },
        proof: [
          {
            formula: {
              ascii: "forall b (b + n = n + b)",
              latex: "\\forall b (b + n = n + b)",
              userInput: "forall b (b + n = n + b)",
            },
            justification: {
              refs: [],
              rule: "assumption",
            },
            stepType: "line",
            uuid: "75dc32b3-5a08-454c-b425-69774da76fa5",
          },
          {
            boxInfo: {
              freshVar: "m",
            },
            proof: [
              {
                formula: {
                  ascii: "m + n = n + m",
                  latex: "m + n = n + m",
                  userInput: "m + n = n + m",
                },
                justification: {
                  refs: ["75dc32b3-5a08-454c-b425-69774da76fa5"],
                  rule: "forall_elim",
                },
                stepType: "line",
                uuid: "29bad9bd-0067-43ac-8651-11da3116cc82",
              },
              {
                formula: {
                  ascii: "m + (n + 1) = (m + n) + 1",
                  latex: "m + (n + 1) = (m + n) + 1",
                  userInput: "m + (n + 1) = (m + n) + 1",
                },
                justification: {
                  refs: [],
                  rule: "peano_2",
                },
                stepType: "line",
                uuid: "4e27dc24-14e7-440f-b958-5f20f30193ff",
              },
              {
                formula: {
                  ascii: "m + (n + 1) = (n + m) + 1",
                  latex: "m + (n + 1) = (n + m) + 1",
                  userInput: "m + (n + 1) = (n + m) + 1",
                },
                justification: {
                  refs: [
                    "29bad9bd-0067-43ac-8651-11da3116cc82",
                    "4e27dc24-14e7-440f-b958-5f20f30193ff",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "008812d3-a209-47ca-bfc9-26a4382fb811",
              },
              {
                formula: {
                  ascii: "n + (m + 1) = (n + m) + 1",
                  latex: "n + (m + 1) = (n + m) + 1",
                  userInput: "n + (m + 1) = (n + m) + 1",
                },
                justification: {
                  refs: [],
                  rule: "peano_2",
                },
                stepType: "line",
                uuid: "61035b9f-b65a-433f-9ba6-68274d4059f0",
              },
              {
                formula: {
                  ascii: "n + (m + 1) = n + (m + 1)",
                  latex: "n + (m + 1) = n + (m + 1)",
                  userInput: "n + (m + 1) = n + (m + 1)",
                },
                justification: {
                  refs: [],
                  rule: "equality_intro",
                },
                stepType: "line",
                uuid: "32a068e2-69a6-4fdd-8f04-a8191c0f3576",
              },
              {
                formula: {
                  ascii: "(n + m) + 1 = n + (m + 1)",
                  latex: "(n + m) + 1 = n + (m + 1)",
                  userInput: "(n + m) + 1 = n + (m + 1)",
                },
                justification: {
                  refs: [
                    "61035b9f-b65a-433f-9ba6-68274d4059f0",
                    "32a068e2-69a6-4fdd-8f04-a8191c0f3576",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "04489472-5a91-405b-ad3f-99fe53951abf",
              },
              {
                formula: {
                  ascii: "m + (n + 1) = n + (m + 1)",
                  latex: "m + (n + 1) = n + (m + 1)",
                  userInput: "m + (n + 1) = n + (m + 1)",
                },
                justification: {
                  refs: [
                    "04489472-5a91-405b-ad3f-99fe53951abf",
                    "008812d3-a209-47ca-bfc9-26a4382fb811",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "2bca103d-3f0f-47a4-858e-27617e8d2bb1",
              },
              {
                formula: {
                  ascii: "1 + 0 = 0 + 1",
                  latex: "1 + 0 = 0 + 1",
                  userInput: "1 + 0 = 0 + 1",
                },
                justification: {
                  refs: ["100"],
                  rule: "forall_elim",
                },
                stepType: "line",
                uuid: "c9bdf795-5830-45a0-884f-81ec07b29f7b",
              },
              {
                formula: {
                  ascii: "1 + 0 = 1 + 0",
                  latex: "1 + 0 = 1 + 0",
                  userInput: "1 + 0 = 1 + 0",
                },
                justification: {
                  refs: [],
                  rule: "equality_intro",
                },
                stepType: "line",
                uuid: "6ba2076b-5d7b-4bf4-9b2a-4d48dd0f32a7",
              },
              {
                formula: {
                  ascii: "0 + 1 = 1 + 0",
                  latex: "0 + 1 = 1 + 0",
                  userInput: "0 + 1 = 1 + 0",
                },
                justification: {
                  refs: [
                    "c9bdf795-5830-45a0-884f-81ec07b29f7b",
                    "6ba2076b-5d7b-4bf4-9b2a-4d48dd0f32a7",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "d9f45517-f804-4511-883a-6598f2a67a0e",
              },
              {
                formula: {
                  ascii: "n + (0 + 1) = n + (0 + 1)",
                  latex: "n + (0 + 1) = n + (0 + 1)",
                  userInput: "n + (0 + 1) = n + (0 + 1)",
                },
                justification: {
                  refs: [],
                  rule: "equality_intro",
                },
                stepType: "line",
                uuid: "ddeb4b8e-958c-490a-9501-31ef3960d14a",
              },
              {
                formula: {
                  ascii: "n + (0 + 1) = n + (1 + 0)",
                  latex: "n + (0 + 1) = n + (1 + 0)",
                  userInput: "n + (0 + 1) = n + (1 + 0)",
                },
                justification: {
                  refs: [
                    "d9f45517-f804-4511-883a-6598f2a67a0e",
                    "ddeb4b8e-958c-490a-9501-31ef3960d14a",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "bddc1707-2e07-4963-81c4-23237733259b",
              },
              {
                formula: {
                  ascii: "1 + 0 = 1",
                  latex: "1 + 0 = 1",
                  userInput: "1 + 0 = 1",
                },
                justification: {
                  refs: [],
                  rule: "peano_1",
                },
                stepType: "line",
                uuid: "05ddaec8-f7bd-4f56-8eca-c468627b89b0",
              },
              {
                formula: {
                  ascii: "n + (0 + 1) = n + 1",
                  latex: "n + (0 + 1) = n + 1",
                  userInput: "n + (0 + 1) = n + 1",
                },
                justification: {
                  refs: [
                    "05ddaec8-f7bd-4f56-8eca-c468627b89b0",
                    "bddc1707-2e07-4963-81c4-23237733259b",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "eb89206f-9d50-4c8d-a5a4-98d24b877158",
              },
              {
                formula: {
                  ascii: "(n + 1) + 0 = n + 1",
                  latex: "(n + 1) + 0 = n + 1",
                  userInput: "n + 1 + 0 = n + 1",
                },
                justification: {
                  refs: [],
                  rule: "peano_1",
                },
                stepType: "line",
                uuid: "9e9b5b1f-be80-4cbd-9881-f5a5ec3876c3",
              },
              {
                formula: {
                  ascii: "(n + 1) + 0 = (n + 1) + 0",
                  latex: "(n + 1) + 0 = (n + 1) + 0",
                  userInput: "(n + 1) + 0 = n + 1 + 0",
                },
                justification: {
                  refs: [],
                  rule: "equality_intro",
                },
                stepType: "line",
                uuid: "4d7d71cd-8dde-496b-b0d2-b9908ce61302",
              },
              {
                formula: {
                  ascii: "n + 1 = (n + 1) + 0",
                  latex: "n + 1 = (n + 1) + 0",
                  userInput: "n + 1 = n + 1 + 0",
                },
                justification: {
                  refs: [
                    "9e9b5b1f-be80-4cbd-9881-f5a5ec3876c3",
                    "4d7d71cd-8dde-496b-b0d2-b9908ce61302",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "26b7b788-70c6-4618-a537-89bb8d8d161b",
              },
              {
                formula: {
                  ascii: "n + (0 + 1) = (n + 1) + 0",
                  latex: "n + (0 + 1) = (n + 1) + 0",
                  userInput: "n + (0 + 1) = n + 1 + 0",
                },
                justification: {
                  refs: [
                    "26b7b788-70c6-4618-a537-89bb8d8d161b",
                    "eb89206f-9d50-4c8d-a5a4-98d24b877158",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "3115d5d1-1137-4f62-90ea-6792499db754",
              },
              {
                boxInfo: {
                  freshVar: "k",
                },
                proof: [
                  {
                    formula: {
                      ascii: "n + (k + 1) = (n + 1) + k",
                      latex: "n + (k + 1) = (n + 1) + k",
                      userInput: "n + (k + 1) = (n + 1) + k",
                    },
                    justification: {
                      refs: [],
                      rule: "assumption",
                    },
                    stepType: "line",
                    uuid: "e51e6eb1-5fb7-42ae-8efb-5b3cd033dc0a",
                  },
                  {
                    formula: {
                      ascii: "n + ((k + 1) + 1) = (n + (k + 1)) + 1",
                      latex: "n + ((k + 1) + 1) = (n + (k + 1)) + 1",
                      userInput: "n + ((k + 1) + 1) = (n + (k + 1)) + 1",
                    },
                    justification: {
                      refs: [],
                      rule: "peano_2",
                    },
                    stepType: "line",
                    uuid: "8b0a032d-1d75-4f7a-ba8d-f2a343c4d671",
                  },
                  {
                    formula: {
                      ascii: "n + ((k + 1) + 1) = ((n + 1) + k) + 1",
                      latex: "n + ((k + 1) + 1) = ((n + 1) + k) + 1",
                      userInput: "n + ((k + 1) + 1) = ((n + 1) + k) + 1",
                    },
                    justification: {
                      refs: [
                        "e51e6eb1-5fb7-42ae-8efb-5b3cd033dc0a",
                        "8b0a032d-1d75-4f7a-ba8d-f2a343c4d671",
                      ],
                      rule: "equality_elim",
                    },
                    stepType: "line",
                    uuid: "c98f923c-48f5-450d-a1ae-2ba1cd117836",
                  },
                  {
                    formula: {
                      ascii: "(n + 1) + (k + 1) = ((n + 1) + k) + 1",
                      latex: "(n + 1) + (k + 1) = ((n + 1) + k) + 1",
                      userInput: "(n + 1) + (k + 1) = ((n + 1) + k) + 1",
                    },
                    justification: {
                      refs: [],
                      rule: "peano_2",
                    },
                    stepType: "line",
                    uuid: "aa759b94-a16c-4435-82e3-08d55ab9d178",
                  },
                  {
                    formula: {
                      ascii: "(n + 1) + (k + 1) = (n + 1) + (k + 1)",
                      latex: "(n + 1) + (k + 1) = (n + 1) + (k + 1)",
                      userInput: "(n + 1) + (k + 1) = (n + 1) + (k + 1)",
                    },
                    justification: {
                      refs: [],
                      rule: "equality_intro",
                    },
                    stepType: "line",
                    uuid: "a8fe5b26-ba12-4e77-9b61-7dd7bf2d9928",
                  },
                  {
                    formula: {
                      ascii: "((n + 1) + k) + 1 = (n + 1) + (k + 1)",
                      latex: "((n + 1) + k) + 1 = (n + 1) + (k + 1)",
                      userInput: "((n + 1) + k) + 1 = (n + 1) + (k + 1)",
                    },
                    justification: {
                      refs: [
                        "aa759b94-a16c-4435-82e3-08d55ab9d178",
                        "a8fe5b26-ba12-4e77-9b61-7dd7bf2d9928",
                      ],
                      rule: "equality_elim",
                    },
                    stepType: "line",
                    uuid: "04613c52-95df-4aea-878a-286253798a7d",
                  },
                  {
                    formula: {
                      ascii: "n + ((k + 1) + 1) = (n + 1) + (k + 1)",
                      latex: "n + ((k + 1) + 1) = (n + 1) + (k + 1)",
                      userInput: "n + ((k + 1) + 1) = (n + 1) + (k + 1)",
                    },
                    justification: {
                      refs: [
                        "04613c52-95df-4aea-878a-286253798a7d",
                        "c98f923c-48f5-450d-a1ae-2ba1cd117836",
                      ],
                      rule: "equality_elim",
                    },
                    stepType: "line",
                    uuid: "310f6ea0-f5a7-4dae-a124-f7545fa70e94",
                  },
                ],
                stepType: "box",
                uuid: "7d06b0dd-445f-45ed-81fa-36e4e1ae03e9",
              },
              {
                formula: {
                  ascii: "forall i (n + (i + 1) = (n + 1) + i)",
                  latex: "\\forall i (n + (i + 1) = (n + 1) + i)",
                  userInput: "forall i (n + (i + 1) = (n + 1) + i)",
                },
                justification: {
                  refs: [
                    "3115d5d1-1137-4f62-90ea-6792499db754",
                    "7d06b0dd-445f-45ed-81fa-36e4e1ae03e9",
                  ],
                  rule: "induction",
                },
                stepType: "line",
                uuid: "e1ae5123-2131-4e5c-9804-2eae867ab2d5",
              },
              {
                formula: {
                  ascii: "n + (m + 1) = (n + 1) + m",
                  latex: "n + (m + 1) = (n + 1) + m",
                  userInput: "n + (m + 1) = (n + 1) + m",
                },
                justification: {
                  refs: ["e1ae5123-2131-4e5c-9804-2eae867ab2d5"],
                  rule: "forall_elim",
                },
                stepType: "line",
                uuid: "45486f7f-5073-47e4-bf47-15864ce7cc84",
              },
              {
                formula: {
                  ascii: "m + (n + 1) = (n + 1) + m",
                  latex: "m + (n + 1) = (n + 1) + m",
                  userInput: "m + (n + 1) = n + 1 + m",
                },
                justification: {
                  refs: [
                    "45486f7f-5073-47e4-bf47-15864ce7cc84",
                    "2bca103d-3f0f-47a4-858e-27617e8d2bb1",
                  ],
                  rule: "equality_elim",
                },
                stepType: "line",
                uuid: "27e18a9b-91e6-4e4f-93e2-aeeb18a88344",
              },
            ],
            stepType: "box",
            uuid: "de3cf44f-2161-445b-ba16-33d29e8553cb",
          },
          {
            formula: {
              ascii: "forall b (b + (n + 1) = (n + 1) + b)",
              latex: "\\forall b (b + (n + 1) = (n + 1) + b)",
              userInput: "forall b (b + (n + 1) = n + 1 + b)",
            },
            justification: {
              refs: ["de3cf44f-2161-445b-ba16-33d29e8553cb"],
              rule: "forall_intro",
            },
            stepType: "line",
            uuid: "6e1c9628-8d43-4efc-bec5-ec5f930f607e",
          },
        ],
        stepType: "box",
        uuid: "2cae48b6-2a69-4d4a-b3e0-2494998b0012",
      },
      {
        formula: {
          ascii: "forall a forall b (b + a = a + b)",
          latex: "\\forall a \\forall b (b + a = a + b)",
          userInput: "forall a forall b (b + a = a + b)",
        },
        justification: {
          refs: ["100", "2cae48b6-2a69-4d4a-b3e0-2494998b0012"],
          rule: "induction",
        },
        stepType: "line",
        uuid: "e44a8f06-2556-4d87-95a4-b250bf6e8120",
      },
    ],
  },
];

export default examples;
