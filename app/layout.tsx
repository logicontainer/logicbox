import "./globals.css";

import { Geist, Geist_Mono } from "next/font/google";

import LogicBoxSVG from "@/app/logicbox-icon.svg";
import type { Metadata } from "next";
import { Providers } from "@/components/Providers";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "LogicBox",
  description: "Create and validate logical proofs interactively",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased relative`}
      >
        <div className="fixed top-0 left-0 w-16 h-16 m-4 lg:w-24 lg:h-24">
          <LogicBoxSVG />
        </div>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
