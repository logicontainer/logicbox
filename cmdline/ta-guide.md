#### Download the `.lgbx` files from your students
Go to brightspace and download each `.lgbx` file handed in by your students.

Put them in some directory you can locate with your terminal.

#### Install node.js
Make sure you have node.js installed. Instructions on how to do so are here: 
> https://nodejs.org/en/download

#### Download the `bulk_verify.js` script
```shell
> curl -O https://raw.githubusercontent.com/logicontainer/logicbox/refs/heads/main/cmdline/bulk_verify.js
```

#### Locate the files
Open a terminal, go to the folder containing `.lgbx` files and run the script on the files.

You should see output as below
```shell
> node path/to/bulk_verify.js path/to/proofs/*.lgbx
┌─────────┬──────────────────────┬────────────┬─────────────┬───────────────────────┐
│ (index) │ filename             │ # of lines │ # of errors │ sequent               │
├─────────┼──────────────────────┼────────────┼─────────────┼───────────────────────┤
│ 0       │ 'proofs/group1.lgbx' │ 10         │ 0           │ 'p ∧ q |- ¬(¬p ∨ ¬q)' │
│ 1       │ 'proofs/group2.lgbx' │ 11         │ 3           │ 'p ∧ q |- ¬(¬p ∨ ¬q)' │
│ 2       │ 'proofs/group3.lgbx' │ 9          │ 4           │ 'p ∧ q |- ¬(¬p ∨ ¬q)' │
│ 3       │ 'proofs/group4.lgbx' │ 6          │ 0           │ 'p ∧ q |- ¬(¬p ∧ ¬q)' │
└─────────┴──────────────────────┴────────────┴─────────────┴───────────────────────┘
```

Here we can see that 
- group 1 correctly solved the exercise
- group 2 and 3 have errors in their proofs
- group 4 proved a different sequent that the question required (assuming the question is $p \land q \vdash \lnot (\lnot p \lor \lnot q)$)
