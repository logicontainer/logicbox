import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function download(
  content: string,
  fileName: string,
  contentType: string,
) {
  const a = document.createElement("a");
  const file = new Blob([content], { type: contentType });
  a.href = URL.createObjectURL(file);
  a.download = fileName;
  a.click();
}

export function isOnLowerHalf(e: React.MouseEvent): boolean {
  const { top, height } = e.currentTarget.getBoundingClientRect()
  return (e.pageY - top) >= height / 2
}
