import "./globals.css";

import { Geist, Geist_Mono } from "next/font/google";

import { Analytics } from "@vercel/analytics/next";
import type { Metadata } from "next";
import { Providers } from "@/components/Providers";
import Script from "next/script";

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
        <Providers>{children}</Providers>
        <Analytics />
      </body>
    </html>
  );
}
