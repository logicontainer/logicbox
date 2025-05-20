package logicbox.rule

trait Substitutor[F, T, V] {
  // substitute every free occurance of x in f with t (compute f[t/x])
  def substitute(f: F, t: T, x: V): F

  // returns true iff `t` occurs in `f`
  def hasFreeOccurance(f: F, t: T): Boolean

  // returns
  //  - Some(t) for the `t` for which dst = src[t/x]
  //  - Some(()) if dst = src[t/x]
  //  - None if no replace exists, such that 
  def findReplacement(src: F, dst: F, x: V): Option[T | Unit]
}
