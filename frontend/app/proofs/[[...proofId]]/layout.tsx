import ContextSidebar from "@/components/ContextSidebar";
import Footer from "@/components/Footer";

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="h-screen max-w-screen grid lg:grid-cols-[400px_1fr] xl:grid-cols-[600px_1fr]">
      <div className="overflow-auto relative">
        <ContextSidebar />
      </div>
      <div className="overflow-auto relative">{children}</div>
      <div className="sm:hidden">
        <Footer />
      </div>
    </div>
  );
}
