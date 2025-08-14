## How do you use LogicBox?
### Add a line
Add a line to your proof by right-clicking on an existing step and clicking on ⬆️ to add a line above or on ⬇️ to add a line below.

<!-- TODO: video adding a line -->

### Enter a formula
Modify the formula of a line by double-clicking on it, and entering the new value.

<!-- TODO: video of entering a formula -->

(alternatively, you can right-click and choose 'Edit')

### Choose a rule
Choose a rule by clicking on the rule and selecting a new one from the side-panel. By hovering on a rule, you may see its definition.

<!-- TODO: video of choosing a rule -->

(note: when a new line is created, "???" is displayed to indicate no rule)

### Pick lines to refer to
To refer to a line, click on a reference, then click on the line/box which you would like to refer to.

<!-- TODO: video of referring to line -->

You may refer to a box by clicking close to its border.

<!-- TODO: video of referring to box -->

### Inspect errors
You may inspect the errors currently on a line/box by clicking on it, and viewing the errors in the side-panel.

<!-- TODO: video of clicking on line and inspecting errors -->

If no line/box is currently selected, the errors pertaining to the currently hovered element will be shown.

<!-- TODO: video of inspecting errors of multiple lines (by hovering) -->

### Add a box
Add a box to the proof by right-clicking on an existing step and clicking on ⬆️ to add a box above or on ⬇️ to add a box below.

<!-- TODO: video of adding box -->

### Remove a step
You may remove a line by right-clicking on it and selecting 'Delete'.

<!-- TODO: video of removing a line -->

If you remove a box, you will remove all steps it contains.

<!-- TODO: video of removing a box with a bunch of stuff -->

### Move a step
You can move a line/box by dragging it to its new location

<!-- TODO: video of moving a line -->

### Edit fresh variable in a box (only in predicate logic/arithmetic)
You may add/edit a fresh variable by right-clicking on a box and choosing 'Edit fresh variable'.

<!-- TODO: video of adding fresh var to box by using context menu -->

(alternatively you may double-click on the box to edit its fresh variable)

<!-- TODO: video of adding fresh var to box by double-clicking -->

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
- $\\lnot, \\forall x$ and $\\exists y$ bind most tightly
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
