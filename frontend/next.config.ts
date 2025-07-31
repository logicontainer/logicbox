import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: false,
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
