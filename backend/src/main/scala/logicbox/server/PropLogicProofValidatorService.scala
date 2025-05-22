package logicbox.server

import logicbox.formula._
import logicbox.rule._
import logicbox.framework._
import logicbox.proof._
import logicbox.server.format._

// 'factory'
object PropLogicProofValidatorService {
  private type F = PropLogicFormula
  private type R = PropLogicRule
  private type B = Unit
  private type Id = String

  import logicbox.server.format.SprayFormatters._

  private def proofChecker: ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] = {
    val scopedChecker = ScopedProofChecker[Id]()

    val boxFirstRuleIsAssumptionChecker = PropLogicBoxAssumptionsProofChecker[R, Id]()

    val optionRuleChecker: RuleChecker[Option[F], Option[R], Option[B]] = 
      OptionRuleChecker(PropLogicRuleChecker[PropLogicFormula]())
    val ruleBasedProofChecker: ProofChecker[Option[F], Option[R], Option[B], Id] = 
      RuleBasedProofChecker(optionRuleChecker)

    new ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] {
      override def check(proof: Proof[IncompleteFormula[F], Option[R], Option[B], Id]): List[Diagnostic[Id]] = {
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
        boxFirstRuleIsAssumptionChecker.check(cleanRulesProofView)
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
}

import PropLogicProofValidatorService._
class PropLogicProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], Option[B]
](
  rawProofConverter = rawProofConverter, 
  proofChecker = proofChecker
)
