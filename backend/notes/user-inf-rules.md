The theoretical background for allowing a proof like
$$p \land q \vdash \lnot (\lnot p \lor \lnot q)$$
to be generalized to
$$\forall \vphi, \psi \in \Phi: \vphi \land \psi \vdash \lnot (\lnot \vphi \lor \lnot \psi)$$

Is the following theorem:

Given a set of formulas $\Phi$ and a provability relation ${\vdash} \subseteq
\Phi^2$.

We can do this if for every two formulas $\vphi, \psi \in \Phi$ with
$$F \triangleq \vb{freeAtoms}(\vphi) \cup \vb{freeAtoms}(\psi)$$
and every assignment 
$$p: F \ra \Phi$$
of the free atoms to formulas, 
$$\vphi \vdash \psi$$
holds if and only if
$$\vphi^* \vdash \psi^*$$
holds, where
$$\chi^* \triangleq \chi[p(x_1)/x_1]\cdots [p(x_m)/x_m]$$
