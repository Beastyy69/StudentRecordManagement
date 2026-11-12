#!/bin/sh
# Compiles all Java sources into the "out" folder.
cd "$(dirname "$0")" || exit 1
rm -rf out
mkdir -p out
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
RESULT=$?
rm -f sources.txt
if [ $RESULT -ne 0 ]; then
    echo "Build FAILED."
    exit $RESULT
fi
echo "Build successful. Run with ./run.sh"
