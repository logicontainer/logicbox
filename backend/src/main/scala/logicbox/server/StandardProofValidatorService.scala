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
import spray.json.DefaultJsonProtocol._

import logicbox.proof.ScopedProofChecker.ReferenceToLaterStep
import logicbox.proof.ScopedProofChecker.ScopeViolation
import logicbox.proof.ScopedProofChecker.ReferenceToUnclosedBox
import logicbox.proof.PLRuleParser
import logicbox.proof.RuledBasedProofChecker.RuleViolation
import logicbox.proof.RuledBasedProofChecker.StepNotFound
import logicbox.proof.RuledBasedProofChecker.ReferenceIdNotFound
import logicbox.proof.RuledBasedProofChecker.MalformedReference
import logicbox.proof.OptionRuleChecker.MissingFormula
import logicbox.proof.OptionRuleChecker.MissingRule
import logicbox.proof.OptionRuleChecker.MissingDetailInReference
import logicbox.proof.PLViolation.WrongNumberOfReferences
import logicbox.proof.PLViolation.ReferenceShouldBeBox
import logicbox.proof.PLViolation.ReferenceShouldBeLine
import logicbox.proof.PLViolation.ReferenceDoesntMatchRule
import logicbox.proof.PLViolation.ReferencesMismatch
import logicbox.proof.PLViolation.FormulaDoesntMatchReference
import logicbox.proof.PLViolation.FormulaDoesntMatchRule
import logicbox.proof.PLViolation.MiscellaneousViolation
import logicbox.proof.ScopedProofChecker.Scope
import logicbox.proof.ScopedProofChecker.Root


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

  private def getViolationType(diag: Diag): String = diag match {
    case diag: RuledBasedProofChecker.Diagnostic[Id, OptionRuleChecker.Violation[PLViolation]] => diag match {
      case RuleViolation(stepId, violation: OptionRuleChecker.Violation[PLViolation] @unchecked) => violation match {
        case MissingFormula => "missingFormula"
        case MissingRule => "missingRule"
        case MissingDetailInReference(_, _) => "missingDetailInReference"
        case logicbox.proof.OptionRuleChecker.RuleViolation(violation) => 
          val shortName = violation match {
            case WrongNumberOfReferences(_, _, _) => "wrongNumberOfReferences"
            case ReferenceShouldBeBox(_, _) => "referenceShouldBeBox"
            case ReferenceShouldBeLine(_, _) => "referenceShouldBeLine"
            case ReferenceDoesntMatchRule(_, _) => "referenceDoesntMatchRule"
            case ReferencesMismatch(_, _) => "referencesMismatch"
            case FormulaDoesntMatchReference(_, _) => "formulaDoesntMatchReference"
            case FormulaDoesntMatchRule(_) => "formulaDoesntMatchRule"
            case MiscellaneousViolation(_) => "miscellaneousViolation"
          }
          s"propositionalLogic:$shortName"
      }
      case StepNotFound(_, _) => "stepNotFound"
      case ReferenceIdNotFound(_, _, _, _) => "referenceIdNotFound"
      case MalformedReference(_, _, _, _) => "malformedReference"
    }
    case diag: ScopedProofChecker.Diagnostic[Id] => diag match {
      case ReferenceToLaterStep(_, _, _) => "referenceToLaterStep" 
      case ScopeViolation(_, _, _, _, _) => "scopeViolation"
      case ReferenceToUnclosedBox(_, _, _) => "referenceToUnclosedBox"
    }
  }

  private def getViolation(diag: Diag): JsValue = diag match {
    case diag: RuledBasedProofChecker.Diagnostic[Id, OptionRuleChecker.Violation[PLViolation]] => diag match {
      case RuleViolation(stepId, violation: OptionRuleChecker.Violation[PLViolation] @unchecked) => violation match {
        case MissingFormula | MissingRule => JsObject()
        case v: MissingDetailInReference => jsonFormat2(MissingDetailInReference.apply).write(v)
        case logicbox.proof.OptionRuleChecker.RuleViolation(violation) => violation match {
          case v: WrongNumberOfReferences => jsonFormat3(WrongNumberOfReferences.apply).write(v)
          case v: ReferenceShouldBeBox => jsonFormat2(ReferenceShouldBeBox.apply).write(v)
          case v: ReferenceShouldBeLine => jsonFormat2(ReferenceShouldBeLine.apply).write(v)
          case v: ReferenceDoesntMatchRule => jsonFormat2(ReferenceDoesntMatchRule.apply).write(v)
          case v: ReferencesMismatch => jsonFormat2(ReferencesMismatch.apply).write(v)
          case v: FormulaDoesntMatchReference => jsonFormat2(FormulaDoesntMatchReference.apply).write(v)
          case v: FormulaDoesntMatchRule => jsonFormat1(FormulaDoesntMatchRule.apply).write(v)
          case v: MiscellaneousViolation => jsonFormat1(MiscellaneousViolation.apply).write(v)
        }
      }
      case d: StepNotFound[Id] => jsonFormat2(StepNotFound[Id].apply).write(d)
      case d: ReferenceIdNotFound[Id] => jsonFormat4(ReferenceIdNotFound[Id].apply).write(d)
      case d: MalformedReference[Id] => jsonFormat4(MalformedReference[Id].apply).write(d)
    }
    case diag: ScopedProofChecker.Diagnostic[Id] => diag match {
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
  }


  private def writeDiagnostic(diag: Diag): JsValue = JsObject(
    "uuid" -> JsString(diag.stepId),
    "violationType" -> JsString(getViolationType(diag)),
    "violation" -> getViolation(diag)
  )
  
  private def diagnosticWriter: JsonWriter[Diag] = JsonWriter.func2Writer(writeDiagnostic)
}

import StandardProofValidatorService._
class StandardProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], B, Id, Err, Diag
](proofReader, proofChecker, proofWriter, getEmptyProof, diagnosticWriter)
