package logicbox.rule

import logicbox.framework.{Reference, Error}
import logicbox.framework.Error._

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors
import logicbox.rule.RulePart.MetaFormula
import logicbox.framework.Location
import java.awt.Shape

class PLRuleTest extends AnyFunSpec {
  import logicbox.rule.PropLogicRule._
  import logicbox.formula._

  private val lexer = PropLogicLexer()
  private val parser = PropLogicParser()
  private def parse(str: String): PropLogicFormula = parser(lexer(str))

  // fake things so tests still work (a little hacky, i admit)
  private case class Line(formula: PropLogicFormula, rule: PropLogicRule, refs: List[Reference[PropLogicFormula, PLBoxInfo]])
    extends Reference.Line[PropLogicFormula]
  private case class Box(fst: PropLogicFormula, lst: PropLogicFormula) extends Reference.Box[PropLogicFormula, PLBoxInfo] {
    override def info = ()
    override def first = Some(ReferenceLineImpl(fst))
    override def last = Some(ReferenceLineImpl(lst))
  }

  private def stub(str: String): Reference[PropLogicFormula, PLBoxInfo] = new Reference.Line[PropLogicFormula] {
    def formula = parse(str)
  }

  private def boxStub(ass: String, concl: String): Reference.Box[PropLogicFormula, PLBoxInfo] =
    Box(parse(ass), parse(concl))

  private def line(fml: String, rl: PropLogicRule, refs: List[Reference[PropLogicFormula, PLBoxInfo]]): Line =
    Line(parse(fml), rl, refs)

  private val checker = PropLogicRuleChecker[PropLogicFormula]()

  import logicbox.framework.RulePosition._
  import logicbox.rule.RulePart._

