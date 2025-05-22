package logicbox.server

import logicbox.formula._
import logicbox.rule._
import logicbox.framework._
import logicbox.proof._
import logicbox.server.format._

// 'factory'
object PredLogicProofValidatorService {
  private type F = PredLogicFormula
  private type R = PropLogicRule | PredLogicRule
  private type B = PredLogicBoxInfo[PredLogicTerm.Var]
  private type Id = String

  import logicbox.server.format.SprayFormatters._

  private def proofChecker: ProofChecker[IncompleteFormula[F], Option[R], Option[B], Id] = {
    val scopedChecker = ScopedProofChecker[Id]()

    val boxContraintsProofChecker = PredLogicBoxConstraintsProofChecker[R, Id](PropLogicRule.Assumption())

    val predLogicChecker = PredLogicRuleChecker[F, PredLogicTerm, PredLogicTerm.Var](
      PredLogicFormulaSubstitutor()
    )

    val propLogicChecker: RuleChecker[F, PropLogicRule, B] = PropLogicRuleChecker[F]()

    val optionRuleChecker: RuleChecker[Option[F], Option[R], Option[B]] = 
      OptionRuleChecker(UnionRuleChecker(predLogicChecker, propLogicChecker, isR1 = {
        case r: PropLogicRule => true
        case _ => false
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
        boxContraintsProofChecker.check(cleanRulesProofView)
      }
    }
  }

  private def parseFormula(userInput: String): Option[F] = {
    try {
      Some(PredLogicParser()(PredLogicLexer()(userInput)))
    } catch { case _ => None }
  }

  private def ruleParser(rule: String): Option[R] = PropLogicRuleParser.parse(rule)
  private val formulaToAscii = format.Stringifiers.propLogicFormulaAsASCII
  private val formulaToLatex = format.Stringifiers.propLogicFormulaAsLaTeX
  private val ruleToString   = format.Stringifiers.propLogicRuleAsString

  val rawProofConverter = RawProofToIncompleteProofConverter[F, R, PredLogicBoxInfo[PredLogicTerm.Var]](
    parseFormula = parseFormula,
    parseRule = ruleParser,
    parseRawBoxInfo = {
      case RawBoxInfo(Some(x)) => x.headOption.map(c => PredLogicBoxInfo(Some(PredLogicTerm.Var(c))))
      case _ => None
    },
    formulaToLatex = Stringifiers.predLogicFormulaAsLaTeX, 
    formulaToAscii = Stringifiers.predLogicFormulaAsASCII,
    ruleToString = {
      case rule: PropLogicRule => Stringifiers.propLogicRuleAsString(rule)
      case rule: PredLogicRule => Stringifiers.predLogicRuleAsString(rule)
    },
    boxInfoToRaw = {
      case PredLogicBoxInfo(freshVar) => RawBoxInfo(freshVar.map(_.x.toString))
    }
  )
}

import PredLogicProofValidatorService._
class PredLogicProofValidatorService extends ProofValidatorServiceImpl[
  IncompleteFormula[F], Option[R], Option[B]
](
  rawProofConverter = rawProofConverter, 
  proofChecker = proofChecker
)
