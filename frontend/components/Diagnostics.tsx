import { Diagnostic, Violation } from "@/types/types";
import { InlineMath } from "react-katex";
import "katex/dist/katex.min.css";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";

type ViolationWithUuid = Violation & { uuid: string }

function MathParagraph({ math, tag }: { math: string, tag?: string }) {
  return <div className="text-center py-2">
    <InlineMath math={math + (tag !== undefined ? `\\quad \\quad (${tag})` : "")}/>
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
}: ViolationWithUuid & { violationType: "missingFormula" }) {
  return <div>Formula is malformed/not specified</div>;
}

function MissingRuleDiagnostic({
}: ViolationWithUuid & { violationType: "missingRule" }) {
  return <div>Missing rule</div>;
}

function MissingDetailInReferenceDiagnostic({
  uuid,
  refIdx,
  expl,
}: ViolationWithUuid & { 
  violationType: "missingDetailInReference",
}) {
  const refLatex = useDiagnostics().getRefLatex(uuid, refIdx)
  return <div>
    {refIdxToString(refIdx)} is incomplete{" "}
    {refLatex && <MathParagraph math={refLatex} tag={refIdx.toString()}/>}
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function WrongNumberOfReferencesDiagnostic({
  exp,
  actual,
}: ViolationWithUuid & { 
  violationType: "wrongNumberOfReferences",
}) {
  return <div>Expected {exp} references but found {actual}</div>;
}

function ReferenceShouldBeBoxDiagnostic({
  uuid,
  ref,
}: ViolationWithUuid & { violationType: "referenceShouldBeBox" }) {
  const refLineNumber = useDiagnostics().getRefString(uuid, ref)
  return <div>{refIdxToString(ref)} {refLineNumber !== null && `(to ${refLineNumber})`} should be a box.</div>;
}

function ReferenceShouldBeLineDiagnostic({
  uuid,
  ref,
}: ViolationWithUuid & { violationType: "referenceShouldBeLine" }) {
  const refLineNumber = useDiagnostics().getRefString(uuid, ref)
  return <div>{refIdxToString(ref)} {refLineNumber !== null && `(to ${refLineNumber})`} should be a line.</div>;
}

function ReferenceDoesntMatchRuleDiagnostic({
  uuid,
  ref,
  expl,
}: ViolationWithUuid & { violationType: "referenceDoesntMatchRule" }) {
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, ref)
  const ruleLatex = useDiagnostics().getRuleAtStepAsLatex(uuid, [ref], false)

  return <div>
    {refIdxToString(ref)}{" "}
      {refLatex !== null && <MathParagraph math={refLatex}/>}
    {"doesn't"} match the rule{" "}
      {ruleLatex !== null && <MathParagraph math={ruleLatex}/>}
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function ReferencesMismatchDiagnostic({
  uuid,
  refs,
  expl,
}: ViolationWithUuid & { violationType: "referencesMismatch" }) {
  const refLatexes = useDiagnostics().getLatexForMultipleRefs(uuid, refs)
  return <div>
   {refs.map((r, i) => refIdxToString(r, i === 0)).join(' and ')}{" "}
    {refLatexes && <MathParagraph math={refLatexes}/>}
    do not match.<br/>

    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function FormulaDoesntMatchReferenceDiagnostic({
  uuid,
  refs,
  expl,
}: ViolationWithUuid & { violationType: "formulaDoesntMatchReference" }) {
  const formulaLatex = useDiagnostics().getStepAsLatex(uuid)
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, refs)

  return <div>
    The formula{" "}
      {formulaLatex && <MathParagraph math={formulaLatex}/>}
    and {refIdxToString(refs, false)}
      {refLatex && <MathParagraph math={refLatex}/>}
    do not match.
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function FormulaDoesntMatchRuleDiagnostic({
  uuid,
  expl,
}: ViolationWithUuid & { violationType: "formulaDoesntMatchRule" }) {
  const formulaLatex = useDiagnostics().getStepAsLatex(uuid)
  const ruleLatex = useDiagnostics().getRuleAtStepAsLatex(uuid, [], true)
  return <div>
    The formula{" "}
      {formulaLatex && <MathParagraph math={formulaLatex}/>}
    {"doesn't"} match the rule
      {ruleLatex && <MathParagraph math={ruleLatex}/>}
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function MiscellaneousViolationDiagnostic({
  expl,
}: ViolationWithUuid & { violationType: "miscellaneousViolation" }) {
  return <div>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function StepNotFoundDiagnostic({
  stepId,
  expl,
}: ViolationWithUuid & { violationType: "stepNotFound" }) {
  return <div>
    Step {stepId} not found.
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function ReferenceIdNotFoundDiagnostic({
  whichRef,
  expl,
}: ViolationWithUuid & { violationType: "referenceIdNotFound" }) {
  return <div>
    {refIdxToString(whichRef)} was not found
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function MalformedReferenceDiagnostic({
  stepId,
  whichRef,
  refId,
  expl,
}: ViolationWithUuid & { violationType: "malformedReference" }) {
  return <div>
    {refIdxToString(whichRef)} is malformed
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function ReferenceToLaterStepDiagnostic({
  uuid,
  refIdx,
  refId,
}: ViolationWithUuid & { violationType: "referenceToLaterStep" }) {
  const refString = useDiagnostics().getRefString(uuid, refIdx)
  return (
    <div>
      Illegal reference to a later step {refString && `(${refString})`}.
    </div>
  );
}

function ScopeViolationDiagnostic({
  uuid,
  stepScope,
  refIdx,
  refScope,
}: ViolationWithUuid & { violationType: "scopeViolation" }) {
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, refIdx)
  const dc = useDiagnostics()
  const stepScopeLatex = stepScope === "root" ? "\\text{(root scope)}" : dc.getStepAsLatexWithTag(stepScope)
  const refScopeLatex = refScope === "root" ? "\\text{(root scope)}" : dc.getStepAsLatexWithTag(refScope)

  return (
    <div>
      Reference is out of scope.<br/>
      {refIdxToString(refIdx)}{" "}
      {refLatex && <MathParagraph math={refLatex}/>}
      has scope{" "}
      {refScopeLatex ? <MathParagraph math={refScopeLatex}/> : "??? "}
      but the step has scope{" "}
      {stepScopeLatex ? <MathParagraph math={stepScopeLatex}/> : "???"}
    </div>
  );
}

function ReferenceToUnclosedBoxDiagnostic({
  uuid,
  refIdx,
}: ViolationWithUuid & { violationType: "referenceToUnclosedBox" }) {
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, refIdx)
  return (
    <div>
      {refIdxToString(refIdx)}{" "}
      {refLatex && <MathParagraph math={refLatex}/>}
      references an unclosed box
    </div>
  );
}

export function DiagnosticMessage({ diagnostic }: { 
  diagnostic: Diagnostic, 
}) {
  const v = { 
    uuid: diagnostic.uuid,
    violationType: diagnostic.violationType,
    ...diagnostic.violation
  } as (ViolationWithUuid | undefined) // TODO: better checking


  if (!v)
    return "Something went wrong when rendering violation"

  switch (v.violationType) {
    case "missingFormula":
      return <MissingFormulaDiagnostic {...v}/>;
    case "missingRule":
      return <MissingRuleDiagnostic {...v} />;
    case "missingDetailInReference":
      return <MissingDetailInReferenceDiagnostic {...v}/>;
    case "wrongNumberOfReferences":
      return <WrongNumberOfReferencesDiagnostic {...v}/>;
    case "referenceShouldBeBox":
      return <ReferenceShouldBeBoxDiagnostic {...v}/>;
    case "referenceShouldBeLine":
      return <ReferenceShouldBeLineDiagnostic {...v}/>;
    case "referenceDoesntMatchRule":
      return <ReferenceDoesntMatchRuleDiagnostic {...v}/>;
    case "referencesMismatch":
      return <ReferencesMismatchDiagnostic {...v}/>;
    case "formulaDoesntMatchReference":
      return <FormulaDoesntMatchReferenceDiagnostic {...v}/>;
    case "formulaDoesntMatchRule":
      return <FormulaDoesntMatchRuleDiagnostic {...v} />;
    case "miscellaneousViolation":
      return <MiscellaneousViolationDiagnostic {...v} />;
    case "stepNotFound":
      return <StepNotFoundDiagnostic {...v} />;
    case "referenceIdNotFound":
      return <ReferenceIdNotFoundDiagnostic {...v} />;
    case "malformedReference":
      return <MalformedReferenceDiagnostic {...v} />;
    case "referenceToLaterStep":
      return <ReferenceToLaterStepDiagnostic {...v} />;
    case "scopeViolation":
      return <ScopeViolationDiagnostic {...v} />;
    case "referenceToUnclosedBox":
      return <ReferenceToUnclosedBoxDiagnostic {...v} />;
    default:
      const _: never = v;
      return null;
  }
}
