"use client";

import { DiagnosticMessage } from "@/components/Diagnostics"
import { ProofProvider, useProof } from "@/contexts/ProofProvider";
import { ServerProvider } from "@/contexts/ServerProvider";
import { Diagnostic, ProofStep, Violation } from "@/types/types"
import { Diagnostics } from "next/dist/build/swc/types";
import React from "react";

const violations: Diagnostic[] = [
  {uuid: "", violationType: "missingFormula", violation: {   } },
  {uuid: "", violationType: "missingRule", violation: {   } },
  {uuid: "", violationType: "missingDetailInReference", violation: {  "expl":"Reference lacks required details","refIdx":1 } },
  {uuid: "", violationType: "wrongNumberOfReferences", violation: {  "actual":1,"exp":2, } },
  {uuid: "", violationType: "referenceShouldBeBox", violation: { "ref":1 } },
  {uuid: "", violationType: "referenceShouldBeLine", violation: { "ref":2 } },
  {uuid: "", violationType: "referenceDoesntMatchRule", violation: {  "expl":"must be a disjunction (or)","ref":0 } },
  {uuid: "", violationType: "referencesMismatch", violation: {  "expl":"last lines of boxes must match","refs":[1,2] } },
  {uuid: "", violationType: "formulaDoesntMatchReference", violation: {  "expl":"must match right-hand side of implication","refs":2 } },
  {uuid: "", violationType: "formulaDoesntMatchRule", violation: {  "expl":"must be a negation" } },
  {uuid: "", violationType: "miscellaneousViolation", violation: {  "expl":"Unknown validation error occurred" } },
  {uuid: "", violationType: "stepNotFound", violation: {  "expl":"The referenced step doesn't exist","stepId":"step5" } },
  {uuid: "", violationType: "referenceIdNotFound", violation: {  "expl":"Reference points to non-existent step","refId":"nonexistent","stepId":"step7","whichRef":1 } },
  {uuid: "", violationType: "malformedReference", violation: {  "expl":"malformed reference","refId":"bad$ref","stepId":"step9","whichRef":0 } },
  {uuid: "", violationType: "referenceToLaterStep", violation: {  "refId":"step4","refIdx":1,"stepId":"step2" } },
  {uuid: "", violationType: "scopeViolation", violation: {  "refId":"box2.step1","refIdx":2,"refScope":"6c132815-cf2a-4a8f-b3b0-034bd0c6e09b","stepId":"box1.step3","stepScope":"root" } },
  {uuid: "", violationType: "referenceToUnclosedBox", violation: {  "boxId":"box3","refIdx":0,"stepId":"step10" } },
]

const proofExample: ProofStep[] = [
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
]

function InnerThing() {
  const uuid = proofExample[proofExample.length - 1].uuid
  const proofContext = useProof()

  React.useEffect(() => {
    setTimeout(() => {
      proofContext.setStringProof(JSON.stringify(proofExample))
    }, 100)
  }, [])

  if (uuid === null)
    return;

  const elms = violations.map(v => 
    <div className="p-4" key={JSON.stringify(v)}>
      <DiagnosticMessage diagnostic={{...v, uuid }}/>
    </div>
  )

  return <div>
    {elms}
  </div>
}

export default function Test() {
  return <InnerThing/>
}
