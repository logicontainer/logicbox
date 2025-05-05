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
  async redirects() {
    return [
      {
        source: "/",
        destination: "/proofs",
        permanent: true,
      },
    ];
  },
};

export default nextConfig;
