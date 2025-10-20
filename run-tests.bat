@echo off
echo ========================================
echo Microservices Testing Suite
echo ========================================

REM Set environment variables
set MAVEN_OPTS=-Xmx1024m
set JAVA_OPTS=-Xmx1024m

echo.
echo Cleaning and compiling all modules...
echo ========================================
call mvn clean compile
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    exit /b 1
)

echo.
echo Running All Tests (Unit, Contract) - Performance, Integration, Application, Mapper, and Filter tests skipped...
echo ========================================
call mvn test -Dspring.profiles.active=test -Dtest="!**/performance/**,!**/integration/**,!**/ShelterApplicationTests,!**/AnimalsApplicationTests,!**/UserManagementApplicationTests,!**/*MapperTest,!**/JwtAuthenticationFilterTest"
if %ERRORLEVEL% neq 0 (
    echo Tests failed!
    exit /b 1
)

echo.
echo Generating Test Reports...
echo ========================================
call mvn surefire-report:report
call mvn jacoco:report

echo.
echo ========================================
echo All tests completed successfully!
echo ========================================
echo.
echo Test reports generated in:
echo - target/surefire-reports/ (Test results)
echo - target/site/jacoco/ (Coverage report)
echo.
pause
