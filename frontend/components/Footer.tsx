import Link from "next/link"

export default function Footer() {
  return (
    <footer className="w-full">
      <div className="max-w-screen mx-auto py-2 px-4">
        <p className="text-center text-gray-600">
          Made with <span className="text-red-500">❤️</span> by{" "}
          <Link href="https://logicbox.dk" className="text-blue-500">
            LogicBox.dk
          </Link>
          {/* &copy; {new Date().getFullYear()} All rights reserved. Made with{" "}
          <span className="text-red-500">❤️</span> by{" "}
          <Link href="LogicBox.dk">LogicBox.dk</Link> */}
        </p>
      </div>
    </footer>
  );
}
