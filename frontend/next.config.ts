import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: false,
  output: process.env.NEXT_OUTPUT_MODE === 'export' ? 'export' : undefined,
  /* config options here */
  experimental: {
    turbo: {
      rules: {
        "*.svg": {
          loaders: ["@svgr/webpack"],
          as: "*.js",
        },
      },
    },
  },
  redirects: async () => [
    {
      source: "/",
      destination: "/gallery",
      statusCode: 301
    }
  ]
};

export default nextConfig;
