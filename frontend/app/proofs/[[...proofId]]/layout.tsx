import ContextSidebar from "@/components/ContextSidebar";
import Footer from "@/components/Footer";
import NavigationSidebar from "@/components/NavigationSidebar";

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="max-w-screen grid lg:grid-cols-[200px_1fr_400px]">
      <NavigationSidebar />
      <div className="overflow-auto relative">{children}</div>
      <ContextSidebar />
      <div className="sm:hidden">
        <Footer />
      </div>
    </div>
  );
}
