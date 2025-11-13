@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Microservices Testing Suite
echo ========================================

REM Set environment variables
set MAVEN_OPTS=-Xmx1024m
set JAVA_OPTS=-Xmx1024m

echo.
echo [Step 1/3] Building shared libraries first...
echo ========================================
cd tnc-shared-libraries
call mvn clean install -DskipTests
if !ERRORLEVEL! neq 0 (
    echo ERROR: Failed to build shared libraries
    cd ..
    exit /b 1
)
cd ..
echo ✓ Shared libraries built successfully
echo.

echo [Step 2/3] Running tests for all microservices...
echo ========================================
set FAILED=0

REM Test each microservice
for %%s in (api-gateway-as micro_as_animal micro_as_shelter micro_as_user naming-server-as) do (
    echo.
    echo Testing %%s...
    echo ----------------------------------------
    cd %%s
    REM Run tests with exclusions, but don't fail if no tests match the pattern
    call mvn clean test -Dspring.profiles.active=test -Dsonar.skip=true -Ddependency-check.skip=true -Dtest="!**/performance/**,!**/integration/**,!**/*MapperTest" -Dsurefire.failIfNoSpecifiedTests=false
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Tests failed in %%s
        set FAILED=1
    ) else (
        echo ✓ %%s tests passed
    )
    cd ..
)

if !FAILED! equ 1 (
    echo.
    echo ========================================
    echo Some tests failed! Check the output above.
    echo ========================================
    exit /b 1
)

echo.
echo [Step 3/3] Generating test reports...
echo ========================================
for %%s in (api-gateway-as micro_as_animal micro_as_shelter micro_as_user naming-server-as) do (
    echo Generating reports for %%s...
    cd %%s
    call mvn surefire-report:report jacoco:report -DskipTests
    cd ..
)

echo.
echo ========================================
echo All tests completed successfully!
echo ========================================
echo.
echo Test reports generated in each service's target directory:
echo - target/surefire-reports/ (Test results)
echo - target/site/jacoco/ (Coverage report)
echo.
pause
