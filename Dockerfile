FROM sbtscala/scala-sbt:graalvm-ce-22.3.3-b1-java17_1.12.6_3.8.2 AS sbt-scalajs
WORKDIR /sbtdir
COPY backend .
RUN sbt fullLinkJS

FROM node:20-slim AS node-base
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
ENV CI=true
RUN corepack enable
WORKDIR /app

FROM node-base AS deps
COPY frontend/pnpm-lock.yaml frontend/package.json frontend/next.config.ts frontend/tsconfig.json ./
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile

FROM node-base AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY --from=deps /app/next.config.ts ./
COPY frontend/ .

ENV NEXT_TELEMETRY_DISABLED 1

RUN pnpm run build
RUN ls -la .
RUN ls -la .next
RUN pnpm prune --prod

FROM node-base AS runtime
ENV NODE_ENV=production
WORKDIR /app

COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static
COPY --from=build /app/public ./public
COPY --from=sbt-scalajs /sbtdir/target/scala-3.4.3/root-opt/main.js ./public/logicbox_backend.js

EXPOSE 3000
# In standalone mode, Next.js generates a server.js file
CMD ["node", "server.js"]
