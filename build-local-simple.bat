@echo off
echo ========================================
echo Building Animal Shelter Microservices
echo (Simple Build - No OWASP/SonarQube)
echo ========================================

echo.
echo Step 1: Building tnc-security-lib library...
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
echo Step 2: Building micro_as_user service...
cd micro_as_user
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build micro_as_user
    pause
    exit /b 1
)
cd ..

echo.
echo Step 3: Building micro_as_animal service...
cd micro_as_animal
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build micro_as_animal
    pause
    exit /b 1
)
cd ..

echo.
echo Step 4: Building micro_as_shelter service...
cd micro_as_shelter
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build micro_as_shelter
    pause
    exit /b 1
)
cd ..

echo.
echo Step 5: Building api-gateway-as service...
cd api-gateway-as
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build api-gateway-as
    pause
    exit /b 1
)
cd ..

echo.
echo Step 6: Building naming-server-as service...
cd naming-server-as
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build naming-server-as
    pause
    exit /b 1
)
cd ..

echo.
echo ========================================
echo All microservices built successfully!
echo ========================================
echo.
echo To run tests:
echo   cd micro_as_user && mvn test
echo   cd micro_as_animal && mvn test
echo   cd micro_as_shelter && mvn test
echo   cd api-gateway-as && mvn test
echo   cd naming-server-as && mvn test
echo.
echo To run with code coverage:
echo   cd micro_as_user && mvn test jacoco:report
echo   cd micro_as_animal && mvn test jacoco:report
echo   cd micro_as_shelter && mvn test jacoco:report
echo   cd api-gateway-as && mvn test jacoco:report
echo   cd naming-server-as && mvn test jacoco:report
echo.
echo To run OWASP dependency checks (optional):
echo   cd micro_as_user && mvn org.owasp:dependency-check-maven:check -Ddependency-check.offline=true
echo   cd micro_as_animal && mvn org.owasp:dependency-check-maven:check -Ddependency-check.offline=true
echo   cd micro_as_shelter && mvn org.owasp:dependency-check-maven:check -Ddependency-check.offline=true
echo   cd api-gateway-as && mvn org.owasp:dependency-check-maven:check -Ddependency-check.offline=true
echo   cd naming-server-as && mvn org.owasp:dependency-check-maven:check -Ddependency-check.offline=true
echo.
pause
