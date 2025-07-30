import { Diagnostic, RulePosition } from "@/types/types";
import { InlineMath } from "react-katex";
import "katex/dist/katex.min.css";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { HoveringEnum, TransitionEnum, useInteractionState } from "@/contexts/InteractionStateProvider";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "./ui/accordion";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "./ui/table";
import { useHovering } from "@/contexts/HoveringProvider";
import { toInteger } from "lodash";
import { Title } from "@radix-ui/react-dialog";
import { LinesContextProps, useLines } from "@/contexts/LinesProvider";
import { ProofContextProps, useProof } from "@/contexts/ProofProvider";


function refIdxToString(refIdx: number, capital: boolean = true): string {
  switch(refIdx) {
    case 0: return `${capital ? "T" : "t"}he first reference`
    case 1: return `${capital ? "T" : "t"}he second reference`
    case 2: return `${capital ? "T" : "t"}he third reference`
    case 3: return `${capital ? "T" : "t"}he fourth reference`
    case 4: return `${capital ? "T" : "t"}he fifth reference`
    default: return `${capital ? "R" : "r"}eference ${(refIdx + 1).toString()}`
  }
}

function rulePositionToLineNumber(rulePos: RulePosition, stepUuid: string, proofContext: ProofContextProps, linesContext: LinesContextProps): string | null {
  switch (rulePos) {
    case "conclusion":
      return linesContext.getReferenceString(stepUuid)

    case "premise 0": case "premise 1": case "premise 2": case "premise 3": case "premise 4": case "premise 5":
      const step = proofContext.getProofStepDetails(stepUuid)?.proofStep
      if (step?.stepType !== "line") {
        return null
      }

      const idx = toInteger(rulePos[rulePos.length - 1])
      if (idx >= step.justification.refs.length) return null
      return linesContext.getReferenceString(step.justification.refs[idx])
  }
}

function TitleOnlyDiagnostic({
  iconLatex,
  title,
  value,
  onHover,
}: {
  iconLatex: string
  title: string | React.ReactNode
  value: string
  onHover?: () => void
}) {
  return <AccordionItem key={value} value={value} onMouseMove={e => {
    e.stopPropagation()
    onHover?.()
  }}>
    <AccordionTrigger disabled className="py-2" >
      <div className="flex justify-start gap-2" >
        <div className="flex w-10 justify-center items-center"><InlineMath math={iconLatex}/></div>
        {title}
      </div>
    </AccordionTrigger>
  </AccordionItem>
}

function RefTitleOnlyDiagnostic({
  iconLatex,
  title,
  value,
  stepUuid,
  refIdx,
}: {
  iconLatex: string
  title: string
  value: string
  stepUuid: string
  refIdx: number
}) {
  const { handleHover } = useHovering() 
  return <TitleOnlyDiagnostic iconLatex={iconLatex} title={title} value={value} onHover={() => {
    handleHover({ enum: HoveringEnum.HOVERING_REF, stepUuid: stepUuid, refIdx: refIdx })
  }}/>
}

