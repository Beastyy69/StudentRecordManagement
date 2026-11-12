@echo off
REM Compiles all Java sources into the "out" folder.
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt
set RESULT=%ERRORLEVEL%
del sources.txt
if %RESULT% neq 0 (
    echo.
    echo Build FAILED.
    pause
    exit /b %RESULT%
)
echo Build successful. Run with run.bat
