package logicbox.proof

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

import logicbox.formula._
import logicbox.framework.ModifiableProof
import logicbox.framework.ProofChecker
import logicbox.framework.Proof
import logicbox.framework.RuleChecker
import logicbox.framework.IncompleteFormula

class IntegrateVerifyPLProofTest extends AnyFunSpec {
  private type F = IncompleteFormula[PropLogicFormula]
  private type R = Option[PropLogicRule]
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
      
      def line(id: Id, pos: ModifiableProof.Pos[Id], f: String, rule: Option[PropLogicRule], refs: Seq[Id]): Unit = {
        proof = proof.addLine(id, pos).getOrElse(???)
        val optF = try {
          Some(PropLogicParser()(PropLogicLexer()(f)))
        } catch {
          case _ => None
        }
        proof = proof.updateFormula(id, IncompleteFormula(f, optF)).getOrElse(???)
        proof = proof.updateRule(id, rule).getOrElse(???)
        proof = proof.updateReferences(id, refs).getOrElse(???)
      }

      line("1", ProofTop, "p -> q", Some(PropLogicRule.Premise()), Seq())
      line("2", AtLine("1", Direction.Below), "q -> s", None, Seq())

      proof = proof.addBox("box", AtLine("2", Direction.Below)).getOrElse(???)
      line("3", BoxTop("box"), "p", Some(PropLogicRule.Assumption()), Seq())
      line("4", AtLine("3", Direction.Below), "q", Some(PropLogicRule.ImplicationElim()), Seq("1", "3"))
      line("5", AtLine("4", Direction.Below), "s", Some(PropLogicRule.ImplicationElim()), Seq("4", "2"))
      line("6", AtLine("box", Direction.Below), "p and p", Some(PropLogicRule.AndIntro()), Seq("3", "3"))
      line("7", AtLine("6", Direction.Below), "p implies  r", Some(PropLogicRule.ImplicationIntro()), Seq("box"))

      val optProofView: Proof[Option[PropLogicFormula], Option[PropLogicRule], Option[PLBoxInfo], Id] = ProofView(proof,
        { case (_, Proof.Line(IncompleteFormula(_, optF), optR, refs)) => 
            ProofLineImpl(optF, optR, refs)
          case (_, Proof.Box(info, steps)) => 
            ProofBoxImpl(Some(info), steps)
        }
      )

      val scopedChecker = ScopedProofChecker[Id]()
      val optionRuleChecker: RuleChecker[Option[PropLogicFormula], Option[PropLogicRule], Option[PLBoxInfo], OptionRuleChecker.Violation[PropLogicViolation]] = 
        OptionRuleChecker(PropLogicRuleChecker())
      val ruleBasedProofChecker: ProofChecker[Option[PropLogicFormula], Option[PropLogicRule], Option[PLBoxInfo], Id, RuleBasedProofChecker.Diagnostic[Id, OptionRuleChecker.Violation[PropLogicViolation]]] = 
        RuleBasedProofChecker(optionRuleChecker)

      val scopedResult = scopedChecker.check(proof)
      val ruleBasedResult = ruleBasedProofChecker.check(optProofView)
    }
  }
}
