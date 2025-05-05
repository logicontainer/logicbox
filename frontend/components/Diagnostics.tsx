import { LineProofStep, ProofStep, Diagnostic } from "@/types/types";
import { InlineMath } from "react-katex";
import "katex/dist/katex.min.css";
import { useRuleset } from "@/contexts/RulesetProvider";
import { createHighlightedLatexRule } from "@/lib/rules";
import { useLines } from "@/contexts/LinesProvider";
import { useDiagnostics } from "@/contexts/DiagnosticsProvider";

function MathParagraph({ math, tag }: { math: string, tag?: string }) {
  return <div className="text-center py-2">
    <InlineMath math={math + (tag !== undefined ? `\\quad \\quad (${tag})` : "")}/>
  </div>
}

function ServerMsg({ children } : React.PropsWithChildren<{}>) {
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
}: Diagnostic & { violationType: "missingFormula" }) {
  return <div>Formula is malformed/not specified</div>;
}

function MissingRuleDiagnostic({
}: Diagnostic & { violationType: "missingRule" }) {
  return <div>Missing rule</div>;
}

function MissingDetailInReferenceDiagnostic({
  uuid,
  refIdx,
  expl,
}: Diagnostic & { 
  violationType: "missingDetailInReference",
}) {
  const refLatex = useDiagnostics().getRefLatex(uuid, refIdx)
  return <div>
    {refLatex !== null && <MathParagraph math={refLatex} tag={refIdx.toString()}/>}
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function WrongNumberOfReferencesDiagnostic({
  exp,
  actual,
}: Diagnostic & { 
  violationType: "wrongNumberOfReferences",
}) {
  return <div>Expected {exp} references but found {actual}</div>;
}

function ReferenceShouldBeBoxDiagnostic({
  uuid,
  ref,
}: Diagnostic & { violationType: "referenceShouldBeBox" }) {
  const refLineNumber = useDiagnostics().getRefString(uuid, ref)
  return <div>{refIdxToString(ref)} {refLineNumber !== null && `(to ${refLineNumber})`} should be a box.</div>;
}

function ReferenceShouldBeLineDiagnostic({
  uuid,
  ref,
}: Diagnostic & { violationType: "referenceShouldBeLine" }) {
  const refLineNumber = useDiagnostics().getRefString(uuid, ref)
  return <div>{refIdxToString(ref)} {refLineNumber !== null && `(to ${refLineNumber})`} should be a line.</div>;
}

function ReferenceDoesntMatchRuleDiagnostic({
  uuid,
  ref,
  expl,
}: Diagnostic & { violationType: "referenceDoesntMatchRule" }) {
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, ref)
  const ruleLatex = useDiagnostics().getRuleAtStepAsLatex(uuid, [ref], false)

  return <div>
    {refIdxToString(ref)}{" "}
      {refLatex !== null && <MathParagraph math={refLatex}/>}
    doesn't match the rule{" "}
      {ruleLatex !== null && <MathParagraph math={ruleLatex}/>}
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function ReferencesMismatchDiagnostic({
  uuid,
  refs,
  expl,
}: Diagnostic & { violationType: "referencesMismatch" }) {
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
}: Diagnostic & { violationType: "formulaDoesntMatchReference" }) {
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
}: Diagnostic & { violationType: "formulaDoesntMatchRule" }) {
  const formulaLatex = useDiagnostics().getStepAsLatex(uuid)
  const ruleLatex = useDiagnostics().getRuleAtStepAsLatex(uuid, [], true)
  return <div>
    The formula{" "}
      {formulaLatex && <MathParagraph math={formulaLatex}/>}
    doesn't match the rule
      {ruleLatex && <MathParagraph math={ruleLatex}/>}
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function MiscellaneousViolationDiagnostic({
  expl,
}: Diagnostic & { violationType: "miscellaneousViolation" }) {
  return <div>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function StepNotFoundDiagnostic({
  stepId,
  expl,
}: Diagnostic & { violationType: "stepNotFound" }) {
  return <div>
    Step {stepId} not found.
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>;
}

function ReferenceIdNotFoundDiagnostic({
  whichRef,
  expl,
}: Diagnostic & { violationType: "referenceIdNotFound" }) {
  return <div>
    Reference #{whichRef} not found
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function MalformedReferenceDiagnostic({
  stepId,
  whichRef,
  refId,
  expl,
}: Diagnostic & { violationType: "malformedReference" }) {
  return <div>
    Reference #{whichRef} is malformed:
    <br/>
    <ServerMsg>{expl}</ServerMsg>
  </div>
}

function ReferenceToLaterStepDiagnostic({
  uuid,
  refIdx,
  refId,
}: Diagnostic & { violationType: "referenceToLaterStep" }) {
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
}: Diagnostic & { violationType: "scopeViolation" }) {
  const refLatex = useDiagnostics().getRefLatexWithTag(uuid, refIdx)
  const stepScopeLatex = stepScope === "root" ? "\\text{(root scope)}" : useDiagnostics().getStepAsLatexWithTag(stepScope)
  const refScopeLatex = refScope === "root" ? "\\text{(root scope)}" : useDiagnostics().getStepAsLatexWithTag(refScope)

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
}: Diagnostic & { violationType: "referenceToUnclosedBox" }) {
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
  switch (diagnostic.violationType) {
    case "missingFormula":
      return <MissingFormulaDiagnostic {...diagnostic}/>;
    case "missingRule":
      return <MissingRuleDiagnostic {...diagnostic} />;
    case "missingDetailInReference":
      return <MissingDetailInReferenceDiagnostic {...diagnostic}/>;
    case "wrongNumberOfReferences":
      return <WrongNumberOfReferencesDiagnostic {...diagnostic}/>;
    case "referenceShouldBeBox":
      return <ReferenceShouldBeBoxDiagnostic {...diagnostic}/>;
    case "referenceShouldBeLine":
      return <ReferenceShouldBeLineDiagnostic {...diagnostic}/>;
    case "referenceDoesntMatchRule":
      return <ReferenceDoesntMatchRuleDiagnostic {...diagnostic}/>;
    case "referencesMismatch":
      return <ReferencesMismatchDiagnostic {...diagnostic}/>;
    case "formulaDoesntMatchReference":
      return <FormulaDoesntMatchReferenceDiagnostic {...diagnostic}/>;
    case "formulaDoesntMatchRule":
      return <FormulaDoesntMatchRuleDiagnostic {...diagnostic} />;
    case "miscellaneousViolation":
      return <MiscellaneousViolationDiagnostic {...diagnostic} />;
    case "stepNotFound":
      return <StepNotFoundDiagnostic {...diagnostic} />;
    case "referenceIdNotFound":
      return <ReferenceIdNotFoundDiagnostic {...diagnostic} />;
    case "malformedReference":
      return <MalformedReferenceDiagnostic {...diagnostic} />;
    case "referenceToLaterStep":
      return <ReferenceToLaterStepDiagnostic {...diagnostic} />;
    case "scopeViolation":
      return <ScopeViolationDiagnostic {...diagnostic} />;
    case "referenceToUnclosedBox":
      return <ReferenceToUnclosedBoxDiagnostic {...diagnostic} />;
    default:
      const _: never = diagnostic;
      return null;
  }
}
