

## Syntax
### Propositional logic
||| Syntax |
|-|-|-|
| Propositional atoms | $a$, $b$, $p$, $q$, ... | `a`, `b`, `p`, `q`, ... |
| Conjunction (and) | $p \\land q$ | `p and q`, `p A q`, `p ∧ q` |
| Disjunction (or) | $a \\lor b$ | `a or b`, `a V b` |
| Implication (if ... then) | $q \\rightarrow r$ | `q implies r`, `q -> r`, `q => r` |
| Negation (not) | $\\lnot s$ | `not s`, `!s`, `¬s` |
| Contradiction | $\\bot$ | `false`, `bot` |
| Tautology | $\\top$ | `true`, `top`, |

The following precedence rules apply (Huth, Micheal 1962, convention 1.3, page 5)
- $\\lnot$ binds most tightly
- then $\\land$ and $\\lor$, which are *left-associative*
- then $\\rightarrow$, which is *right-associative*

As examples, this means that
- `not p and q` is parsed as $(\\lnot p) \\land q)$
- `p and q or r` is parsed as $(p \\land q) \\lor r$
- `p -> q -> r` is parsed as $p \\rightarrow (q \\rightarrow r)$

Note: if an ambiguous formula (such as `p and q and r`) is entered, it will automatically be converted to an unambiguous form (in this case $(p \\land q) \\land r$) 

### Predicate logic
||| Syntax |
|-|-|-|
| Universal quantification (for all) | $\\forall x P(x)$ | `forall x P(x)`, `∀x P(x)` |
| Existensial quantification (exists) | $\\exists y Q(y)$ | `exists y Q(y)`, `∃y Q(y)` |
| Predicate | $P(a, b, c)$ | `P(a, b, c)` |
| Equality | $x = y$ | `x = y` |
| Variables | $x$, $y$, $z$, $x_0$, $k_{123}$ | `x`, `y`, `z`, `x_0`, `k_{123}` |
| Function application | $f(x, y, z)$ | `f(x, y, z)` |

The symbols $\\land, \\lor, \\rightarrow, \\lnot, \\bot, \\top$ are also supported, and are parsed as described above.

The following precedence rules apply (Huth, Micheal 1962, convention 2.4, page 101)
- $\\lnot, \\forall x$ and $\\exists y$ binds most tightly
- then $\\land$ and $\\lor$, which are *left-associative*
- then $\\rightarrow$, which is *right-associative*

As examples, this means that
- `forall x P(x) and not Q(x)` is parsed as $(\\forall x P(x)) \\land (\\lnot Q(x))$
- `P(a) and Q(b) or R(c)` is parsed as $((P(a) \\land Q(b)) \\land R(c))$
- `P(a) -> Q(b) -> R(c)` is parsed as $P(a) \\rightarrow (Q(b) \\rightarrow R(c))$

Note: nullary predicates are also supported. As an example, this means that that the formula `forall x S -> Q(x)` ( $\\forall x (S \\rightarrow Q(x))$ ) consists of two predicate symbols $P, Q$, where $P$ takes $0$ arguments and $Q$ takes $1$.

### Arithmetic
||| Syntax |
|-|-|-|
| Universal quantification (for all) | $\\forall x P(x)$ | `forall x P(x)`, `∀x P(x)` |
| Existensial quantification (exists) | $\\exists y Q(y)$ | `exists y Q(y)`, `∃y Q(y)` |
| Equality | $x = y$ | `x = y` |
| Variables | $x$, $y$, $z$, $x_0$, $k_{123}$ | `x`, `y`, `z`, `x_0`, `k_{123}` |
| Addition (plus) | $x + y$ | `x + y` |
| Multiplication (times) | $x * y$ | `x * y` |
| Zero, One | $0$, $1$ | `0`, `1` |

As arithmetic is just an instance of predicate logic with no predicate symbols, the constants (or nullary functions) $0, 1$ and functions $+, *$ (infix), the precedence rules described above still apply.
