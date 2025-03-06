package logicbox.proof

import logicbox.framework.{Reference}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class PLRuleTest extends AnyFunSpec {
  import PLRule._
  import PLViolation._
  import logicbox.formula._

  private val lexer = PLLexer()
  private val parser = PLParser()
  private def parse(str: String): PLFormula = parser(lexer(str))

  // fake things so tests still work (a little hacky, i admit)
  private case class Line(formula: PLFormula, rule: PLRule, refs: List[Reference[PLFormula, PLBoxInfo]])
    extends Reference.Line[PLFormula]
  private case class Box(fst: PLFormula, lst: PLFormula) extends Reference.Box[PLFormula, PLBoxInfo] {
    override def info = ()
    override def assumption = fst
    override def conclusion = lst
  }

  private def stub(str: String): Reference[PLFormula, PLBoxInfo] = new Reference.Line[PLFormula] {
    def formula = parse(str)
  }

  private def boxStub(ass: String, concl: String): Reference.Box[PLFormula, PLBoxInfo] =
    Box(parse(ass), parse(concl))

  private def line(fml: String, rl: PLRule, refs: List[Reference[PLFormula, PLBoxInfo]]): Line =
    Line(parse(fml), rl, refs)

  describe("AndElim") {
    val leftRule = AndElim(Side.Left)

    it("should copy lhs of conjunction") {
      val ref = stub("p and (q or p and q -> r -> not not not (not p or r -> q))")
      val l = line("p", leftRule, List(ref))
      leftRule.check(l.formula, List(ref)) should be (Nil)
    }

    it("should reject disjunction (doesn't match rule, left)") {
      val ref = stub("(p and q) or v")
      val l = line("p and q", leftRule, List(ref))

      leftRule.check(l.formula, List(ref)) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) => 
      }
    }

    it("should reject disjunction (right)"){
      // wrong formula on lhs (is q, should be p)
      val ref = stub("q and (p -> v or r)")
      val l = line("p", leftRule, List(ref))
      leftRule.check(l.formula, List(ref)) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }

    val rightRule = AndElim(Side.Right)
    it("should elim rhs") {
      val ref = stub("p and (q or p and q -> r -> not not not (not p or r -> q))")
      val l = line("(q or p and q -> r -> not not not (not p or r -> q))", rightRule, List(ref))
      assert(rightRule.check(l.formula, List(ref)) === Nil)
    }
    it("should not match or") {
      // doesn't match rule
      val ref = stub("(p and q) or v")
      val l = line("p", rightRule, List(ref))
      rightRule.check(l.formula, List(ref)) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }
    it("should not match wrong atom") {
      // wrong formula on lhs (is q, should be p)
      val ref = stub("q and (p -> v or r)")
      val l = line("p", rightRule, List(ref))
      rightRule.check(l.formula, List(ref)) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
  }

  describe("orIntro") {
    val leftRule = OrIntro(Side.Left)
    it("should intro with reference on lhs") {
      val ref = stub("p")
      val l = line("p or (p -> q -> v)", leftRule, List(ref))
      assert(leftRule.check(l.formula, List(ref)) === Nil)
    }
    it("should not match with wrong lhs") {
      val ref = stub("p")
      val l = line("q or (p -> q -> v)", leftRule, List(ref))
      leftRule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
    it("should not introduce and (left)") {
      val ref = stub("p")
      val l = line("p and (p -> q -> v)", leftRule, List(ref))
      leftRule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
    val rightRule = OrIntro(Side.Right)
    it("should introduce with ref on rhs") {
      val ref = stub("p")
      val l = line("(p -> q -> v) or p", rightRule, List(ref))
      assert(rightRule.check(l.formula, List(ref)) === Nil)
    }
    it("should not introduce q when ref is p") {
      val ref = stub("p")
      val l = line("(p -> q -> v) or q", rightRule, List(ref))
      rightRule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
    it("should not introduce and (right)") {
      val ref = stub("p")
      val l = line("(p -> q -> v) and p", rightRule, List(ref))
      rightRule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
  }

  describe("OrElim") {
    val rule = OrElim()
    it("should reject when first ref is box") {
      // r0 is not line
      val (r0, r1, r2) = (boxStub("p or q", "s"), boxStub("p", "s"), boxStub("q", "s"))
      val l = line("s", rule, List(r0, r1, r2))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceShouldBeLine(0, _)) =>
      }
    }

    it("should reject when second ref is line") {
      // r1 is not box
      val (r0, r1, r2) = (stub("p or q"), stub("s"), boxStub("q", "s"))
      val l = line("s", rule, List(r0, r1, r2))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceShouldBeBox(1, _)) =>
      }
    }

    it("should reject when third ref is line"){
      // r2 is not box
      val (r0, r1, r2) = (stub("p or q"), boxStub("p", "s"), stub("q"))
      val l = line("s", rule, List(r0, r1, r2))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceShouldBeBox(2, _)) =>
      }
    }
  
    it("should reject implication (should be or)") {
      val (r0, r1, r2) = (stub("p -> q"), boxStub("p", "s"), boxStub("q", "s"))
      val l = line("s", rule, List(r0, r1, r2))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) =>
      }
    }

    it("should reject incorrect conclusion (third ref)") {
      val refs = List(stub("p or q"), boxStub("p", "s"), boxStub("q", "d"))
      val l = line("s", rule, refs)
      val mms = rule.check(l.formula, l.refs)
      Inspectors.forAtLeast(1, mms) {
        _ should matchPattern {
          case ReferencesMismatch(List(1, 2), _) =>
        }
      }
      Inspectors.forAtLeast(1, mms) {
        _ should matchPattern {
          case FormulaDoesntMatchReference(2, _) =>
        }
      }
    }

    it("should reject incorrect conclusion (second ref)") {
      val refs = List(stub("p or q"), boxStub("p", "d"), boxStub("q", "s"))
      val l = line("s", rule, refs)
      val mms = rule.check(l.formula, l.refs)
      Inspectors.forAtLeast(1, mms) {
        _ should matchPattern {
          case ReferencesMismatch(List(1, 2), _) =>
        }
      }
      Inspectors.forAtLeast(1, mms) {
        _ should matchPattern {
          case FormulaDoesntMatchReference(1, _) =>
        }
      }
    }
  }

  describe("AndIntro") {
    val rule = AndIntro()
    val refs = List("p", "q").map(stub)

    it("should work with correct usage") {
      val l = line("p and q", rule, refs)
      rule.check(l.formula, l.refs) should be (Nil)
    }

    it("should report two formula mismatches when both operands are wrong") {
      val l = line("r and (s or v)", rule, refs)
      val mismatches = rule.check(l.formula, l.refs)
      Inspectors.forAtLeast(1, mismatches) {
        _ should matchPattern {
          case FormulaDoesntMatchReference(0, _) =>
        }
      } 
      Inspectors.forAtLeast(1, mismatches) {
        _ should matchPattern {
          case FormulaDoesntMatchReference(1, _) =>
        }
      }
    }

    it("should report lhs mismatches ref") {
      val l = line("r and q", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) => 
      }
    }

    it("should report rhs mismatches ref") {
      val l = line("p and r", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(1, _)) => 
      }
    }
  }

  describe("ImpicationIntro") {
    val rule = ImplicationIntro()
    it("should reject when lhs is not assumption") {
      val box = boxStub("p", "q")
      val l = line("r -> q", rule, List(box))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
    it("should reject when rhs is not conclusion") {
      val box = boxStub("p", "q")
      val l = line("p -> r", rule, List(box))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
    it("should not introduce and") {
      val box = boxStub("p", "q")
      val l = line("p and q", rule, List(box))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
    it("should not introduce implication when given line as ref") {
      val l = line("p -> q", rule, List(stub("q")))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceShouldBeBox(0, _)) =>
      }
    }
  }

  describe("ImplicationElim") {
    val rule = ImplicationElim()
    it("should reject if first ref doesn't match lhs of second") {
      val (r0, r1) = (stub("p"), stub("r -> q"))
      val l = line("q", rule, List(r0, r1))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferencesMismatch(List(0, 1), _)) => 
      }
    }
    it("should reject when formula is not rhs of second ref") {
      val (r0, r1) = (stub("p"), stub("p -> q"))
      val l = line("r", rule, List(r0, r1))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(1, _)) => 
      }
    }
    it("should reject when second ref is conjunction") {
      val (r0, r1) = (stub("p"), stub("p and q"))
      val l = line("q", rule, List(r0, r1))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(1, _)) => 
      }
    }
  }

  describe("NotIntro") {
    val rule = NotIntro()

    it("should reject when last line is not contradiction") {
      val box = boxStub("p", "q")
      val l = line("not p", rule, List(box))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) => 
      }
    }

    it("should reject when formula is not negation of assumption") {
      val box = boxStub("p", "false")
      val l = line("not q", rule, List(box))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }

    it("should reject when formula is not a negation") {
      val box = boxStub("p", "false")
      val l = line("p", rule, List(box))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
  }  

  describe("NotElim") {
    val rule = NotElim()
    it("should reject when second ref is not negation of first") {
      val refs = List(stub("p"), stub("not q"))
      val l = line("false", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferencesMismatch(List(0, 1), _)) =>
      }
    }

    it("should reject when second ref is not a negation") {
      val refs = List(stub("p"), stub("p"))
      val l = line("false", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(1, _)) =>
      }
    }

    it("should reject when formula is not a contradiction") {
      val refs = List(stub("p"), stub("not p"))
      val l = line("p", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
  }

  describe("ContradictionElim") {
    val rule = ContradictionElim()
    it("should reject when formula is not contradiction") {
      val ref = stub("p")
      val l = line("p", rule, List(ref))
      rule.check(l.formula, l.refs) match {
        case List(ReferenceDoesntMatchRule(0, _)) =>
        case s => println(s"huh: $s")
      }
    }
  }

  describe("NotNotElim") {
    val rule = NotNotElim() 

    it("should reject when ref is only single negation (not double)") {
      val ref = stub("not p")
      val l = line("p", rule, List(ref))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) => 
      }
    }

    it("should reject when formula does not match what has been doubly negated") {
      val ref = stub("not not p")
      val l = line("q", rule, List(ref))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) => 
      }
    }
  }

  describe("ModusTollens") {
    val rule = ModusTollens()

    it("should reject when formula is not negation of rhs of implication") {
      val refs = List(stub("p -> q"), stub("not q"))
      val l = line("p", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) => 
      }
    }

    it("should reject when first ref is not implication") {
      val refs = List(stub("p and q"), stub("not q"))
      val l = line("not p", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(0, _)) => 
      }
    }

    it("should reject when second ref is not negation") {
      val refs = List(stub("p -> q"), stub("q"))
      val l = line("not p", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferenceDoesntMatchRule(1, _)) => 
      }
    }

    it("should reject when formula is not negation of lhs of implication") {
      val refs = List(stub("p -> q"), stub("not q"))
      val l = line("not r", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) => 
      }
    }
    
    it("should reject when second ref is not negation of rhs of implication") {
      val refs = List(stub("p -> q"), stub("not r"))
      val l = line("not p", rule, refs)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(ReferencesMismatch(List(0, 1), _)) => 
      }
    }
  }

  describe("NotNotIntro") {
    val rule = NotNotIntro()

    it("should reject when formula is not double negation") {
      val ref = stub("q")
      val l = line("q", rule, List(ref))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) => 
      }
    }

    it("should reject when formula is double negation of wrong subformula") {
      val ref = stub("q")
      val l = line("not not p", rule, List(ref))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
    }
  }

  describe("ProofByContradiction") {
    val rule = ProofByContradiction()

    it("should reject when ref is not a box") {
      val ref = stub("false") // not a box
      val l = line("q", rule, List(ref))
      rule.check(l.formula, l.refs) match {
        case List(ReferenceShouldBeBox(0, _)) => 
        case _ => Nil
      }
    }

    it("should reject when assumption is not a negation") {
      val box = boxStub("p", "false")
      val l = line("p", rule, List(box))
      rule.check(l.formula, l.refs) match {
        case List(ReferenceDoesntMatchRule(0, _)) =>
        case s => println(s"huh: $s")
      }
    }

    it("should reject when conclusion is not contradiction") {
      val box = boxStub("not p", "true") // should end in bot
      val l = line("p", rule, List(box))
      rule.check(l.formula, l.refs) match {
        case List(ReferenceDoesntMatchRule(0, _)) =>
        case s => println(s"huh: $s")
      }
    }

    it("should reject when formula is not the assumption without outer negation") {
      val box = boxStub("not p", "false")
      val l = line("q", rule, List(box))
      rule.check(l.formula, l.refs) match {
        case List(FormulaDoesntMatchReference(0, _)) =>
        case s => println(s"huh: $s")
      }
    }
  }

  describe("LawOfExcludedMiddle") {
    val rule = LawOfExcludedMiddle()
    it("should reject when rhs is not negation") {
      val l = line("p or p", rule, Nil)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }

    it("should reject when refs don't refer to same subformula") {
      val l = line("p or not q", rule, Nil)
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchRule(_)) =>
      }
    }
  }

  describe("Copy") {
    val rule = Copy()
    it("should reject when formula is not same as ref") {
      val ref = stub("q")
      val l = line("p", rule, List(ref))
      rule.check(l.formula, l.refs) should matchPattern {
        case List(FormulaDoesntMatchReference(0, _)) =>
      }
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
//     case Proof.Line(formula, rule, refs) => rule.check(formula, refs).map((formula, _))
//     case Proof.Box(_, proof) => checkProof(proof)
//   }
//
//   val proof = List(l1, l2, box, l9)
//   checkProof(proof).foreach {
//     case (formula, mismatch) => println(s"$formula:\n $mismatch")
//   }
// }
//
