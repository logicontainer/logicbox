package logicbox.rule

trait Substitutor[F, T, V] {
  // substitute every free occurance of x in f with t (compute f[t/x])
  def substitute(f: F, t: T, x: V): F
}
