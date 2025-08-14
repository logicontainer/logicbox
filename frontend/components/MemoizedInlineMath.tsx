import "katex/dist/katex.min.css";
import React from "react";
import { InlineMath } from "react-katex";

export const MemoizedInlineMath = React.memo(InlineMath)
