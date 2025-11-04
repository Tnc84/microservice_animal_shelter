@echo off
REM ============================================
REM Generate PDF from Presentation.html
REM ============================================

echo.
echo ============================================
echo Presentation PDF Generator
echo ============================================
echo.

REM Check if Node.js is installed
where node >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Node.js is not installed!
    echo.
    echo Please install Node.js from: https://nodejs.org/
    echo Then run this script again.
    echo.
    pause
    exit /b 1
)

echo [INFO] Node.js found!
echo.

REM Check if puppeteer is installed
if not exist "node_modules\puppeteer" (
    echo [INFO] Installing Puppeteer...
    call npm install puppeteer
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Failed to install Puppeteer!
        pause
        exit /b 1
    )
    echo [SUCCESS] Puppeteer installed!
    echo.
)

REM Check if Presentation.html exists
if not exist "Presentation.html" (
    echo [ERROR] Presentation.html not found!
    echo.
    pause
    exit /b 1
)

echo [INFO] Generating PDF...
echo.

REM Generate PDF
call node generate-pdf.js

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo [SUCCESS] PDF generated successfully!
    echo ============================================
    echo.
    echo Output file: Presentation.pdf
    echo.
) else (
    echo.
    echo ============================================
    echo [ERROR] PDF generation failed!
    echo ============================================
    echo.
)

pause

