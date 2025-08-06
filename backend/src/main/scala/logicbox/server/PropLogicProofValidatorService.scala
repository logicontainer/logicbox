package logicbox.server

import logicbox.formula._
import logicbox.rule._
import logicbox.framework._
import logicbox.proof._
import logicbox.server.format._
import logicbox.framework.RulePart.TemplateTerm
import logicbox.framework.RulePart.TemplateFormula
import logicbox.rule.ReferenceUtil.BoxOrFormula

// 'factory'
object PropLogicProofValidatorService {
  private type F = PropLogicFormula
  private type R = PropLogicRule
  private type B = Unit
  private type Id = String

  private def proofChecker: ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] = {
    val scopedChecker = ScopedProofChecker[Id]()

    val boxFirstRuleIsAssumptionChecker = PropLogicBoxAssumptionsProofChecker[R, Id]()

    val optionRuleChecker: RuleChecker[Option[F], Option[R], Option[B]] = 
      OptionRuleChecker(PropLogicRuleChecker[PropLogicFormula]())
    val ruleBasedProofChecker: ProofChecker[Option[F], Option[R], Option[B], Id] = 
      RuleBasedProofChecker(optionRuleChecker)

    val structuralProofChecker = StructuralProofChecker[R, Id](PropLogicRule.Premise(), PropLogicRule.Assumption())

    new ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] {
      override def check(proof: Proof[IncompleteFormula[F], Option[R], Option[B], Id]): List[(Id, Error)] = {
        val optProofView = ProofView(proof, { 
          case (id, line: Proof.Line[IncompleteFormula[F], Option[R], Id]) => 
            ProofLineImpl(line.formula.optFormula, line.rule, line.refs)

          case (_, box: Proof.Box[Option[B], Id]) => 
            ProofBoxImpl(box.info, box.steps)
        })

        val cleanRulesProofView: Proof[?, R, ?, Id] = OptionProofView(optProofView, {
          case (_, Proof.Line(f, Some(r), refs)) => 
            Some(ProofLineImpl(f, r, refs))
          case (_, Proof.Box(info, steps)) => 
            Some(ProofBoxImpl(info, steps))
          case _ => None
        })

        ruleBasedProofChecker.check(optProofView) ++ scopedChecker.check(proof) ++
        boxFirstRuleIsAssumptionChecker.check(cleanRulesProofView) ++
        structuralProofChecker.check(cleanRulesProofView)
      }
    }
  }

  private def parseFormula(userInput: String): Option[F] = {
    try {
      Some(PropLogicParser()(PropLogicLexer()(userInput)))
    } catch { case _ => None }
  }

  private def ruleParser(rule: String): Option[R] = PropLogicRuleParser.parse(rule)
  private val formulaToAscii = format.Stringifiers.propLogicFormulaAsASCII
  private val formulaToLatex = format.Stringifiers.propLogicFormulaAsLaTeX
  private val ruleToString   = format.Stringifiers.propLogicRuleAsString

  val rawProofConverter = RawProofToIncompleteProofConverter[F, R, Unit](
    parseFormula = parseFormula,
    parseRule = ruleParser,
    parseRawBoxInfo = _ => Some(()),
    formulaToLatex = formulaToLatex,
    formulaToAscii = formulaToAscii,
    ruleToString = ruleToString,
    boxInfoToRaw = _ => RawBoxInfo(None)
  )

  def getInfRule(rule: R): Option[InfRule] = Some(RuleMaps.getPropLogicInfRule(rule))
}

class NoBoxInfoNavigator extends Navigator[Unit, Nothing] {
  override def get(subject: Unit, loc: Location): Option[Nothing] = None
}

import PropLogicProofValidatorService._
class PropLogicProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], Option[B]
](
  rawProofConverter = rawProofConverter, 
  proofChecker = proofChecker,
  createErrorConverter = pf => createErrorConverter(
    pf,
    FormulaNavigator(),
    NoBoxInfoNavigator(),
    getInfRule,
    f => f match {
      case f: Formula[FormulaKind.Prop] => Stringifiers.propLogicFormulaAsLaTeX(f)
    }
  )
)
