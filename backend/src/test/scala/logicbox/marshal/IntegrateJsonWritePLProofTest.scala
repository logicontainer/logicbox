package logicbox.marshal

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.framework._
import logicbox.proof._
import logicbox.formula._
import spray.json._
import logicbox.framework.ModifiableProof.ProofTop
import logicbox.formula.PLFormula.Contradiction


class IntegrateJsonWritePLProofTest extends AnyFunSpec {
  private type F = IncompleteFormula[PLFormula]
  private type R = Option[PLRule]
  private type B = PLBoxInfo
  private type Id = String

  val stepStrategy: ProofStepStrategy[F, R, B, Id] = StandardStepStrategy(
    ProofLineImpl(IncompleteFormula("", None), None, Seq()),
    ProofBoxImpl((), Seq())
  )

  val idWriter = JsonWriter.func2Writer { (id: Id) => JsString(id) }

  val ruleWriter: JsonWriter[R] = {
    val inner = PLRuleWriter()
    JsonWriter.func2Writer {
      case Some(value) => inner.write(value)
      case None => JsNull
    }
  }

  describe("integration between json writers, plformula/plrule and proof impl") {
    import PLFormula._

    val writer: JsonWriter[Proof[F, R, ?, Id]] = SimpleProofJsonWriter[F, R, Id](
      idWriter, 
      IncompleteFormulaWriter[PLFormula](PrettyPLFormula.asLaTeX, PrettyPLFormula.asASCII), 
      JustificationWriter(ruleWriter, idWriter)
    )

    it("should correctly write single line proof") {
      var proof: ModifiableProof[F, R, B, Id] = ProofImpl.empty(stepStrategy)

      proof = proof.addLine("l1", ProofTop).getOrElse(???)

      writer.write(proof) shouldBe JsArray(JsObject(
        "stepType" -> JsString("line"),
        "uuid" -> JsString("l1"),
        "formula" -> JsObject(
          "userInput" -> JsString(""),
          "ascii" -> JsNull,
          "latex" -> JsNull
        ),
        "justification" -> JsObject(
          "rule" -> JsNull, 
          "refs" -> JsArray()
        )
      ))

      proof = proof.updateFormula("l1", 
        IncompleteFormula("USER INPUT", Some(And(Atom('p'), Or(Atom('q'), Contradiction()))))
      ).getOrElse(???)

      proof = proof.updateRule("l1", Some(PLRule.AndIntro())).getOrElse(???)
    }

    it("should correctly json write small proof") {
      // 1:             prem.
      // 2: q -> s      NONE
      // ---- box -----------
      // 3:             ass.
      // 4:  q     NONE 3, 1
      // 5:  s      ->e 4, 3
      // --------------------
      // 6: p implies s   NONE box
      
      import ModifiableProof._
      var proof: ModifiableProof[F, R, B, Id] = ProofImpl.empty(stepStrategy)
      def line(id: Id, pos: ModifiableProof.Pos[Id], f: String, rule: Option[PLRule], refs: Seq[Id]): Unit = {
        proof = proof.addLine(id, pos).getOrElse(???)
        val optF = try {
          Some(PLParser()(PLLexer()(f)))
        } catch {
          case _ => None
        }
        proof = proof.updateFormula(id, IncompleteFormula(f, optF)).getOrElse(???)
        proof = proof.updateRule(id, rule).getOrElse(???)
        proof = proof.updateReferences(id, refs).getOrElse(???)
      }

      line("1", ProofTop, "", Some(PLRule.Premise()), Seq())
      line("2", AtLine("1", Direction.Below), "q -> s", None, Seq())

      proof = proof.addBox("box", AtLine("2", Direction.Below)).getOrElse(???)
      line("3", BoxTop("box"), "", Some(PLRule.Assumption()), Seq())
      line("4", AtLine("3", Direction.Below), "q", None, Seq("3", "1"))
      line("5", AtLine("4", Direction.Below), "s", Some(PLRule.ImplicationElim()), Seq("4", "3"))
      line("6", AtLine("box", Direction.Below), "p implies  s", Some(PLRule.ImplicationIntro()), Seq("box"))

      val result = writer.write(proof)
      val exp = JsArray(
        JsObject(
          "stepType" -> JsString("line"),
          "uuid" -> JsString("1"),
          "formula" -> JsObject(
            "userInput" -> JsString(""),
            "ascii" -> JsNull,
            "latex" -> JsNull
          ),
          "justification" -> JsObject(
            "rule" -> JsString("premise"),
            "refs" -> JsArray()
          )
        ), 
        JsObject(
          "stepType" -> JsString("line"),
          "uuid" -> JsString("2"),
          "formula" -> JsObject(
            "userInput" -> JsString("q -> s"),
            "ascii" -> JsString("q -> s"),
            "latex" -> JsString("q \\rightarrow s")
          ),
          "justification" -> JsObject(
            "rule" -> JsNull, 
            "refs" -> JsArray()
          )
        ),
        JsObject(
          "stepType" -> JsString("box"),
          "uuid" -> JsString("box"),
          "proof" -> JsArray(
            JsObject(
              "stepType" -> JsString("line"),
              "uuid" -> JsString("3"),
              "formula" -> JsObject(
                "userInput" -> JsString(""),
                "ascii" -> JsNull,
                "latex" -> JsNull
              ),
              "justification" -> JsObject(
                "rule" -> JsString("assumption"), 
                "refs" -> JsArray()
              )
            ),
            JsObject(
              "stepType" -> JsString("line"),
              "uuid" -> JsString("4"),
              "formula" -> JsObject(
                "userInput" -> JsString("q"),
                "ascii" -> JsString("q"),
                "latex" -> JsString("q")
              ),
              "justification" -> JsObject(
                "rule" -> JsNull,
                "refs" -> JsArray(JsString("3"), JsString("1"))
              )
            ),
            JsObject(
              "stepType" -> JsString("line"),
              "uuid" -> JsString("5"),
              "formula" -> JsObject(
                "userInput" -> JsString("s"),
                "ascii" -> JsString("s"),
                "latex" -> JsString("s")
              ),
              "justification" -> JsObject(
                "rule" -> JsString("implies_elim"),
                "refs" -> JsArray(JsString("4"), JsString("3"))
              )
            ),
          )
        ),
        JsObject(
          "stepType" -> JsString("line"),
          "uuid" -> JsString("6"),
          "formula" -> JsObject(
            "userInput" -> JsString("p implies  s"),
            "ascii" -> JsString("p -> s"),
            "latex" -> JsString("p \\rightarrow s")
          ),
          "justification" -> JsObject(
            "rule" -> JsString("implies_intro"),
            "refs" -> JsArray(JsString("box"))
          )
        ),
      )

      result shouldBe exp
    }
  }
}
