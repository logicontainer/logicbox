import { cn } from "@/lib/utils";

export default function Card({
  ref,
  children,
  className = "",
}: {
  ref?: React.Ref<HTMLDivElement>;
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div
      ref={ref}
      className={cn(
        `bg-white shadow-md rounded-lg p-4 border border-gray-200`,
        className,
      )}
    >
      {children}
    </div>
  );
}
