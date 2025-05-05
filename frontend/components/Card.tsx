export default function Card({
  children,
  className = "",
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div
      className={`bg-white shadow-md rounded-lg p-4 border border-gray-200 ${className}`}
    >
      {children}
    </div>
  );
}
