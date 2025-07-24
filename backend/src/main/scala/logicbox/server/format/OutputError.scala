package logicbox.server.format

sealed trait OutputError {
  def uuid: String
  def errorType: String
}

// 'conclusion' or 'premise X' where X ∈ { 0, 1, 2, ... } (eg. 'conclusion' or 'premise 0')
type OutputRulePos = String 

object OutputError {
  case class Simple(uuid: String, errorType: String) extends OutputError
  case class RefErr(uuid: String, errorType: String, refIdx: Int) extends OutputError

  case class WrongNumberOfReferences(uuid: String, expected: Int, actual: Int) extends OutputError {
    override def errorType = "WrongNumberOfReferences"
  }

  case class FreshVarEscaped(uuid: String, boxUuid: String, freshVar: String) extends OutputError {
    override def errorType: String = "FreshVarEscaped"
  }

  case class ShapeMismatch(
    uuid: String, 
    rulePosition: OutputRulePos, // in which part of rule the mismatch is
    expected: String,            // LaTeX formula of the expected shape                    (eg. '\phi \land \psi' or '\bot')
    actual: String               // LaTeX formula of what is there                         (eg. 'p \rightarrow q' or '\forall x P(x) \land Q(a)')
  ) extends OutputError {
    override def errorType = "ShapeMismatch"
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
  ) extends OutputError {
    override def errorType = "Ambiguous"
  }

  case class Miscellaneous(
    uuid: String,
    rulePosition: OutputRulePos, // in which part of rule the error occured
    explanation: String          // a (hopefully) readable error message
  ) extends OutputError {
    override def errorType = "Miscellaneous"
  }
}
