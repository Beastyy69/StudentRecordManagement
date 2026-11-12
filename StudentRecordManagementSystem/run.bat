@echo off
REM Builds (if needed) and runs the application. Adds every jar in lib\ to the classpath.
cd /d "%~dp0"
if not exist out\com\srms\Main.class call build.bat
java -cp "out;lib\*" com.srms.Main
