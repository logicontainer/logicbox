package logicbox.rule

import logicbox.rule.ArithLogicRule.Peano1
import logicbox.rule.ArithLogicRule.Peano2
import logicbox.rule.ArithLogicRule.Peano3
import logicbox.rule.ArithLogicRule.Peano4
import logicbox.rule.ArithLogicRule.Peano5
import logicbox.rule.ArithLogicRule.Peano6
import logicbox.rule.ArithLogicRule.Induction
import logicbox.framework.InfRule
import logicbox.rule.PropLogicRule.Premise
import logicbox.rule.PropLogicRule.Assumption
import logicbox.rule.PropLogicRule.Copy
import logicbox.rule.PropLogicRule.AndElim
import logicbox.rule.PropLogicRule.AndIntro
import logicbox.rule.PropLogicRule.OrIntro
import logicbox.rule.PropLogicRule.OrElim
import logicbox.rule.PropLogicRule.ImplicationIntro
import logicbox.rule.PropLogicRule.ImplicationElim
import logicbox.rule.PropLogicRule.NotIntro
import logicbox.rule.PropLogicRule.NotElim
import logicbox.rule.PropLogicRule.ContradictionElim
import logicbox.rule.PropLogicRule.NotNotElim
import logicbox.rule.PropLogicRule.NotNotIntro
import logicbox.rule.PropLogicRule.ModusTollens
import logicbox.rule.PropLogicRule.ProofByContradiction
import logicbox.rule.PropLogicRule.LawOfExcludedMiddle
import java.lang.invoke.LambdaMetafactory
import logicbox.framework.RulePart
import logicbox.rule.PredLogicRule.ForAllElim
import logicbox.rule.PredLogicRule.ForAllIntro
import logicbox.rule.PredLogicRule.ExistsElim
import logicbox.rule.PredLogicRule.ExistsIntro
import logicbox.rule.PredLogicRule.EqualityIntro
import logicbox.rule.PredLogicRule.EqualityElim

object RuleMaps {
  val PHI = RulePart.MetaFormula(RulePart.Formulas.Phi)
  val PSI = RulePart.MetaFormula(RulePart.Formulas.Psi)

  def getPropLogicInfRule(rule: PropLogicRule): InfRule = {
    import logicbox.framework.RulePart._
    rule match {
      case Premise() | Assumption() => InfRule(Nil, PHI)
      case Copy() => InfRule(List(PHI), MetaFormula(Formulas.Phi))

      case AndElim(side) => InfRule(
        List(And(PHI, PSI)),
        MetaFormula(side match {
          case PropLogicRule.Side.Left => Formulas.Phi
          case PropLogicRule.Side.Right => Formulas.Psi
        })
      )

      case AndIntro() => InfRule(
        List(
          PHI,
          PSI,
        ),
        And(PHI, PSI)
      )

      case OrIntro(side) => InfRule(
        List(MetaFormula(side match {
          case PropLogicRule.Side.Left => Formulas.Phi
          case PropLogicRule.Side.Right => Formulas.Psi
        })),
        Or(PHI, PSI)
      )

      case OrElim() => InfRule(
        List(
          Or(PHI, PSI),
          TemplateBox(
            Some(PHI),
            Some(MetaFormula(Formulas.Chi)),
            None
          ),
          TemplateBox(
            Some(PSI),
            Some(MetaFormula(Formulas.Chi)),
            None
          )
        ),
        MetaFormula(Formulas.Chi)
      )

      case ImplicationIntro() => InfRule(
        List(TemplateBox(
          Some(PHI),
          Some(PSI),
          None
        )),
        Implies(PHI, PSI)
      )

      case ImplicationElim() => InfRule(
        List(
          PHI,
          Implies(PHI, PSI)
        ),
        PSI
      )

      case NotIntro() => InfRule(
        List(
          TemplateBox(
            Some(PHI), 
            Some(Contradiction()),
            None
          )
        ),
        Not(PHI)
      )


      case NotElim() => InfRule(
        List(
          PHI,
          Not(PHI)
        ),
        Contradiction()
      )


      case ContradictionElim() => InfRule(
        List(Contradiction()),
        PHI
      )

      case NotNotElim() => InfRule(
        List(Not(Not(PHI))),
        PHI
      )

      case NotNotIntro() => InfRule(
        List(PHI),
        Not(Not(PHI))
      )

      case ModusTollens() => InfRule(
        List(Implies(PHI, PSI), Not(PSI)),
        Not(PHI)
      )

      case ProofByContradiction() => InfRule(
        List(TemplateBox(
          Some(Not(PHI)),
          Some(Contradiction()),
          None
        )),
        PHI
      )

      case LawOfExcludedMiddle() => InfRule(
        Nil,
        Or(PHI, Not(PHI))
      )
    }
  }
  
