import { Diagnostic } from "@/types/types";
import { InlineMath } from "react-katex";
import "katex/dist/katex.min.css";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";
import { refIsBeingHovered } from "@/lib/state-helpers";
import { useInteractionState } from "@/contexts/InteractionStateProvider";

function prettifyLatex(math: string, tag?: string, highlight?: string) {
  let content = math + (tag !== undefined ? `\\quad \\quad (${tag})` : "")
  if (highlight) {
    content = `{\\color{${highlight}}\\underline{${content}}}`
  }
  return content
}

function MathParagraph({ 
  math, 
  tag,
  highlight
}: { math: string, tag?: string, highlight?: string }) {
  return <div className="text-center py-2">
    <InlineMath math={prettifyLatex(math, tag, highlight)}/>
  </div>
}

function ServerMsg({ children } : React.PropsWithChildren<object>) {
  return <div className="bg-gray-300 rounded-md text-center py-3 text-sm">
    <code>{children}</code>
  </div>
}

function refIdxToString(refIdx: number, capital: boolean = true): string {
  switch(refIdx) {
    case 0: return `${capital ? "T" : "t"}he first reference`
    case 1: return `${capital ? "T" : "t"}he second reference`
    case 2: return `${capital ? "T" : "t"}he third reference`
    case 3: return `${capital ? "T" : "t"}he fourth reference`
    case 4: return `${capital ? "T" : "t"}he fifth reference`
    default: return `${capital ? "R" : "r"}eference ${refIdx.toString()}`
  }
}

function MissingFormulaDiagnostic({
}: Diagnostic & { errorType: "MissingFormula" }) {
  return <div>Formula is malformed/not specified</div>;
}

function MissingRuleDiagnostic({
}: Diagnostic & { errorType: "MissingRule" }) {
  return <div>Missing rule</div>;
}

function WrongNumberOfReferencesDiagnostic({
  expected,
  actual,
}: Diagnostic & { 
  errorType: "WrongNumberOfReferences",
}) {
  return <div>Expected {expected} references but found {actual}</div>;
}

function ReferenceShouldBeBoxDiagnostic({
  uuid,
  refIdx,
}: Diagnostic & { errorType: "ReferenceShouldBeBox" }) {
  const refLineNumber = useDiagnostics().getRefString(uuid, refIdx)
  return <div>{refIdxToString(refIdx)} {refLineNumber !== null && `(to ${refLineNumber})`} should be a box.</div>;
}

function ReferenceShouldBeLineDiagnostic({
  uuid,
  refIdx,
}: Diagnostic & { errorType: "ReferenceShouldBeLine" }) {
  const refLineNumber = useDiagnostics().getRefString(uuid, refIdx)
  return <div>{refIdxToString(refIdx)} {refLineNumber !== null && `(to ${refLineNumber})`} should be a line.</div>;
}

function MiscellaneousDiagnostic({
  explanation,
}: Diagnostic & { errorType: "Miscellaneous" }) {
  return <div>
    <ServerMsg>{explanation}</ServerMsg>
  </div>;
}

function MissingRefDiagnostic({
  refIdx: whichRef,
}: Diagnostic & { errorType: "MissingRef" }) {
  return <div>
    {refIdxToString(whichRef)} is missing
  </div>
}

function ReferenceToLaterStepDiagnostic({
  uuid,
  refIdx,
}: Diagnostic & { errorType: "ReferenceToLaterStep" }) {
  const refString = useDiagnostics().getRefString(uuid, refIdx)
  return (
    <div>
      Illegal reference to a later step {refString && `(${refString})`}.
    </div>
  );
}

function ReferenceOutOfScopeDiagnostic({
  uuid,
  refIdx,
}: Diagnostic & { errorType: "ReferenceOutOfScope" }) {
  return (
    <div>
      {refIdxToString(refIdx)}{" "} is out of scope.
    </div>
  )
}

function ReferenceToUnclosedBoxDiagnostic({
  uuid,
  refIdx,
}: Diagnostic & { errorType: "ReferenceToUnclosedBox" }) {
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, refIdx)
  const { interactionState } = useInteractionState()
  const refIsHovered = refIsBeingHovered(uuid, refIdx, interactionState)
  return (
    <div>
      {refIdxToString(refIdx)}{" "}
      {refLatex && <MathParagraph math={refLatex} highlight={refIsHovered ? "blue" : undefined}/>}
      references an unclosed box
    </div>
  );
}

export function ReferenceBoxMissingFreshVarDiagnostic({
  uuid,
  refIdx
}: Diagnostic & { errorType: "ReferenceBoxMissingFreshVar" }) {
  return <div>
    {refIdxToString(refIdx)}{" "} has no fresh variable
  </div>
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
      {d.entries.map(entry => (<tr id={JSON.stringify(entry)}>
        <td>{entry.rulePosition}</td>
        <td><InlineMath math={entry.meta}/></td>
        <td><InlineMath math={entry.actual}/></td>
      </tr>)
      )}
    </table>
  </div>
}

export function ShapeMismatchDiagnostic({
  uuid,
  expected,
  actual
}: Diagnostic & { errorType: "ShapeMismatch" }) {
  return <div>
    Expected: <InlineMath math={expected}/><br/>
    Got: <InlineMath math={actual}/>
  </div>;
}

export function DiagnosticMessage({ diagnostic }: { 
  diagnostic: Diagnostic, 
}) {
  if (!diagnostic)
    return "Something went wrong when rendering violation"

  switch (diagnostic.errorType) {
    case "MissingFormula":
      return <MissingFormulaDiagnostic {...diagnostic}/>;
    case "MissingRule":
      return <MissingRuleDiagnostic {...diagnostic} />;
    case "MissingRef":
      return <MissingRefDiagnostic {...diagnostic}/>;
    case "WrongNumberOfReferences":
      return <WrongNumberOfReferencesDiagnostic {...diagnostic}/>;
    case "ReferenceShouldBeBox":
      return <ReferenceShouldBeBoxDiagnostic {...diagnostic}/>;
    case "ReferenceShouldBeLine":
      return <ReferenceShouldBeLineDiagnostic {...diagnostic}/>;
    case "ReferenceToUnclosedBox":
      return <ReferenceToUnclosedBoxDiagnostic {...diagnostic} />;
    case "ReferenceOutOfScope":
      return <ReferenceOutOfScopeDiagnostic {...diagnostic}/>;
    case "Miscellaneous":
      return <MiscellaneousDiagnostic {...diagnostic}/>;
    case "ReferenceToLaterStep": 
      return <ReferenceToLaterStepDiagnostic {...diagnostic}/>;
    case "ReferenceBoxMissingFreshVar":
      return <ReferenceBoxMissingFreshVarDiagnostic {...diagnostic}/>;
    case "Ambiguous":
      return <AmbiguousDiagnostic {...diagnostic}/>;
    case "ShapeMismatch":
      return <ShapeMismatchDiagnostic {...diagnostic}/>;
    default:
      const _: never = diagnostic;
      return _;
  }
}