  describe("AndElim") {
    val leftRule = AndElim(Side.Left)

    it("should copy lhs of conjunction") {
      val ref = stub("p and (q or p and q -> r -> not not not (not p or r -> q))")
      val l = line("p", leftRule, List(ref))
      checker.check(leftRule, l.formula, List(ref)) should be (Nil)
    }

    it("should reject disjunction (doesn't match rule, left)") {
      val ref = stub("(p and q) or v")
      val l = line("p and q", leftRule, List(ref))

      checker.check(leftRule, l.formula, List(ref)) shouldBe List(
        ShapeMismatch(Ref(0), And(MetaFormula(0), MetaFormula(1)))
      )
    }

    it("should reject when result isn't lhs of ref"){
      // wrong formula on lhs (is q, should be p)
      val ref = stub("q and (p -> v or r)")
      val l = line("p", leftRule, List(ref))
      checker.check(leftRule, l.formula, List(ref)) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(0), Location.lhs)
        ))
      )
    }

    val rightRule = AndElim(Side.Right)

    it("should elim rhs") {
      val ref = stub("p and (q or p and q -> r -> not not not (not p or r -> q))")
      val l = line("(q or p and q -> r -> not not not (not p or r -> q))", rightRule, List(ref))
      assert(checker.check(rightRule, l.formula, List(ref)) === Nil)
    }

    it("should not match or") {
      // doesn't match rule
      val ref = stub("(p and q) or v")
      val l = line("p", rightRule, List(ref))
      checker.check(rightRule, l.formula, List(ref)) shouldBe List(
        ShapeMismatch(Ref(0), And(MetaFormula(0), MetaFormula(1)))
      )
    }

    it("should not match wrong atom") {
      val ref = stub("q and (p -> v or r)")
      val l = line("p", rightRule, List(ref))
      checker.check(rightRule, l.formula, List(ref)) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(1), Location.rhs)
        ))
      )
    }
  }

  describe("AndIntro") {
    val rule = AndIntro()
    val refs = List("p", "q").map(stub)

    it("should work with correct usage") {
      val l = line("p and q", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe Nil
    }

    it("should reject when line is or") {
      val l = line("p or q", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, And(MetaFormula(0), MetaFormula(1)))
      )
    }

    it("should report two formula mismatches when both operands are wrong") {
      val l = line("r and (s or v)", rule, refs)
      val mismatches = checker.check(rule, l.formula, l.refs)
      Inspectors.forAtLeast(1, mismatches) {
        _ shouldBe Ambiguous(MetaFormula(0), List(
          (Formula, Location.lhs),
          (Ref(0), Location.root)
        ))
      } 
      Inspectors.forAtLeast(1, mismatches) {
        _ shouldBe Ambiguous(MetaFormula(1), List(
          (Formula, Location.rhs),
          (Ref(1), Location.root)
        ))
      }
    }

    it("should report lhs mismatches ref") {
      val l = line("r and q", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.lhs),
          (Ref(0), Location.root)
        ))
      )
    }
    
    it("should report rhs mismatches ref") {
      val l = line("p and r", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(1), List(
          (Formula, Location.rhs),
          (Ref(1), Location.root)
        ))
      )
    }
  }

  describe("orIntro") {
    val leftRule = OrIntro(Side.Left)

    it("should intro with reference on lhs") {
      val ref = stub("p")
      val l = line("p or (p -> q -> v)", leftRule, List(ref))
      checker.check(leftRule, l.formula, List(ref)) shouldBe Nil
    }

    it("should not match with wrong lhs") {
      val ref = stub("p")
      val l = line("q or (p -> q -> v)", leftRule, List(ref))
      checker.check(leftRule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.lhs),
          (Ref(0), Location.root)
        ))
      )
    }

    it("should not introduce and (left)") {
      val ref = stub("p")
      val l = line("p and (p -> q -> v)", leftRule, List(ref))
      checker.check(leftRule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Or(MetaFormula(0), MetaFormula(1)))
      )
    }

    val rightRule = OrIntro(Side.Right)
    it("should introduce with ref on rhs") {
      val ref = stub("p")
      val l = line("(p -> q -> v) or p", rightRule, List(ref))
      assert(checker.check(rightRule, l.formula, List(ref)) === Nil)
    }

    it("should not introduce q when ref is p") {
      val ref = stub("p")
      val l = line("(p -> q -> v) or q", rightRule, List(ref))
      checker.check(rightRule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(1), List(
          (Formula, Location.rhs),
          (Ref(0), Location.root)
        ))
      )
    }

    it("should not introduce and (right)") {
      val ref = stub("p")
      val l = line("(p -> q -> v) and p", rightRule, List(ref))
      checker.check(rightRule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Or(MetaFormula(0), MetaFormula(1)))
      )
    }
  }
  
  describe("OrElim") {
    val rule = OrElim()
    it("should reject when first ref is box") {
      // r0 is not line
      val (r0, r1, r2) = (boxStub("p or q", "s"), boxStub("p", "s"), boxStub("q", "s"))
      val l = line("s", rule, List(r0, r1, r2))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ReferenceShouldBeLine(0)
      )
    }

    it("should reject when second ref is line") {
      // r1 is not box
      val (r0, r1, r2) = (stub("p or q"), stub("s"), boxStub("q", "s"))
      val l = line("s", rule, List(r0, r1, r2))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ReferenceShouldBeBox(1)
      )
    }

    it("should reject when third ref is line"){
      // r2 is not box
      val (r0, r1, r2) = (stub("p or q"), boxStub("p", "s"), stub("q"))
      val l = line("s", rule, List(r0, r1, r2))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ReferenceShouldBeBox(2)
      )
    }

    it("should reject implication (should be or)") {
      val (r0, r1, r2) = (stub("p -> q"), boxStub("p", "s"), boxStub("q", "s"))
      val l = line("s", rule, List(r0, r1, r2))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(0), Or(MetaFormula(0), MetaFormula(1)))
      )
    }

    it("should reject incorrect conclusion (third ref)") {
      val refs = List(stub("p or q"), boxStub("p", "s"), boxStub("q", "d"))
      val l = line("s", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(2), List(
          (Formula, Location.root),
          (Ref(1), Location.conclusion),
          (Ref(2), Location.conclusion)
        ))
      )
    }

    it("should reject incorrect conclusion (second ref)") {
      val refs = List(stub("p or q"), boxStub("p", "d"), boxStub("q", "s"))
      val l = line("s", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(2), List(
          (Formula, Location.root),
          (Ref(1), Location.conclusion),
          (Ref(2), Location.conclusion)
        ))
      )
    }

    it("should reject when assumption of second ref is not lhs of first ref") {
      val refs = List(stub("p or q"), boxStub("d", "s"), boxStub("q", "s"))
      val l = line("s", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Ref(0), Location.lhs),
          (Ref(1), Location.assumption)
        ))
      )
    }

    it("should reject when assumption of third ref is not rhs of first ref") {
      val refs = List(stub("p or q"), boxStub("p", "s"), boxStub("d", "s"))
      val l = line("s", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(1), List(
          (Ref(0), Location.rhs),
          (Ref(2), Location.assumption)
        ))
      )
    }
  }
  
  
  describe("ImpicationIntro") {
    val rule = ImplicationIntro()
    it("should reject when lhs is not assumption") {
      val box = boxStub("p", "q")
      val l = line("r -> q", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.lhs),
          (Ref(0), Location.assumption)
        ))
      )
    }

    it("should reject when rhs is not conclusion") {
      val box = boxStub("p", "q")
      val l = line("p -> r", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(1), List(
          (Formula, Location.rhs),
          (Ref(0), Location.conclusion)
        ))
      )
    }

    it("should not introduce and") {
      val box = boxStub("p", "q")
      val l = line("p and q", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Implies(MetaFormula(0), MetaFormula(1)))
      )
    }

    it("should not introduce implication when given line as ref") {
      val l = line("p -> q", rule, List(stub("q")))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ReferenceShouldBeBox(0)
      )
    }
  }
  
  describe("ImplicationElim") {
    val rule = ImplicationElim()
    it("should reject if first ref doesn't match lhs of second") {
      val (r0, r1) = (stub("p"), stub("r -> q"))
      val l = line("q", rule, List(r0, r1))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Ref(0), Location.root),
          (Ref(1), Location.lhs)
        ))
      )
    }

    it("should reject when formula is not rhs of second ref") {
      val (r0, r1) = (stub("p"), stub("p -> q"))
      val l = line("r", rule, List(r0, r1))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(1), List(
          (Ref(1), Location.rhs),
          (Formula, Location.root)
        ))
      )
    }
    
    it("should reject when second ref is conjunction") {
      val (r0, r1) = (stub("p"), stub("p and q"))
      val l = line("q", rule, List(r0, r1))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(1), Implies(MetaFormula(0), MetaFormula(1)))
      )
    }
  }
  
  describe("NotIntro") {
    val rule = NotIntro()

    it("should reject when last line is not contradiction") {
      val box = boxStub("p", "q")
      val l = line("not p", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch((Ref(0), Location.conclusion), Contradiction())
      )
    }

    it("should reject when formula is not negation of assumption") {
      val box = boxStub("p", "false")
      val l = line("not q", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.negated),
          (Ref(0), Location.assumption)
        ))
      )
    }

    it("should reject when formula is not a negation") {
      val box = boxStub("p", "false")
      val l = line("p", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Not(MetaFormula(0)))
      )
    }
  }  
  
  describe("NotElim") {
    val rule = NotElim()
    it("should reject when second ref is not negation of first") {
      val refs = List(stub("p"), stub("not q"))
      val l = line("false", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Ref(0), Location.root),
          (Ref(1), Location.negated)
        ))
      )
    }

    it("should reject when second ref is not a negation") {
      val refs = List(stub("p"), stub("p"))
      val l = line("false", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(1), RulePart.Not(MetaFormula(0)))
      )
    }

    it("should reject when formula is not a contradiction") {
      val refs = List(stub("p"), stub("not p"))
      val l = line("p", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Contradiction())
      )
    }
  }
  
  describe("ContradictionElim") {
    val rule = ContradictionElim()
    it("should reject when ref is not contradiction") {
      val ref = stub("p")
      val l = line("p", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(0), Contradiction())
      )
    }
  }

  describe("NotNotElim") {
    val rule = NotNotElim() 

    it("should reject when ref is only single negation (not double)") {
      val ref = stub("not p")
      val l = line("p", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(0), RulePart.Not(RulePart.Not(MetaFormula(0))))
      )
    }

    it("should reject when formula does not match what has been doubly negated") {
      val ref = stub("not not p")
      val l = line("q", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(0), Location.negated.negated)
        ))
      )
    }
  }
  
  describe("ModusTollens") {
    val rule = ModusTollens()

    it("should reject when formula is not negation") {
      val refs = List(stub("p -> q"), stub("not q"))
      val l = line("p", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Not(MetaFormula(0)))
      )
    }

    it("should reject when first ref is not implication") {
      val refs = List(stub("p and q"), stub("not q"))
      val l = line("not p", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(0), Implies(MetaFormula(0), MetaFormula(1)))
      )
    }

    it("should reject when second ref is not negation") {
      val refs = List(stub("p -> q"), stub("q"))
      val l = line("not p", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Ref(1), Not(MetaFormula(1)))
      )
    }

    it("should reject when formula is not negation of lhs of implication") {
      val refs = List(stub("p -> q"), stub("not q"))
      val l = line("not r", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.negated),
          (Ref(0), Location.lhs)
        ))
      )
    }

    it("should reject when second ref is not negation of rhs of implication") {
      val refs = List(stub("p -> q"), stub("not r"))
      val l = line("not p", rule, refs)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(1), List(
          (Ref(0), Location.rhs),
          (Ref(1), Location.negated)
        ))
      )
    }
  }
  
  describe("NotNotIntro") {
    val rule = NotNotIntro()

    it("should reject when formula is not double negation") {
      val ref = stub("q")
      val l = line("q", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Not(Not(MetaFormula(0))))
      )
    }

    it("should reject when formula is double negation of wrong subformula") {
      val ref = stub("q")
      val l = line("not not p", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.negated.negated),
          (Ref(0), Location.root)
        ))
      )
    }
  }

  describe("ProofByContradiction") {
    val rule = ProofByContradiction()

    it("should reject when ref is not a box") {
      val ref = stub("false") // not a box
      val l = line("q", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ReferenceShouldBeBox(0)
      )
    }

    it("should reject when assumption is not a negation") {
      val box = boxStub("p", "false")
      val l = line("p", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch((Ref(0), Location.assumption), Not(MetaFormula(0)))
      )
    }

    it("should reject when conclusion is not contradiction") {
      val box = boxStub("not p", "true") // should end in bot
      val l = line("p", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch((Ref(0), Location.conclusion), RulePart.Contradiction())
      )
    }

    it("should reject when formula is not the assumption without outer negation") {
      val box = boxStub("not p", "false")
      val l = line("q", rule, List(box))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(0), Location.assumption.negated)
        ))
      )
    }
  }
  
  describe("LawOfExcludedMiddle") {
    val rule = LawOfExcludedMiddle()

    it("should allow correct usage") {
      val l = line("p or not p", rule, Nil)
      checker.check(rule, l.formula, l.refs) shouldBe Nil
    }

    it("should reject when rhs is not negation") {
      val l = line("p or p", rule, Nil)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        ShapeMismatch(Formula, Or(MetaFormula(0), Not(MetaFormula(0))))
      )
    }

    it("should reject when lhs and negated rhs are not equal") {
      val l = line("p or not q", rule, Nil)
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.lhs),
          (Formula, Location.rhs.negated)
        ))
      )
    }
  }
  
  describe("Copy") {
    val rule = Copy()
    it("should reject when formula is not same as ref") {
      val ref = stub("q")
      val l = line("p", rule, List(ref))
      checker.check(rule, l.formula, l.refs) shouldBe List(
        Ambiguous(MetaFormula(0), List(
          (Formula, Location.root),
          (Ref(0), Location.root)
        ))
      )
    }
  }
}

