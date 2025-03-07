package logicbox.server

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.framework.ProofValidatorService
import spray.json._
import logicbox.demarshal.ProofJsonReader
import logicbox.framework.JsonReaderWithErr
import logicbox.framework.ModifyProofCommand
import logicbox.framework.ProofChecker
import logicbox.framework.Proof
import logicbox.framework.ModifiableProof

import logicbox.proof.StandardStepStrategy
import logicbox.proof.{ProofImpl, ProofLineImpl, ProofBoxImpl}
import logicbox.framework.AddLine
import logicbox.framework.ModifiableProof.ProofTop

class ProofValidatorServiceImplTest extends AnyFunSpec {
  def ident(str: String) = str

  case class Diag(i: Int)
  case class ReaderErr()

  def getService(commands: List[ModifyProofCommand[String, String, String]] = Nil, diagnostics: List[Diag] = Nil): ProofValidatorService[ReaderErr | ModifiableProof.Error[String]] = { 
    case object Reader extends JsonReader[Either[ReaderErr, List[ModifyProofCommand[String, String, String]]]] {
      override def read(json: JsValue): Either[ReaderErr, List[ModifyProofCommand[String, String, String]]] = json match {
        case JsString("happy reader") => Right(commands)
        case _ => Left(ReaderErr())
      }
    }

    case object Checker extends ProofChecker[String, String, String, String, Diag] {
      override def check(proof: Proof[String, String, String, String]): List[Diag] = diagnostics
    }

    def errToJson(err: Diag): JsValue = err match {
      case Diag(i) => JsString(s"diag_$i")
    }

    // technically depending on proof impl here - oops
    def emptyProof(): ModifiableProof[String, String, String, String] = ProofImpl.empty(StandardStepStrategy(
      ProofLineImpl("", "", Seq()),
      ProofBoxImpl("", Seq())
    ))

    // very much a stub !!!
    def proofWriter(proof: Proof[String, String, String, String]): JsValue = {
      val steps = proof.rootSteps.map(id => (id, proof.getStep(id).getOrElse(???))).map {
        case (id, Proof.Box(_, _))    => JsString(s"box: $id")
        case (id, Proof.Line(_, _, _)) => JsString(s"line: $id")
        case _ => ???
      }
      JsArray(steps.toVector)
    }

    ProofValidatorServiceImpl(Reader, Checker, JsonWriter.func2Writer(proofWriter), emptyProof, JsonWriter.func2Writer(errToJson))
  }

  describe("validateProof") {
    it("should give err when proof is not read correctly") {
      val validator = getService()
      val res = validator.validateProof(JsArray(JsNull)) // trigger Reader to error
      res shouldBe Left(ReaderErr())
    }

    it("should return empty proof when given one") {
      val validator = getService(commands = Nil)
      val proofJson = JsString("happy reader")
      validator.validateProof(proofJson) shouldBe Right(JsObject(
        "proof" -> JsArray(),
        "diagnostics" -> JsArray()
      ))
    }

    it("should return proof with single line and no diagnostics when good") {
      val validator = getService(
        commands = List(AddLine("someid", ProofTop))
      )
      val proofJson = JsString("happy reader")
      validator.validateProof(proofJson) shouldBe Right(JsObject(
        "proof" -> JsArray(JsString("line: someid")),
        "diagnostics" -> JsArray()
      ))
    }

    it("should diagnostics for proof that does not check") {
      val validator = getService(
        commands = Nil, // empty proof
        diagnostics = List(Diag(2))
      )

      validator.validateProof(JsString("happy reader")) shouldBe Right(JsObject(
        "proof" -> JsArray(),
        "diagnostics" -> JsArray(JsString(s"diag_2"))
      ))
    }
  }
}
