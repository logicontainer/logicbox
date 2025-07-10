package logicbox.server

import logicbox.framework._
import logicbox.proof._
import logicbox.rule._
import logicbox.server.format.Stringifiers

def createErrorConverter[F, B, R, O](
  proof: IncompleteProof[F, R, B, String], 
  formulaNavigator: Navigator[F, O], 
  boxInfoNavigator: Navigator[B, O],
  getInfRule: R => Option[InfRule],
  actualExpToLaTeX: O => String
): ErrorConverter = {
  val cleanProof: Proof[F, R, B, String] = OptionProofView(proof, {
    case (_, Proof.Line(IncompleteFormula(_, Some(f)), Some(r), refs)) => 
      Some(ProofLineImpl(f, r, refs))
    case (_, Proof.Box(Some(info), steps)) => 
      Some(ProofBoxImpl(info, steps))
    case _ => None
  })

  ErrorConverterImpl(
    ProofNavigator(
      formulaNavigator,
      boxInfoNavigator
    ),
    InfRuleNavigator(RulePartNavigator()),
    getInfRule,
    actualExpToLaTeX,
    rulePart => rulePart match {
      case t: RulePart.TemplateTerm => Stringifiers.templateTermToLaTeX(t)
      case f: RulePart.TemplateFormula => Stringifiers.templateFormulaToLaTeX(f)
      case _ => "???"
    },
    cleanProof
  )
}
