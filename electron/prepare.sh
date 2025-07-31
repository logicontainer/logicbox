#!/usr/bin/env bash

cd ../backend
echo "------------ COMPILING BACKEND"

sbt fullLinkJS
cd ..


echo "------------ MOVING OUTPUT FILE TO FRONTEND"

SBTOUTPUTFILE="backend/target/scala-3.4.3/root-opt/main.js"
DSTFILE="frontend/public/logicbox_backend.js"

echo "\
// This file was generated during a build of this commit:
// - current commit hash: $(git rev-parse HEAD)
// - origin URL: $(git remote get-url origin)
// - link: $(git remote get-url origin | sed -E "s/(git@|https:\/\/)(github.com|gitlab.com)[:\/](.*)\.git/https:\/\/\2\/\3\/commit\/$(git rev-parse HEAD)/")" \
  > $DSTFILE

cat $SBTOUTPUTFILE >> $DSTFILE

echo "------------ RUNNING pnpm build"
NEXT_OUTPUT_MODE=export pnpm build

cd ..
echo "------------ COPYING pnpm OUTPUT TO electron/app"
cp -rv frontend/out electron/app

cd electron
