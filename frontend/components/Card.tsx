import { cn } from "@/lib/utils";

export default function Card({
  children,
  className = "",
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div
      className={cn(
        `bg-white shadow-md rounded-lg p-4 border border-gray-200`,
        className
      )}
    >
      {children}
    </div>
  );
}
