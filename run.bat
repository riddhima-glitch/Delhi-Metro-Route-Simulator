@echo off
REM =======================================================
REM Delhi Metro Route & Schedule Simulator
REM Windows Build & Run Script
REM =======================================================

echo.
echo ===================================================
echo   Delhi Metro Route and Schedule Simulator
echo   Compiling Java source files...
echo ===================================================
echo.

if not exist bin (
    mkdir bin
)

javac -encoding UTF-8 -d bin src\metro\*.java test\metro\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please check Java installation.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.
echo Starting Delhi Metro Simulator...
echo.

java -cp bin metro.Main

pause
