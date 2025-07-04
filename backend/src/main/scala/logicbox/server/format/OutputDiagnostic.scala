package logicbox.server.format

sealed trait OutputDiagnostic {
  def uuid: String
  def violationType: String
}

// 'conclusion' or 'premise X' where X ∈ { 0, 1, 2, ... } (eg. 'conclusion' or 'premise 0')
type OutputRulePos = String 


object OutputDiagnostic {
  case class Simple(uuid: String, violationType: String) extends OutputDiagnostic
  case class RefDiag(uuid: String, violationType: String, refIdx: Int) extends OutputDiagnostic

  case class WrongNumberOfReferences(uuid: String, expected: Int, actual: Int) extends OutputDiagnostic {
    override def violationType = "WrongNumberOfReferences"
  }

  case class ShapeMismatch(
    uuid: String, 
    rulePosition: OutputRulePos, // in which part of rule the mismatch is
    expected: String,            // LaTeX formula of the expected shape                    (eg. '\phi \land \psi' or '\bot')
    actual: String               // LaTeX formula of what is there                         (eg. 'p \rightarrow q' or '\forall x P(x) \land Q(a)')
  ) extends OutputDiagnostic {
    override def violationType = "ShapeMismatch"
  }

  case class AmbiguityEntry(
    rulePosition: OutputRulePos, // the part of the rule this entry pertains to
    meta: String,                // LaTeX formula of the meta variable which this entry is for (eg. '\phi[n + 1/x]', '\chi')
    actual: String               // LaTeX formula of what is in place of `meta` in this entry
  )

  case class Ambiguous(
    uuid: String,
    subject: String,             // LaTeX representation of what is ambiguous (eg. '\phi', 't_2')
    entries: List[AmbiguityEntry]
  ) extends OutputDiagnostic {
    override def violationType = "Ambiguous"
  }

  case class Miscellaneous(
    uuid: String,
    rulePosition: OutputRulePos, // in which part of rule the error occured
    explanation: String          // a (hopefully) readable error message
  ) extends OutputDiagnostic {
    override def violationType = "Miscellaneous"
  }
}
