package logicbox.rule

sealed trait RulePart

object RulePart {
  sealed trait TemplateTerm extends RulePart
  case class MetaVariable(id: Int) extends TemplateTerm
  case class MetaTerm(id: Int) extends TemplateTerm
  case class Plus(t1: TemplateTerm, t2: TemplateTerm) extends TemplateTerm
  case class Mult(t1: TemplateTerm, t2: TemplateTerm) extends TemplateTerm
  case class Zero() extends TemplateTerm
  case class One() extends TemplateTerm

  sealed trait TemplateFormula extends RulePart
  case class MetaFormula(id: Int) extends TemplateFormula
  case class Equals(t1: TemplateTerm, t2: TemplateTerm) extends TemplateFormula
  case class Substitution(phi: TemplateFormula, t: TemplateTerm, x: MetaVariable) extends TemplateFormula

  case class Contradiction() extends TemplateFormula
  case class Not(phi: TemplateFormula) extends TemplateFormula
  case class And(phi: TemplateFormula, psi: TemplateFormula) extends TemplateFormula
  case class Or(phi: TemplateFormula, psi: TemplateFormula) extends TemplateFormula
  case class Implies(phi: TemplateFormula, psi: TemplateFormula) extends TemplateFormula

  case class Exists(x: MetaVariable, phi: TemplateFormula) extends TemplateFormula
  case class ForAll(x: MetaVariable, phi: TemplateFormula) extends TemplateFormula

  case class TemplateBox(ass: Option[TemplateFormula], concl: Option[TemplateFormula], freshVar: Option[MetaVariable]) extends RulePart
}
