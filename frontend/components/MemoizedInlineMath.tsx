import "katex/dist/katex.min.css";
import React from "react";
import { InlineMath, MathComponentPropsWithMath } from "react-katex";

declare global {
  var toggleFishMode: () => void;
}

const IN_FISH_MODE = (localStorage.getItem("mode") === "FISH");
window.toggleFishMode = () => {
  if (IN_FISH_MODE) {
    localStorage.removeItem("mode")
  } else {
    localStorage.setItem("mode", "FISH")
  }
  window.location.href = window.location.href
}

const MyInlineMath = (props: MathComponentPropsWithMath) => {
  if (IN_FISH_MODE) {
    const math = props.math
      .replaceAll("x_0", "🐟")
      .replaceAll("y_0", "🐠")
    return <InlineMath {...props} math={math}/>
  } else return <InlineMath {...props}/>
}

export const MemoizedInlineMath = React.memo(MyInlineMath)
