package logicbox.server

import logicbox.framework.JsonReaderWithErr
import logicbox.framework.ModifyProofCommand
import logicbox.formula.PropLogicFormula
import logicbox.rule.PropLogicRule
import logicbox.demarshal.ProofJsonReader
import spray.json.JsonReader
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
import logicbox.framework.ModifiableProof
import spray.json.JsValue
import spray.json.JsString
import logicbox.marshal.SimpleProofJsonWriter
import logicbox.marshal.PrettyPLFormula
import logicbox.marshal.IncompleteFormulaWriter
import logicbox.framework.Justification
import logicbox.proof.ProofImpl
import logicbox.proof.StandardStepStrategy
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


// 'factory'
object StandardProofValidatorService {
  private type F = PropLogicFormula
  private type R = PropLogicRule
  private type B = Unit
  private type Id = String
  private type Err = ProofJsonReader.Err

  private def formulaParser(userInput: String): IncompleteFormula[F] = IncompleteFormula(
    userInput, optFormula = try {
      Some(PropLogicParser()(PropLogicLexer()(userInput)))
    } catch { case _ => None }
  )

  private def ruleParser(rule: String): Option[R] = PropLogicRuleParser.parse(rule)

  private def idParser(str: String): Id = str

  private def proofReader: JsonReaderWithErr[List[ModifyProofCommand[IncompleteFormula[F], Option[R], Id]], ProofJsonReader.Err] = 
    ProofJsonReader(formulaParser, ruleParser, idParser)

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

  private def getEmptyProof(): ModifiableProof[IncompleteFormula[F], Option[R], B, Id] = ProofImpl.empty(
    StandardStepStrategy(
      ProofLineImpl(IncompleteFormula("", None), None, Seq()),
      ProofBoxImpl((), Seq())
    )
  )

  private def getViolationType(diag: Diagnostic[Id]): String = diag match {
    case RuleViolationAtStep(stepId, violation: RuleViolation) => violation match {
      case MissingFormula => "missingFormula"
      case MissingRule => "missingRule"
      case MissingDetailInReference(_, _) => "missingDetailInReference"
      case WrongNumberOfReferences(_, _, _) => "propositionalLogic:wrongNumberOfReferences"
      case ReferenceShouldBeBox(_, _) => "propositionalLogic:referenceShouldBeBox"
      case ReferenceShouldBeLine(_, _) => "propositionalLogic:referenceShouldBeLine"
      case ReferenceDoesntMatchRule(_, _) => "propositionalLogic:referenceDoesntMatchRule"
      case ReferencesMismatch(_, _) => "propositionalLogic:referencesMismatch"
      case FormulaDoesntMatchReference(_, _) => "propositionalLogic:formulaDoesntMatchReference"
      case FormulaDoesntMatchRule(_) => "propositionalLogic:formulaDoesntMatchRule"
      case MiscellaneousViolation(_) => "propositionalLogic:miscellaneousViolation"
    }
    case StepNotFound(_, _) => "stepNotFound"
    case ReferenceIdNotFound(_, _, _, _) => "referenceIdNotFound"
    case MalformedReference(_, _, _, _) => "malformedReference"
    case ReferenceToLaterStep(_, _, _) => "referenceToLaterStep" 
    case ScopeViolation(_, _, _, _, _) => "scopeViolation"
    case ReferenceToUnclosedBox(_, _, _) => "referenceToUnclosedBox"
  }

  private def getViolation(diag: Diagnostic[Id]): JsValue = diag match {
    case RuleViolationAtStep(stepId, violation) => violation match {
      case MissingFormula | MissingRule => JsObject()
      case v: MissingDetailInReference => jsonFormat2(MissingDetailInReference.apply).write(v)
      case v: WrongNumberOfReferences => jsonFormat3(WrongNumberOfReferences.apply).write(v)
      case v: ReferenceShouldBeBox => jsonFormat2(ReferenceShouldBeBox.apply).write(v)
      case v: ReferenceShouldBeLine => jsonFormat2(ReferenceShouldBeLine.apply).write(v)
      case v: ReferenceDoesntMatchRule => jsonFormat2(ReferenceDoesntMatchRule.apply).write(v)
      case v: ReferencesMismatch => jsonFormat2(ReferencesMismatch.apply).write(v)
      case v: FormulaDoesntMatchReference => jsonFormat2(FormulaDoesntMatchReference.apply).write(v)
      case v: FormulaDoesntMatchRule => jsonFormat1(FormulaDoesntMatchRule.apply).write(v)
      case v: MiscellaneousViolation => jsonFormat1(MiscellaneousViolation.apply).write(v)
    }
    case d: StepNotFound[Id] => jsonFormat2(StepNotFound[Id].apply).write(d)
    case d: ReferenceIdNotFound[Id] => jsonFormat4(ReferenceIdNotFound[Id].apply).write(d)
    case d: MalformedReference[Id] => jsonFormat4(MalformedReference[Id].apply).write(d)
    case d: ReferenceToLaterStep[Id] => jsonFormat3(ReferenceToLaterStep[Id].apply).write(d)
    case ScopeViolation(stepId, stepScope, refIdx, refId, refScope) => JsObject(
      "stepId" -> JsString(stepId),
      "stepScope" -> JsString(stepScope match { case s: String => s ; case Root => "root" }),
      "refIdx" -> JsNumber(refIdx),
      "refId" -> JsString(refId),
      "refScope" -> JsString(refScope match { case s: String => s ; case Root => "root" })
    )
    case d: ReferenceToUnclosedBox[Id] => jsonFormat3(ReferenceToUnclosedBox[Id].apply).write(d)
  }


  private def writeDiagnostic(diag: Diagnostic[Id]): JsValue = JsObject(
    "uuid" -> JsString(diag.stepId),
    "violationType" -> JsString(getViolationType(diag)),
    "violation" -> getViolation(diag)
  )
  
  private def diagnosticWriter: JsonWriter[Diagnostic[Id]] = JsonWriter.func2Writer(writeDiagnostic)
}

import StandardProofValidatorService._
class StandardProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], B, Id, Err
](proofReader, proofChecker, proofWriter, getEmptyProof, diagnosticWriter)
