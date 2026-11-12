#!/bin/sh
# Builds (if needed) and runs the application. Adds every jar in lib/ to the classpath.
cd "$(dirname "$0")" || exit 1
[ -f out/com/srms/Main.class ] || ./build.sh || exit 1
java -cp "out:lib/*" com.srms.Main
