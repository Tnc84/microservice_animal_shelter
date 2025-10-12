@echo off
echo ========================================
echo Microservices Testing Suite
echo ========================================

REM Set environment variables
set MAVEN_OPTS=-Xmx1024m
set JAVA_OPTS=-Xmx1024m

echo.
echo 1. Running Unit Tests...
echo ========================================
call mvn clean test -Dtest=*Test -Dspring.profiles.active=test
if %ERRORLEVEL% neq 0 (
    echo Unit tests failed!
    exit /b 1
)

echo.
echo 2. Running Integration Tests...
echo ========================================
call mvn test -Dtest=*IntegrationTest -Dspring.profiles.active=test
if %ERRORLEVEL% neq 0 (
    echo Integration tests failed!
    exit /b 1
)

echo.
echo 3. Running Performance Tests...
echo ========================================
call mvn test -Dtest=*PerformanceTest -Dspring.profiles.active=test
if %ERRORLEVEL% neq 0 (
    echo Performance tests failed!
    exit /b 1
)

echo.
echo 4. Running Contract Tests...
echo ========================================
call mvn test -Dtest=*ContractTest -Dspring.profiles.active=test
if %ERRORLEVEL% neq 0 (
    echo Contract tests failed!
    exit /b 1
)

echo.
echo 5. Generating Test Reports...
echo ========================================
call mvn surefire-report:report
call mvn jacoco:report

echo.
echo 6. Running Security Scan...
echo ========================================
call mvn dependency-check:check

echo.
echo ========================================
echo All tests completed successfully!
echo ========================================
echo.
echo Test reports generated in:
echo - target/surefire-reports/ (Test results)
echo - target/site/jacoco/ (Coverage report)
echo - target/dependency-check-report.html (Security report)
echo.
pause
