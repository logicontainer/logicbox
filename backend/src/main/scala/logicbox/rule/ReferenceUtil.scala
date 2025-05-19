package logicbox.rule

import logicbox.framework.Reference
import logicbox.framework.Violation
import logicbox.framework.Violation._

object ReferenceUtil {
  enum BoxOrFormula { case Box; case Formula }

  def extractAndThen[F, I](refs: List[Reference[F, I]], pattern: Seq[BoxOrFormula]) 
    (func: PartialFunction[List[Reference[F, I]], List[Violation]]): List[Violation] = 
  {
    def checkLengthMatches(refs: Seq[?], pattern: Seq[?]): List[Violation] = {
      if (refs.length != pattern.length) List(
        WrongNumberOfReferences(pattern.length, refs.length)
      ) else Nil
    }

    def extract[F, I](refs: List[Reference[F, I]], pattern: Seq[BoxOrFormula]): Either[List[Violation], List[Reference[F, I]]] = {
      val zp = refs.zipWithIndex.zip(pattern).map { case ((ref, idx), pattern) => (idx, pattern, ref)}

      import Reference._

      val result = zp.map {
        // matches
        case (_, BoxOrFormula.Formula, f: Line[_]) => Right(f)
        case (_, BoxOrFormula.Box, b: Box[F, I]) => Right(b)

        // violations
        case (idx, BoxOrFormula.Box, _) => Left(ReferenceShouldBeBox(idx))
        case (idx, BoxOrFormula.Formula, _) => Left(ReferenceShouldBeLine(idx))
      }

      val good = result.forall {
        case Right(_) => true
        case Left(_) => false
      }

      if (good) {
        // collect steps 
        Right(result.collect { case Right(step) => step }) 
      } else {
        // collect violations
        Left(result.collect { case Left(mm) => mm })
      }
    }

    val mms1 = checkLengthMatches(refs, pattern)
    if mms1.nonEmpty then mms1 else { 
      extract(refs, pattern) match {
        case Right(ls) =>
          assert(func.isDefinedAt(ls), s"Partial function is not defined on given pattern $pattern")
          func.apply(ls)

        case Left(mismatches) => 
          mismatches
      }
    }
  }

  // try to extract a list of `n` formulas from `refs` (only if there are `n`).
  // otherwise report mismatches
  def extractNFormulasAndThen[F, I](refs: List[Reference[F, I]], n: Int)
    (func: PartialFunction[List[F], List[Violation]]): List[Violation] = 
  {
    extractAndThen(refs, (1 to n).map { _ => BoxOrFormula.Formula }) {
      case lines: List[Reference.Line[F]] @unchecked =>
        func.apply(lines.map(_.formula))
    }
  }
}
