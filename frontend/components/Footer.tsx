import Link from "next/link"

export default function Footer() {
  return (
    <footer className="w-full">
      <div className="max-w-screen mx-auto py-2 px-4">
        <p className="text-center text-gray-600">
          Made by{" "}
          <Link href="https://kasperskov.dev" className="text-blue-500">
            Kasper Skov Hansen
          </Link>
          {" "}and{" "}
          <Link href="https://github.com/felix-berg" className="text-blue-500">
            Felix Berg
          </Link>
          {" "} @ Aarhus University
          {/* &copy; {new Date().getFullYear()} All rights reserved. Made with{" "}
          <span className="text-red-500">❤️</span> by{" "}
          <Link href="LogicBox.dk">LogicBox.dk</Link> */}
        </p>
      </div>
    </footer>
  );
}