// ------------------- OLD STUFF ----------------------------------
// def fullProof = {
//   val l1 = line("p -> q", Premise(), Nil)
//   val l2 = line("r -> s", Premise(), Nil)
//
//   val l3 = line("p and r", Assumption(), Nil)
//   val l4 = line("p", AndElim(Side.Left), List(l3))
//   val l5 = line("r", AndElim(Side.Right), List(l3))
//   val l6 = line("q", ImplicationElim(), List(l4, l1))
//   val l7 = line("s", ImplicationElim(), List(l5, l2))
//   val l8 = line("q and s", AndIntro(), List(l6, l7))
//
//   val box = Proof.Box(info = (), proof = List(l3, l4, l5, l6, l7, l8))
//   val l9 = line("p and r -> q and s", ImplicationIntro(), List(box))
//
//   def checkProof(p: Proof[PLFormula]): List[(PLFormula, Mismatch)] = p.flatMap {
//     case Proof, formula, rule, refs) => rule.checker.check(formula, refs).map((formula, _))
//     case Proof.Box(_, proof) => checkProof(proof)
//   }
//
//   val proof = List(l1, l2, box, l9)
//   checkProof(proof).foreach {
//     case (formula, mismatch) => println(s"$formula:\n $mismatch")
  // }
// }
