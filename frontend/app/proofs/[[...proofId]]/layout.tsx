import NavigationSidebar from "@/components/NavigationSidebar";

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="grid grid-cols-[300px_auto] border">
      <NavigationSidebar />
      <div className="bg-white flex flex-col h-full overflow-auto">
        <div className="flex-1">{children}</div>
      </div>
    </div>
  );
}
