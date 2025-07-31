cp -v project/plugins.sbt plugins.sbt_TEMP

printf "Removing directories... "
rm -rf .bsp .bloop .metals project target
echo "Done."

mkdir project
mv -v plugins.sbt_TEMP project/plugins.sbt