  def getPredLogicInfRule(rule: PredLogicRule): InfRule = {
    import RulePart._
    rule match {
      case ForAllElim() => InfRule(
        List(ForAll(MetaVariable(Vars.X), PHI)),
        Substitution(PHI, MetaTerm(Terms.T), MetaVariable(Vars.X))
      )

      case ForAllIntro() => InfRule(
        List(TemplateBox(
          None,
          Some(Substitution(PHI, MetaVariable(Vars.X0), MetaVariable(Vars.X))),
          Some(MetaVariable(Vars.X0))
        )),
        ForAll(MetaVariable(Vars.X), PHI)
      )

      case ExistsElim() => InfRule(
        List(
          Exists(MetaVariable(Vars.X), PHI),
          TemplateBox(
            Some(Substitution(PHI, MetaVariable(Vars.X0), MetaVariable(Vars.X))),
            Some(MetaFormula(Formulas.Chi)),
            Some(MetaVariable(Vars.X0))
          )
        ),
        MetaFormula(Formulas.Chi)
      )

      case ExistsIntro() => InfRule(
        List(Substitution(PHI, MetaVariable(Vars.X0), MetaVariable(Vars.X))),
        Exists(MetaVariable(Vars.X), PHI)
      )

      case EqualityIntro() => InfRule(
        Nil,
        Equals(MetaTerm(Terms.T), MetaTerm(Terms.T))
      )

      case EqualityElim() => InfRule(
        List(
          Equals(MetaTerm(Terms.T1), MetaTerm(Terms.T2)),
          Substitution(PHI, MetaTerm(Terms.T1), MetaVariable(Vars.X))
        ),
        Substitution(PHI, MetaTerm(Terms.T2), MetaVariable(Vars.X))
      )
    }
  }

  def getArithLogicInfRule(rule: ArithLogicRule): InfRule = {
    import logicbox.framework.RulePart._
    rule match {
      case Peano1() => InfRule(
        Nil, 
        Equals( // t + 0 = 0
          Plus(MetaTerm(Terms.T), Zero()), 
          MetaTerm(Terms.T)
        )
      )

      case Peano2() => InfRule(
        Nil, 
        Equals(
          Plus(MetaTerm(Terms.T1), Plus(MetaTerm(Terms.T2), One())), // t1 + (t2 + 1)
          Plus(Plus(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), One())  // (t1 + t2) + 1
        )
      )

      case Peano3() => InfRule(
        Nil,
        Equals(
          Mult(MetaTerm(Terms.T), Zero()),
          Zero()
        )
      )

      case Peano4() => InfRule(
        Nil,
        Equals(
          Mult(MetaTerm(Terms.T1), Plus(MetaTerm(Terms.T2), One())), // t1 * (t2 + 1),
          Plus(Mult(MetaTerm(Terms.T1), MetaTerm(Terms.T2)), MetaTerm(Terms.T1)) // (t1 * t2) + t1
        )
      )

      case Peano5() => InfRule(
        Nil,
        Not(Equals(
          Zero(),
          Plus(MetaTerm(Terms.T), One())
        ))
      )


      case Peano6() => InfRule(List(
        Equals(
          Plus(MetaTerm(Terms.T1), One()),
          Plus(MetaTerm(Terms.T2), One()),
        )), 
        Equals(
          MetaTerm(Terms.T1),
          MetaTerm(Terms.T2),
        )
      )

      case Induction() => InfRule(List(
        Substitution(PHI, Zero(), MetaVariable(Vars.X)), // phi[0/x]
        TemplateBox(
         // phi[n/x]
         Some(Substitution(PHI, MetaVariable(Vars.N), MetaVariable(Vars.X))), 
         // phi[n+1/x]
         Some(Substitution(PHI, Plus(MetaVariable(Vars.N), One()), MetaVariable(Vars.X))),
         // n
         Some(MetaVariable(Vars.N))
        )
      ), ForAll(MetaVariable(Vars.X), PHI)) 
    }
  }
}
