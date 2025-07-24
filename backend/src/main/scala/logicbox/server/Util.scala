package logicbox.server

import logicbox.framework._
import logicbox.proof._
import logicbox.rule._
import logicbox.server.format.Stringifiers

class OptNavigator[I, O](
  inner: Navigator[I, O]
) extends Navigator[Option[I], O] {
  override def get(subject: Option[I], loc: Location): Option[O] = 
    subject.flatMap(inner.get(_, loc))
}

def createErrorConverter[F, B, R, O](
  proof: IncompleteProof[F, R, B, String], 
  formulaNavigator: Navigator[F, O], 
  boxInfoNavigator: Navigator[B, O],
  getInfRule: R => Option[InfRule],
  actualExpToLaTeX: O => String
): ErrorConverter = {

  def optGetInfRule(optRule: Option[R]): Option[InfRule] = optRule.flatMap(getInfRule(_))
  val optProofView = ProofView(proof, { 
    case (id, line: Proof.Line[IncompleteFormula[F], Option[R], String]) => 
      ProofLineImpl(line.formula.optFormula, line.rule, line.refs)

    case (_, box: Proof.Box[Option[B], String]) => 
      ProofBoxImpl(box.info, box.steps)
  })

  ErrorConverterImpl(
    ProofNavigator(
      OptNavigator(formulaNavigator),
      OptNavigator(boxInfoNavigator)
    ),
    InfRuleNavigator(RulePartNavigator()),
    optGetInfRule,
    actualExpToLaTeX,
    rulePart => rulePart match {
      case t: RulePart.TemplateTerm => Stringifiers.templateTermToLaTeX(t)
      case f: RulePart.TemplateFormula => Stringifiers.templateFormulaToLaTeX(f)
      case _ => "???"
    },
    optProofView
  )
}