export function DiagnosticsPanel({
  diagnostics
}: { 
  diagnostics: Diagnostic[]
}) {
  
  function rulePositionToSubscript(pos: RulePosition) {
    switch (pos) {
      case "premise 0": return "_1"
      case "premise 1": return "_2"
      case "premise 2": return "_3"
      case "premise 3": return "_4"
      case "premise 4": return "_5"
      case "premise 5": return "_6"
      case "conclusion": return ""
    }
  }

  const proofContext = useProof()
  const linesContext = useLines()

  const { getRuleNameAtStepAsLatex } = useDiagnostics()
  const { handleHover } = useHovering()

  return <Accordion
    type="single"
    defaultValue="diag-0"
  >{diagnostics.map((d, idx) => {
    const value = `diag-${idx}`
    switch (d.errorType) {
      case "MissingFormula": return <TitleOnlyDiagnostic iconLatex="?" title="Missing formula" value={value} key={value}/>
      case "MissingRule": return <TitleOnlyDiagnostic iconLatex="?" title="Missing rule" value={value} key={value}/>

      case "MissingRef": return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`?_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} is missing`} value={value} key={value}/>
      case "ReferenceOutOfScope": return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`\\dashrightarrow_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} is out of scope`} value={value} key={value}/>
      case "ReferenceShouldBeBox": return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`\\square_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} should be a box`} value={value} key={value}/>
      case "ReferenceShouldBeLine": return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`-_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} should be a line`} value={value} key={value}/>

      case "ReferenceToLaterStep": return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`\\dashrightarrow_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} refers to a later step`} value={value} key={value}/>
      case "ReferenceToUnclosedBox": return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`\\boxtimes_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} refers to an unclosed box`} value={value} key={value}/>

      case "ReferenceBoxMissingFreshVar":
        return <RefTitleOnlyDiagnostic stepUuid={d.uuid} refIdx={d.refIdx} iconLatex={`\\square_${(d.refIdx + 1).toString()}`} title={`${refIdxToString(d.refIdx)} has no fresh variable`} value={value} key={value}/>

      case "Miscellaneous": 
        const expl = d.explanation.split('')
        if (expl[0] >= 'a' && expl[0] <= 'z') 
          expl[0] = expl[0].toUpperCase()

        return <TitleOnlyDiagnostic iconLatex={`\\emptyset${rulePositionToSubscript(d.rulePosition)}`} title={expl.join('')} value={value} key={value}/>

      case "PremiseInsideBox":
        return <TitleOnlyDiagnostic iconLatex={"\\times"} title={"A premise must not occur inside a box"} value={value} key={value}/>

      case "InvalidAssumption":
        return <TitleOnlyDiagnostic iconLatex={"\\times"} title={"An assumption must be on the first line of a box"} value={value} key={value}/>

      case "FreshVarEscaped":
        // TODO: highlight the box when hovering
        return <TitleOnlyDiagnostic iconLatex={d.freshVar} title={<>Occurance of <InlineMath math={d.freshVar}/> outside the box in which it is defined</>} value={value} key={value}/>

      case "ShapeMismatch": {
        const icon = getRuleNameAtStepAsLatex(d.uuid) ?? "?"

        return <AccordionItem 
          key={value} value={value}
          onMouseMove={e => {
            e.stopPropagation()
            switch (d.rulePosition) {
              case "conclusion":
                handleHover({ 
                  enum: HoveringEnum.HOVERING_FORMULA,
                  stepUuid: d.uuid
                })
                break;

              case "premise 0": case "premise 1": case "premise 2": case "premise 3": case "premise 4": case "premise 5":
                handleHover({
                  enum: HoveringEnum.HOVERING_REF,
                  stepUuid: d.uuid,
                  refIdx: toInteger(d.rulePosition[d.rulePosition.length - 1])
                })
                break;
            }
          }}
        >
          <AccordionTrigger className="py-2">
            <div className="flex justify-start gap-2">
              <div className="flex w-10 justify-center items-center"><InlineMath math={icon}/></div>
              Shape mismatch
            </div>
          </AccordionTrigger>
          <AccordionContent className="flex-col justify-center items-center">
            <Table>
              <TableBody>
                <TableRow>
                  <TableCell className="w-[120px]">Expected</TableCell>
                  <TableCell className="flex justify-center"><InlineMath math={d.expected}/></TableCell>
                </TableRow>
                <TableRow>
                  <TableCell className="w-[120px]">Actual</TableCell>
                  <TableCell className="flex justify-center"><InlineMath math={d.actual}/></TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </AccordionContent>
        </AccordionItem>
      }

      case "Ambiguous": {
        const icon = getRuleNameAtStepAsLatex(d.uuid) ?? "?"

        return <AccordionItem key={value} value={value}>
          <AccordionTrigger className="py-2">
            <div className="flex justify-start gap-2">
              <div className="flex w-10 justify-center items-center"><InlineMath math={icon}/></div>
              <InlineMath math={d.subject}/> is ambiguous
            </div>
          </AccordionTrigger>
          <AccordionContent>
            <Table>
              <TableBody>
                {d.entries.map((e, idx) => 
                  <TableRow key={idx.toString()} className="flex justify-start" onMouseMove={ev => {
                    ev.stopPropagation()
                    switch (e.rulePosition) {
                      case "conclusion":
                        handleHover({ 
                          enum: HoveringEnum.HOVERING_FORMULA,
                          stepUuid: d.uuid
                        })
                        break;

                      case "premise 0": case "premise 1": case "premise 2": case "premise 3": case "premise 4": case "premise 5":
                        handleHover({
                          enum: HoveringEnum.HOVERING_REF,
                          stepUuid: d.uuid,
                          refIdx: toInteger(e.rulePosition[e.rulePosition.length - 1])
                        })
                        break;
                    }
                  }}>
                    <TableCell className="flex w-[100px] gap-4 items-center">{(() => {
                      switch (e.rulePosition) {
                        case "conclusion": return "Conclusion"
                        case "premise 0":  return "Premise 1"
                        case "premise 1":  return "Premise 2"
                        case "premise 2":  return "Premise 3"
                        case "premise 3":  return "Premise 4"
                        case "premise 4":  return "Premise 5"
                      }
                    })()}
                    </TableCell>
                    <TableCell className="flex justify-center items-center w-[80px]"><InlineMath math={e.meta}/></TableCell>
                    <TableCell className="flex justify-center grow"><InlineMath math={e.actual}/></TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </AccordionContent>
        </AccordionItem>
      }

      case "WrongNumberOfReferences": 
        console.warn("Bug. Got 'wrong number of references'.")
        return null

      default:
        const _: never = d
    }
  })}</Accordion>
}
