import { Diagnostic, RulePosition } from "@/types/types";
import { InlineMath } from "react-katex";
import "katex/dist/katex.min.css";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { refIsBeingHovered } from "@/lib/state-helpers";
import { useInteractionState } from "@/contexts/InteractionStateProvider";
import { JSX } from "react";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "./ui/accordion";

function prettifyLatex(math: string, tag?: string, highlight?: string) {
  let content = math + (tag !== undefined ? `\\quad \\quad (${tag})` : "")
  if (highlight) {
    content = `{\\color{${highlight}}\\underline{${content}}}`
  }
  return content
}

function DiagnosticMsgImpl({
  title,
  body,
  value
}: {
  title: JSX.Element,
  body: JSX.Element,
  value: string,
}) {
  return 
}

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
export function AmbiguousDiagnostic(d: Diagnostic & { errorType: "Ambiguous" }) {
  return <div>
    <InlineMath math={d.subject}/> is ambiguous <br/>
    <table style={{width: "100%"}}>
      <tr>
        <th>Where</th>
        <th>Meta</th>
        <th>Actual</th>
      </tr>
      {d.entries.map(entry => (<tr key={JSON.stringify(entry)}>
        <td>{entry.rulePosition}</td>
        <td><InlineMath math={entry.meta}/></td>
        <td><InlineMath math={entry.actual}/></td>
      </tr>)
      )}
    </table>
  </div>
}

export function TitleOnlyDiagnostic({
  iconLatex,
  title,
  value
}: {
  iconLatex: string
  title: string
  value: string
}) {
  return <AccordionItem key={value} value={value}>
    <AccordionTrigger disabled className="py-2">
      <div className="flex justify-start gap-2">
        <div className="flex w-10 justify-center items-center"><InlineMath math={iconLatex}/></div>
        {title}
      </div>
    </AccordionTrigger>
  </AccordionItem>
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

  const { getRuleNameAtStepAsLatex } = useDiagnostics()

  return <Accordion
    type="single"
    defaultValue="diag-0"
  >{diagnostics.map((d, idx) => {
    const value = `diag-${idx}`
    switch (d.errorType) {
      case "MissingFormula": return <TitleOnlyDiagnostic iconLatex="?" title="Missing formula" value={value} key={value}/>
      case "MissingRule": return <TitleOnlyDiagnostic iconLatex="?" title="Missing rule" value={value} key={value}/>
      case "MissingRef": return <TitleOnlyDiagnostic iconLatex={`?_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} is missing`} value={value} key={value}/>

      case "ReferenceOutOfScope": return <TitleOnlyDiagnostic iconLatex={`\\dashrightarrow_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} is out of scope`} value={value} key={value}/>
      case "ReferenceShouldBeBox": return <TitleOnlyDiagnostic iconLatex={`\\square_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} should be a box`} value={value} key={value}/>
      case "ReferenceShouldBeLine": return <TitleOnlyDiagnostic iconLatex={`-_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} should be a line`} value={value} key={value}/>

      case "ReferenceToLaterStep": return <TitleOnlyDiagnostic iconLatex={`\\dashrightarrow_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} refers to a later step`} value={value} key={value}/>
      case "ReferenceToUnclosedBox": return <TitleOnlyDiagnostic iconLatex={`\\boxtimes_{${(d.refIdx + 1).toString()}}`} title={`${refIdxToString(d.refIdx)} refers to an unclosed box`} value={value} key={value}/>

      case "Miscellaneous": 
        const expl = d.explanation.split('')
        if (expl[0] >= 'a' && expl[0] <= 'z') 
          expl[0] = expl[0].toUpperCase()

        return <TitleOnlyDiagnostic iconLatex={`\\emptyset${rulePositionToSubscript(d.rulePosition)}`} title={expl.join('')} value={value} key={value}/>

      case "ReferenceBoxMissingFreshVar":
        return <TitleOnlyDiagnostic iconLatex={`\\square_${(d.refIdx + 1).toString()}`} title={`${refIdxToString(d.refIdx)} has no fresh variable`} value={value} key={value}/>

      case "ShapeMismatch": {
        const icon = getRuleNameAtStepAsLatex(d.uuid) ?? "?"

        return <AccordionItem key={value} value={value}>
          <AccordionTrigger className="py-2">
            <div className="flex justify-start gap-2">
              <div className="flex w-10 justify-center items-center"><InlineMath math={icon}/></div>
              Shape mismatch
            </div>
          </AccordionTrigger>
          <AccordionContent className="flex-col justify-center items-center">
            <div className="px-16 py-1 flex justify-start">
              <div className="w-24 font-bold">Expected:</div>
              <div><InlineMath math={d.expected}/></div>
            </div>
            <div className="px-16 py-1 flex justify-start">
              <div className="w-24 font-bold">Got:</div>
              <div><InlineMath math={d.actual}/></div>
            </div>
          </AccordionContent>
        </AccordionItem>
      }

      case "Ambiguous": {
        const icon = getRuleNameAtStepAsLatex(d.uuid) ?? "?"

        const shouldShowMetaColumn = d.entries.some(e => e.meta !== d.subject)

        return <AccordionItem key={value} value={value}>
          <AccordionTrigger className="py-2">
            <div className="flex justify-start gap-2">
              <div className="flex w-10 justify-center items-center"><InlineMath math={icon}/></div>
              <InlineMath math={d.subject}/> is ambiguous
            </div>
          </AccordionTrigger>
          <AccordionContent>
            {d.entries.map(e => 
              <div className="px-16 py-1 flex justify-start" key={JSON.stringify(e)}>
                <div className="w-24 font-bold">{e.rulePosition}</div>
                {shouldShowMetaColumn ? <div><InlineMath math={e.meta}/></div> : null}
                <div><InlineMath math={e.actual}/></div>
              </div>
            )}
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
