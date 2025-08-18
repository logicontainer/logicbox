## How do you use LogicBox?
### Create a new proof
Create a new proof by clicking on the ➕ icon on the front page. Then enter the name of the proof, and the type of logic the proof is in.
- propositional logic: with atomic formulas $p, q, r, \dots$ and the logical connectives $\land, \lor$ and $\rightarrow$
- predicate logic: with quantifiers $\forall, \exists$, predicates $P, Q, \dots$, functions $f(x, y), g(z)$ and equality $x_0 = y$
- arithmetic: with addition $+$, multiplication $*$, $0$ and $1$.

![13  create-proof](https://github.com/user-attachments/assets/c6b0ef0c-6cea-4876-a976-68e7b6ff53a0)

(note: when a proof is created, it contains a single line with no formula or rule specified)

### Add a line
Add a line to your proof by right-clicking on an existing step and clicking on ⬆️ to add a line above or on ⬇️ to add a line below.

![01  add-line](https://github.com/user-attachments/assets/f2e5ccea-7303-4a4a-93e7-1afde748e0fc)

### Enter a formula
Modify the formula of a line by double-clicking on it, and entering the new value.

![02  edit-formula](https://github.com/user-attachments/assets/b47c0e53-19b7-4223-b9a9-4235d89fcf4e)

(alternatively, you can right-click and choose 'Edit')

### Choose a rule
Choose a rule by clicking on the rule and selecting a new one from the side-panel. By hovering on a rule, you may see its definition.

![03  choose-rule](https://github.com/user-attachments/assets/1baa76b8-2d70-4c5e-932b-d697ae3a5e3c)

(note: when a new line is created, "???" is displayed to indicate no rule)

### Pick lines to refer to
To refer to a line, click on a reference, then click on the line/box which you would like to refer to.

![04  pick-refs](https://github.com/user-attachments/assets/7f1a7cec-3084-460a-ba0f-daedf22b462f)

### Inspect errors
You may inspect the errors currently on a line/box by clicking on it, and viewing the errors in the side-panel.

![05  inspect-single-line](https://github.com/user-attachments/assets/e101064a-a419-423c-91d4-246851378794)

If no line/box is currently selected, the errors pertaining to the currently hovered element will be shown.

![06  inspect-multiple-lines](https://github.com/user-attachments/assets/87a4ade2-0dea-4e42-a839-00eedf47bb4c)

### Add a box
Add a box to the proof by right-clicking on an existing step and clicking on ⬆️ to add a box above or on ⬇️ to add a box below.

![07  add-box](https://github.com/user-attachments/assets/cd9cfeb1-2641-4db4-9bab-6dc5a06df008)

### Remove a step
You may remove a line by right-clicking on it and selecting 'Delete'.

![08  delete-line](https://github.com/user-attachments/assets/08d2b201-5bdd-47ea-bcd0-af56e1546a76)

If you remove a box, you will remove all steps it contains.

![09  delete-box](https://github.com/user-attachments/assets/244111bd-dfa6-4aa7-8f85-7b72d8420fc7)

### Move a step
You can move a line/box by dragging it to its new location

![10  move-line](https://github.com/user-attachments/assets/d8471c62-b5ff-4191-b5e2-9346566bef4c)

### Edit fresh variable in a box (only in predicate logic/arithmetic)
You may add/edit a fresh variable by right-clicking on a box and choosing 'Edit fresh variable'.

![11  edit-fresh-var-ctx-menu](https://github.com/user-attachments/assets/54ff79e7-7763-4a53-aa1c-2b184fc3e16d)

(alternatively you may double-click on the box to edit its fresh variable)

![12  edit-fresh-var-dbl-click](https://github.com/user-attachments/assets/4b5cf8a7-56fc-4dda-a414-c985fcb47b62)

## Syntax
### Propositional logic
||| Syntax |
|-|-|-|
| Propositional atoms | $a$, $b$, $p$, $q$, ... | `a`, `b`, `p`, `q`, ... |
| Conjunction (and) | $p \land q$ | `p and q`, `p A q`, `p ∧ q` |
| Disjunction (or) | $a \lor b$ | `a or b`, `a V b` |
| Implication (if ... then) | $q \rightarrow r$ | `q implies r`, `q -> r`, `q => r` |
| Negation (not) | $\lnot s$ | `not s`, `!s`, `¬s` |
| Contradiction | $\bot$ | `false`, `bot` |
| Tautology | $\top$ | `true`, `top`, |

The following precedence rules apply (Huth, Micheal 1962, convention 1.3, page 5)
- $\lnot$ binds most tightly
- then $\land$ and $\lor$, which are *left-associative*
- then $\rightarrow$, which is *right-associative*

As examples, this means that
- `not p and q` is parsed as $(\lnot p) \land q$
- `p and q or r` is parsed as $(p \land q) \lor r$
- `p -> q -> r` is parsed as $p \rightarrow (q \rightarrow r)$

Note: if an ambiguous formula (such as `p and q and r`) is entered, it will automatically be converted to an unambiguous form (in this case $(p \land q) \land r$) 

### Predicate logic
||| Syntax |
|-|-|-|
| Universal quantification (for all) | $\forall x P(x)$ | `forall x P(x)`, `∀x P(x)` |
| Existensial quantification (exists) | $\exists y Q(y)$ | `exists y Q(y)`, `∃y Q(y)` |
| Predicate | $P(a, b, c)$ | `P(a, b, c)` |
| Equality | $x = y$ | `x = y` |
| Variables | $x$, $y$, $z$, $x_0$, $k_{123}$ | `x`, `y`, `z`, `x_0`, `k_{123}` |
| Function application | $f(x, y, z)$ | `f(x, y, z)` |

The symbols $\land, \lor, \rightarrow, \lnot, \bot, \top$ are also supported, and are parsed as described above.

The following precedence rules apply (Huth, Micheal 1962, convention 2.4, page 101)
- $\lnot, \forall x$ and $\exists y$ bind most tightly
- then $\land$ and $\lor$, which are *left-associative*
- then $\rightarrow$, which is *right-associative*

As examples, this means that
- `forall x P(x) and not Q(x)` is parsed as $(\forall x P(x)) \land (\lnot Q(x))$
- `P(a) and Q(b) or R(c)` is parsed as $(P(a) \land Q(b)) \land R(c)$
- `P(a) -> Q(b) -> R(c)` is parsed as $P(a) \rightarrow (Q(b) \rightarrow R(c))$

Note: nullary predicates are also supported. As an example, this means that that the formula `forall x S -> Q(x)` ( $\forall x (S \rightarrow Q(x))$ ) consists of two predicate symbols $S, Q$, where $S$ takes $0$ arguments and $Q$ takes $1$.

### Arithmetic
||| Syntax |
|-|-|-|
| Universal quantification (for all) | $\forall x P(x)$ | `forall x P(x)`, `∀x P(x)` |
| Existensial quantification (exists) | $\exists y Q(y)$ | `exists y Q(y)`, `∃y Q(y)` |
| Equality | $x = y$ | `x = y` |
| Variables | $x$, $y$, $z$, $x_0$, $k_{123}$ | `x`, `y`, `z`, `x_0`, `k_{123}` |
| Addition (plus) | $x + y$ | `x + y` |
| Multiplication (times) | $x * y$ | `x * y` |
| Zero, One | $0$, $1$ | `0`, `1` |

As arithmetic is just an instance of predicate logic with no predicate symbols, the constants (or nullary functions) $0, 1$ and functions $+, *$ (infix), the precedence rules described above still apply.
