package logicbox.server

import logicbox.formula.PropLogicFormula
import logicbox.rule.PropLogicRule
import logicbox.framework.Proof
import logicbox.formula.PropLogicLexer
import logicbox.formula.PropLogicParser

import logicbox.framework.IncompleteFormula
import logicbox.framework.ProofChecker
import logicbox.proof.ScopedProofChecker
import logicbox.framework.RuleChecker
import logicbox.rule.OptionRuleChecker
import logicbox.rule.PropLogicRuleChecker
import logicbox.proof.RuleBasedProofChecker
import logicbox.proof.ProofView
import logicbox.proof.ProofLineImpl
import logicbox.proof.ProofBoxImpl
import spray.json.JsonWriter
import spray.json.JsValue
import spray.json.JsString
import logicbox.framework.Justification
import logicbox.proof.ProofImpl

import spray.json._
import spray.json.DefaultJsonProtocol._

import logicbox.rule.PropLogicRuleParser
import logicbox.rule.OptionRuleChecker._

import logicbox.framework.RuleViolation
import logicbox.framework.RuleViolation._
import logicbox.framework.Diagnostic
import logicbox.framework.Diagnostic._
import logicbox.framework.Root
import logicbox.framework.Scope
import logicbox.rule.PropLogicRuleParser.parse

// 'factory'
object StandardProofValidatorService {
  private type F = PropLogicFormula
  private type R = PropLogicRule
  private type B = Unit
  private type Id = String

  import logicbox.server.format.SprayFormatters._



  private def proofChecker: ProofChecker[IncompleteFormula[F], Option[R], B, Id] = {
    val scopedChecker = ScopedProofChecker[Id]()
    val optionRuleChecker: RuleChecker[Option[F], Option[R], Option[B]] = 
      OptionRuleChecker(PropLogicRuleChecker[PropLogicFormula]())
    val ruleBasedProofChecker: ProofChecker[Option[F], Option[R], Option[B], Id] = 
      RuleBasedProofChecker(optionRuleChecker)

    new ProofChecker[IncompleteFormula[F], Option[R], B, Id] {
      override def check(proof: Proof[IncompleteFormula[F], Option[R], B, Id]): List[Diagnostic[Id]] = {
        val optProofView = ProofView(proof, { 
          case (id, line: Proof.Line[IncompleteFormula[F], Option[R], Id]) => 
            ProofLineImpl(line.formula.optFormula, line.rule, line.refs)

          case (_, box: Proof.Box[B, Id]) => 
            ProofBoxImpl(Some(box.info), box.steps)
        })

        ruleBasedProofChecker.check(optProofView) ++ scopedChecker.check(proof)
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

  val rawProofConverter = RawProofToIncompleteProofConverter(
    parseFormula = parseFormula,
    parseRule = ruleParser,
    formulaToLatex = formulaToLatex,
    formulaToAscii = formulaToAscii,
    ruleToString = ruleToString,
  )
}

import StandardProofValidatorService._
class StandardProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], B
](
  rawProofConverter = rawProofConverter, 
  proofChecker = proofChecker
)
