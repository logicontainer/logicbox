package logicbox.server

import logicbox.formula._
import logicbox.rule._
import logicbox.framework._
import logicbox.proof._
import logicbox.server.format._

// 'factory'
object ArithLogicProofValidatorService {
  private type F = ArithLogicFormula
  private type R = PropLogicRule | PredLogicRule | ArithLogicRule
  private type B = FreshVarBoxInfo[ArithLogicTerm.Var]
  private type Id = String

  import logicbox.server.format.SprayFormatters._

  private def proofChecker: ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] = {
    val scopedChecker = ScopedProofChecker[Id]()

    val boxAssumptionProofChecker = PropLogicBoxAssumptionsProofChecker[R, Id]()
    val boxContraintsProofChecker = PredLogicBoxConstraintsProofChecker[R, Id](PropLogicRule.Assumption())

    val propLogicChecker: RuleChecker[F, PropLogicRule, B] = PropLogicRuleChecker[F]()
    val predLogicChecker = PredLogicRuleChecker[F, ArithLogicTerm, ArithLogicTerm.Var](
      ArithLogicFormulaSubstitutor()
    )
    val arithLogicChecker = ArithLogicRuleChecker[F, ArithLogicTerm, ArithLogicTerm.Var](
      ArithLogicFormulaSubstitutor()
    )

    val optionRuleChecker: RuleChecker[Option[F], Option[R], Option[B]] = 
      OptionRuleChecker(Union3RuleChecker(propLogicChecker, predLogicChecker, arithLogicChecker, which = {
        case r: PropLogicRule => 1
        case r: PredLogicRule => 2
        case r: ArithLogicRule => 3
      }))

    val ruleBasedProofChecker: ProofChecker[Option[F], Option[R], Option[B], Id] = 
      RuleBasedProofChecker(optionRuleChecker)

    new ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] {
      override def check(proof: Proof[IncompleteFormula[F], Option[R], Option[B], Id]): List[Diagnostic[Id]] = {
        val optProofView: Proof[Option[F], Option[R], Option[B], Id] = ProofView(proof, { 
          case (id, line: Proof.Line[IncompleteFormula[F], Option[R], Id]) => 
            ProofLineImpl(line.formula.optFormula, line.rule, line.refs)

          case (_, box: Proof.Box[Option[B], Id]) => 
            ProofBoxImpl(box.info, box.steps)
        })

        val cleanRulesProofView: Proof[?, R, ?, Id] = OptionProofView(optProofView, {
          case (_, Proof.Line(f, Some(r), refs)) => 
            Some(ProofLineImpl(f, r, refs))
          case (_, Proof.Box(Some(info), steps)) => 
            Some(ProofBoxImpl(info, steps))
          case _ => None
        })

        ruleBasedProofChecker.check(optProofView) ++ scopedChecker.check(proof) ++
        boxContraintsProofChecker.check(cleanRulesProofView) ++
        boxAssumptionProofChecker.check(cleanRulesProofView)
      }
    }
  }

  private def parseFormula(userInput: String): Option[F] = {
    try {
      Some(ArithLogicParser().parseFormula(ArithLogicLexer()(userInput)))
    } catch { case _ => None }
  }

  private def parseVariable(userInput: String): Option[ArithLogicTerm.Var] = {
    try {
      Some(ArithLogicParser().parseVariable(ArithLogicLexer()(userInput)))
    } catch { case _ => None }
  }

  private def ruleParser(rule: String): Option[R] = 
    PropLogicRuleParser.parse(rule)
      .orElse(PredLogicRuleParser.parse(rule))
      .orElse(ArithLogicRuleParser.parse(rule))

  val rawProofConverter = RawProofToIncompleteProofConverter[F, R, FreshVarBoxInfo[ArithLogicTerm.Var]](
    parseFormula = parseFormula,
    parseRule = ruleParser,
    parseRawBoxInfo = {
      case RawBoxInfo(Some(x)) => Some(FreshVarBoxInfo(parseVariable(x)))
      case _ => Some(FreshVarBoxInfo(None))
    },
    formulaToLatex = Stringifiers.arithLogicFormulaAsLaTeX, 
    formulaToAscii = Stringifiers.arithLogicFormulaAsASCII,
    ruleToString = {
      case rule: PropLogicRule => Stringifiers.propLogicRuleAsString(rule)
      case rule: PredLogicRule => Stringifiers.predLogicRuleAsString(rule)
      case rule: ArithLogicRule => Stringifiers.arithLogicRuleAsString(rule)
    },
    boxInfoToRaw = {
      case FreshVarBoxInfo(freshVar) => RawBoxInfo(freshVar.map(_.x.toString))
    }
  )
}

import ArithLogicProofValidatorService._
class ArithLogicProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], Option[B]
](
  rawProofConverter = rawProofConverter, 
  proofChecker = proofChecker
)
