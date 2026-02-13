import "katex/dist/katex.min.css";
import React from "react";
import { InlineMath, MathComponentPropsWithMath } from "react-katex";

declare global {
  interface Window {
    toggleFishMode: () => void;
  }
}

const IN_FISH_MODE = typeof window !== 'undefined' && globalThis.localStorage?.getItem("mode") === "FISH";
if (typeof window !== 'undefined') {
  window.toggleFishMode = () => {
    if (IN_FISH_MODE) {
      localStorage.removeItem("mode")
    } else {
      localStorage.setItem("mode", "FISH")
    }
    window.location.href = window.location.href
  }
}

const MyInlineMath = (props: MathComponentPropsWithMath) => {
  if (IN_FISH_MODE) {
    const math = props.math
      .replaceAll("x_0", "🐟")
      .replaceAll("y_0", "🐠")
      .replaceAll("z_0", "🐡")
    return <InlineMath {...props} math={math}/>
  } else return <InlineMath {...props}/>
}

export const MemoizedInlineMath = React.memo(MyInlineMath)
