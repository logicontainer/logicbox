package logicbox.server

import logicbox.proof.PLBoxInfo
import logicbox.framework.JsonReaderWithErr
import logicbox.framework.ModifyProofCommand
import logicbox.formula.PLFormula
import logicbox.proof.PLRule
import logicbox.demarshal.ProofJsonReader
import spray.json.JsonReader
import logicbox.framework.Proof
import logicbox.formula.PLLexer
import logicbox.formula.PLParser

import logicbox.framework.IncompleteFormula
import logicbox.framework.ProofChecker
import logicbox.proof.ScopedProofChecker
import logicbox.framework.RuleChecker
import logicbox.proof.OptionRuleChecker
import logicbox.DelegatingRuleChecker
import logicbox.proof.PLViolation
import logicbox.proof.RuledBasedProofChecker
import logicbox.proof.ProofView
import logicbox.proof.ProofLineImpl
import logicbox.proof.ProofBoxImpl
import spray.json.JsonWriter
import logicbox.framework.ModifiableProof
import spray.json.JsValue
import spray.json.JsString
import logicbox.marshal.SimpleProofJsonWriter
import logicbox.marshal.PrettyPLFormula
import logicbox.marshal.IncompleteFormulaWriter
import logicbox.marshal.Justification
import logicbox.proof.ProofImpl
import logicbox.proof.StandardStepStrategy
import logicbox.marshal.PLRuleWriter
import logicbox.marshal.JustificationWriter
import spray.json._
import logicbox.proof.ScopedProofChecker.ReferenceToLaterStep
import logicbox.proof.ScopedProofChecker.ScopeViolation
import logicbox.proof.ScopedProofChecker.ReferenceToUnclosedBox
import logicbox.proof.PLRuleParser


// 'factory'
object StandardProofValidatorService {
  private type F = PLFormula
  private type R = PLRule
  private type B = PLBoxInfo
  private type Id = String
  private type Err = ProofJsonReader.Err
  private type Diag = RuledBasedProofChecker.Diagnostic[Id, OptionRuleChecker.Violation[PLViolation]] | ScopedProofChecker.Diagnostic[Id]

  private def formulaParser(userInput: String): IncompleteFormula[F] = IncompleteFormula(
    userInput, optFormula = try {
      Some(PLParser()(PLLexer()(userInput)))
    } catch { case _ => None }
  )

  private def ruleParser(rule: String): Option[R] = PLRuleParser.parse(rule)

  private def idParser(str: String): Id = str

  private def proofReader: JsonReaderWithErr[List[ModifyProofCommand[IncompleteFormula[F], Option[R], Id]], ProofJsonReader.Err] = 
    ProofJsonReader(formulaParser, ruleParser, idParser)

  private def proofChecker: ProofChecker[IncompleteFormula[F], Option[R], B, Id, Diag] = {
    val scopedChecker = ScopedProofChecker[Id]()
    val optionRuleChecker: RuleChecker[Option[F], Option[R], Option[B], OptionRuleChecker.Violation[PLViolation]] = 
      OptionRuleChecker(DelegatingRuleChecker[F, R, B, PLViolation]())
    val ruleBasedProofChecker: ProofChecker[Option[F], Option[R], Option[B], Id, RuledBasedProofChecker.Diagnostic[Id, OptionRuleChecker.Violation[PLViolation]]] = 
      RuledBasedProofChecker(optionRuleChecker)

    new ProofChecker[IncompleteFormula[F], Option[R], B, Id, Diag] {
      override def check(proof: Proof[IncompleteFormula[F], Option[R], B, Id]): List[Diag] = {
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
    case Some(value) => PLRuleWriter().write(value)
  }

  private def formulaWriter: JsonWriter[IncompleteFormula[F]] = IncompleteFormulaWriter(
    PrettyPLFormula.asLaTeX, PrettyPLFormula.asASCII
  )

  private def justificationWriter: JsonWriter[Justification[Option[R], Id]] = JustificationWriter(ruleWriter, idWriter)

  private def proofWriter: JsonWriter[Proof[IncompleteFormula[F], Option[R], B, Id]] = 
    SimpleProofJsonWriter(idWriter, formulaWriter, justificationWriter)

  private def getEmptyProof(): ModifiableProof[IncompleteFormula[F], Option[R], B, Id] = ProofImpl.empty(
    StandardStepStrategy(
      ProofLineImpl(IncompleteFormula("", None), None, Seq()),
      ProofBoxImpl((), Seq())
    )
  )

  private def writeDiagnostic(diag: Diag): JsValue = JsObject(
    "uuid" -> JsString(diag.stepId),
    "violationType" -> JsString(diag.getClass.getName),
    "violation" -> JsString(diag.toString)
  )
  
  private def diagnosticWriter: JsonWriter[Diag] = JsonWriter.func2Writer(writeDiagnostic)
}

import StandardProofValidatorService._
class StandardProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], B, Id, Err, Diag
](proofReader, proofChecker, proofWriter, getEmptyProof, diagnosticWriter)
