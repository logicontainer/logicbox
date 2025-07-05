package logicbox.server.format

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.Inspectors

class TemplateFormulaAndTermToLaTeXTest extends AnyFunSpec {
  describe("Stringifiers::templateFormulaToLaTeX") {
    it("should work on an example") {
      import logicbox.rule.RulePart._
      Stringifiers.templateFormulaToLaTeX(
        Implies(
          ForAll(MetaVariable(Vars.X), 
            And(
              Equals(MetaVariable(Vars.X0), Plus(MetaTerm(Terms.T1), MetaTerm(Terms.T))),
              MetaFormula(Formulas.Psi)
            )
          ), 
          Or(
            MetaFormula(Formulas.Psi),
            Not(MetaFormula(Formulas.Chi))
          )
        )
      ) shouldBe "forall x ((x_0 = t_1 + t) and \\psi) -> (\\psi or not \\chi)"
    }
  }
}
