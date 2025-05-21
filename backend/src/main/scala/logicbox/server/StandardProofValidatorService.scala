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
import logicbox.marshal.SimpleProofJsonWriter
import logicbox.marshal.PrettyPLFormula
import logicbox.marshal.IncompleteFormulaWriter
import logicbox.framework.Justification
import logicbox.proof.ProofImpl
import logicbox.marshal.PropLogicRuleWriter
import logicbox.marshal.JustificationWriter

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

// 'factory'
object StandardProofValidatorService {
  private type F = PropLogicFormula
  private type R = PropLogicRule
  private type B = Unit
  private type Id = String

  import logicbox.server.format.SprayFormatters._

  private def formulaParser(userInput: String): IncompleteFormula[F] = IncompleteFormula(
    userInput, optFormula = try {
      Some(PropLogicParser()(PropLogicLexer()(userInput)))
    } catch { case _ => None }
  )

  private def ruleParser(rule: String): Option[R] = PropLogicRuleParser.parse(rule)

  private def idParser(str: String): Id = str

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

  private def idWriter: JsonWriter[Id] = JsonWriter.func2Writer(JsString(_))
  private def ruleWriter: JsonWriter[Option[R]] = JsonWriter.func2Writer {
    case None => JsNull
    case Some(value) => PropLogicRuleWriter().write(value)
  }

  private def formulaWriter: JsonWriter[IncompleteFormula[F]] = IncompleteFormulaWriter(
    PrettyPLFormula.asLaTeX, PrettyPLFormula.asASCII
  )

  private def justificationWriter: JsonWriter[Justification[Option[R], Id]] = JustificationWriter(ruleWriter, idWriter)

  private def proofWriter: JsonWriter[Proof[IncompleteFormula[F], Option[R], B, Id]] = 
    SimpleProofJsonWriter(idWriter, formulaWriter, justificationWriter)
}

import StandardProofValidatorService._
class StandardProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], B
](???, ???)
