package logicbox.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.marshal.IncompleteFormula
import logicbox.formula._
import logicbox.framework.ModifiableProof
import logicbox.framework.ProofChecker
import logicbox.framework.Proof
import logicbox.DelegatingRuleChecker
import logicbox.framework.RuleChecker

class IntegrateVerifyPLProofTest extends AnyFunSpec {
  private type F = IncompleteFormula[PLFormula]
  private type R = Option[PLRule]
  private type B = PLBoxInfo
  private type Id = String
  
  val stepStrategy: ProofStepStrategy[F, R, B, Id] = StandardStepStrategy(
    ProofLineImpl(IncompleteFormula("", None), None, Seq()),
    ProofBoxImpl((), Seq())
  )

  describe("integration between proof impl with incomplete formulas and verifiction") {
    it("should work") {
      import ModifiableProof._
      var proof: ModifiableProof[F, R, B, Id] = ProofImpl.empty(stepStrategy)

      // 1: p -> q      prem.       // fine
      // 2: q -> s      NONE        // no rule
      // ---- box -----------
      // 3:  p          ass.        // fine
      // 4:  q      ->e 1, 3        // wrong args
      // 5:  s      ->e 4, 2        // good
      // --------------------
      // 6: p and p       ∧i 3 3    // scope violation
      // 7: p implies r   ->i box   // wrong conclusion
      
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

      val bef = System.currentTimeMillis()
      line("1", ProofTop, "p -> q", Some(PLRule.Premise()), Seq())
      line("2", AtLine("1", Direction.Below), "q -> s", None, Seq())

      proof = proof.addBox("box", AtLine("2", Direction.Below)).getOrElse(???)
      line("3", BoxTop("box"), "p", Some(PLRule.Assumption()), Seq())
      line("4", AtLine("3", Direction.Below), "q", Some(PLRule.ImplicationElim()), Seq("1", "3"))
      line("5", AtLine("4", Direction.Below), "s", Some(PLRule.ImplicationElim()), Seq("4", "2"))
      line("6", AtLine("box", Direction.Below), "p and p", Some(PLRule.AndIntro()), Seq("3", "3"))
      line("7", AtLine("6", Direction.Below), "p implies  r", Some(PLRule.ImplicationIntro()), Seq("box"))

      val optProofView: Proof[Option[PLFormula], Option[PLRule], Option[PLBoxInfo], Id] = ProofView(proof,
        { case (_, Proof.Line(IncompleteFormula(_, optF: Option[PLFormula]), optR: Option[PLRule], refs: Seq[Id])) => 
            ProofLineImpl(optF, optR, refs)
          case (_, Proof.Box(info: PLBoxInfo, steps: Seq[Id])) => 
            ProofBoxImpl(Some(info), steps)
        }
      )

      val scopedChecker = ScopedProofChecker[Id]()
      val optionRuleChecker: RuleChecker[Option[PLFormula], Option[PLRule], Option[PLBoxInfo], OptionRuleChecker.Violation[PLViolation]] = 
        OptionRuleChecker(DelegatingRuleChecker[PLFormula, PLRule, PLBoxInfo, PLViolation]())
      val ruleBasedProofChecker: ProofChecker[Option[PLFormula], Option[PLRule], Option[PLBoxInfo], Id, RuledBasedProofChecker.Diagnostic[Id, OptionRuleChecker.Violation[PLViolation]]] = 
        RuledBasedProofChecker(optionRuleChecker)

      val scopedResult = scopedChecker.check(proof)
      val ruleBasedResult = ruleBasedProofChecker.check(optProofView)
      val aft = System.currentTimeMillis()
      println(s"Took ${aft - bef} ms")

      scopedResult.foreach(println)
      ruleBasedResult.foreach(println)
    }
  }
}
