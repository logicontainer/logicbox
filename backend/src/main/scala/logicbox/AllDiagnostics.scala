package logicbox

import logicbox.proof.OptionRuleChecker._
import logicbox.proof.PLViolation._
import logicbox.proof.ScopedProofChecker._
import logicbox.proof.RuledBasedProofChecker._
import logicbox.server.StandardProofValidatorService
import zio.metrics.jvm.Standard
import logicbox.proof.RuledBasedProofChecker
import logicbox.proof.OptionRuleChecker
import logicbox.proof.PLViolation
import logicbox.proof.ScopedProofChecker

object AllDiagnostics {
  def main(args: Array[String]) = {
    val ds: List[
      RuledBasedProofChecker.Diagnostic[String, OptionRuleChecker.Violation[PLViolation]]
      | ScopedProofChecker.Diagnostic[String]
    ] = List(
      MissingFormula, 
      MissingRule,
      MissingDetailInReference(3, "Reference #3 lacks required details"),
    ).map(v => RuledBasedProofChecker.RuleViolation("some_id", v)) ++ List(
      WrongNumberOfReferences(exp = 2, actual = 1, expl = "Expected 2 references but found 1"),
      ReferenceShouldBeBox(ref = 1, expl = "Reference should point to a box, not a line"),
      ReferenceShouldBeLine(ref = 2, expl = "Reference should point to a line, not a box"),
      ReferenceDoesntMatchRule(ref = 0, expl = "Reference doesn't satisfy rule requirements"),
      ReferencesMismatch(refs = List(1, 3), expl = "References 1 and 3 are incompatible"),
      FormulaDoesntMatchReference(refs = 2, expl = "Formula doesn't match reference #2"),
      FormulaDoesntMatchRule(expl = "Formula doesn't satisfy the rule's constraints"),
      MiscellaneousViolation(expl = "Unknown validation error occurred"),
    ).map(v => RuledBasedProofChecker.RuleViolation("some_id", OptionRuleChecker.RuleViolation(v))) ++ List(
      StepNotFound(stepId = "step5", expl = "The referenced step doesn't exist"),
      ReferenceIdNotFound(
        stepId = "step7", 
        whichRef = 1, 
        refId = "nonexistent", 
        expl = "Reference points to non-existent step"
      ),
      MalformedReference(
        stepId = "step9",
        whichRef = 0,
        refId = "bad$ref",
        expl = "Reference ID contains invalid characters"
      ),
    ) ++ List(
      ReferenceToLaterStep(
        stepId = "step2",
        refIdx = 1,
        refId = "step4"
      ),
      ScopeViolation(
        stepId = "box1.step3",
        stepScope = "box1",
        refIdx = 2,
        refId = "box2.step1",
        refScope = "box2"
      ),
      ReferenceToUnclosedBox(
        stepId = "step10",
        refIdx = 0,
        boxId = "box3"
      )
    )

    ds.map(d => StandardProofValidatorService.getViolation(d)).foreach(println)
  }
}
