@echo off
echo ========================================
echo Running End-to-End Tests
echo (Animal Shelter Microservices)
echo ========================================

echo.
echo Step 1: Building tnc-security-lib library first...
cd tnc-shared-libraries\tnc-security-lib
call mvn clean install -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build tnc-security-lib
    pause
    exit /b 1
)
cd ..
cd ..

echo.
echo Step 2: Running E2E tests for micro_as_user...
cd micro_as_user
call mvn clean test -Dspring.profiles.active=staging
if %ERRORLEVEL% neq 0 (
    echo WARNING: E2E tests failed for micro_as_user, continuing...
)
cd ..

echo.
echo Step 3: Running E2E tests for micro_as_animal...
cd micro_as_animal
call mvn clean test -Dspring.profiles.active=staging
if %ERRORLEVEL% neq 0 (
    echo WARNING: E2E tests failed for micro_as_animal, continuing...
)
cd ..

echo.
echo Step 4: Running E2E tests for micro_as_shelter...
cd micro_as_shelter
call mvn clean test -Dspring.profiles.active=staging
if %ERRORLEVEL% neq 0 (
    echo WARNING: E2E tests failed for micro_as_shelter, continuing...
)
cd ..

echo.
echo Step 5: Running E2E tests for api-gateway-as...
cd api-gateway-as
call mvn clean test -Dspring.profiles.active=staging
if %ERRORLEVEL% neq 0 (
    echo WARNING: E2E tests failed for api-gateway-as, continuing...
)
cd ..

echo.
echo Step 6: Running E2E tests for naming-server-as...
cd naming-server-as
call mvn clean test -Dspring.profiles.active=staging
if %ERRORLEVEL% neq 0 (
    echo WARNING: E2E tests failed for naming-server-as, continuing...
)
cd ..

echo.
echo ========================================
echo E2E test execution completed!
echo ========================================
echo.
echo Test reports generated in:
echo   - micro_as_user/target/surefire-reports/
echo   - micro_as_animal/target/surefire-reports/
echo   - micro_as_shelter/target/surefire-reports/
echo   - api-gateway-as/target/surefire-reports/
echo   - naming-server-as/target/surefire-reports/
echo.
echo Note: E2E tests require all services to be running.
echo Make sure to start all microservices before running E2E tests.
echo.
echo To start all services:
echo   docker-compose up -d
echo.
pause
