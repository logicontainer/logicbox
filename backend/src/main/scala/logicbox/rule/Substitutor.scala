package logicbox.rule

trait Substitutor[F, T, V] {
  // substitute every free occurance of x in f with t (compute f[t/x])
  //   NOTE: doesn't check whether t is free for x in f
  def substitute(f: F, t: T, x: V): F

  // returns true iff t is free for x in f
  def isFreeFor(f: F, t: T, x: V): Boolean

  // returns true iff `t` occurs in `f`
  def hasFreeOccurance(f: F, t: T): Boolean

  // returns
  //  - Some(t) for the `t` for which dst = src[t/x]
  //  - Some(()) if dst = src[t/x]
  //  - None if no replace exists, such that 
  def findReplacement(src: F, dst: F, x: V): Option[Either[Unit, T]]

  // returns true iff f1[t1/x] = f2[t2/x] for some x
  def equalExcept(f1: F, f2: F, t1: T, t2: T): Boolean
}
