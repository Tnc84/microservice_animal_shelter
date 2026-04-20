@echo off
echo ========================================
echo Running OWASP Dependency Checks
echo (Offline Mode - No Network Required)
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

echo.
echo Step 2: Running OWASP check for tnc-security-lib...
cd tnc-shared-libraries\tnc-security-lib
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for tnc-security-lib, continuing...
)
cd ..

echo.
echo Step 3: Running OWASP check for micro_as_user...
cd micro_as_user
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for micro_as_user, continuing...
)
cd ..

echo.
echo Step 4: Running OWASP check for micro_as_animal...
cd micro_as_animal
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for micro_as_animal, continuing...
)
cd ..

echo.
echo Step 5: Running OWASP check for micro_as_shelter...
cd micro_as_shelter
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for micro_as_shelter, continuing...
)
cd ..

echo.
echo Step 6: Running OWASP check for api-gateway-as...
cd api-gateway-as
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for api-gateway-as, continuing...
)
cd ..

echo.
echo Step 7: Running OWASP check for naming-server-as...
cd naming-server-as
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for naming-server-as, continuing...
)
cd ..

echo.
echo Step 8: Running OWASP check for pet-hotel-microservice...
cd pet-hotel-microservice
call mvn org.owasp:dependency-check-maven:check \
  -Dformat=ALL \
  -Dformat=HTML \
  -Dformat=JSON \
  -Ddependency-check.offline=true \
  -Ddependency-check.retryCount=3 \
  -Ddependency-check.connectionTimeout=30000 \
  -Ddependency-check.readTimeout=30000
if %ERRORLEVEL% neq 0 (
    echo WARNING: OWASP check failed for pet-hotel-microservice, continuing...
)
cd ..

echo.
echo ========================================
echo OWASP dependency checks completed!
echo ========================================
echo.
echo Reports generated in:
echo   - tnc-shared-libraries/tnc-security-lib/target/dependency-check-report.html
echo   - micro_as_user/target/dependency-check-report.html
echo   - micro_as_animal/target/dependency-check-report.html
echo   - micro_as_shelter/target/dependency-check-report.html
echo   - api-gateway-as/target/dependency-check-report.html
echo   - naming-server-as/target/dependency-check-report.html
echo   - pet-hotel-microservice/target/dependency-check-report.html
echo.
echo Note: Offline mode uses cached vulnerability data.
echo For latest data, run without offline mode when network is available.
echo.
pause
