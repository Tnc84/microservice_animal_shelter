@echo off
echo ========================================
echo Building Docker Images for Microservices
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
echo Step 2: Building API Gateway Docker image...
docker build -f api-gateway-as/Dockerfile -t animal-shelter-api-gateway:latest .
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build API Gateway Docker image
    pause
    exit /b 1
)

echo.
echo Step 3: Building User Service Docker image...
docker build -f micro_as_user/Dockerfile -t animal-shelter-user-service:latest .
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build User Service Docker image
    pause
    exit /b 1
)

echo.
echo Step 4: Building Animal Service Docker image...
docker build -f micro_as_animal/Dockerfile -t animal-shelter-animal-service:latest .
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build Animal Service Docker image
    pause
    exit /b 1
)

echo.
echo Step 5: Building Shelter Service Docker image...
docker build -f micro_as_shelter/Dockerfile -t animal-shelter-shelter-service:latest .
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build Shelter Service Docker image
    pause
    exit /b 1
)

echo.
echo Step 6: Building Naming Server Docker image...
docker build -f naming-server-as/Dockerfile -t animal-shelter-naming-server:latest .
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build Naming Server Docker image
    pause
    exit /b 1
)

echo.
echo Step 7: Building Pet Hotel Service Docker image...
docker build -f pet-hotel-microservice/Dockerfile -t animal-shelter-pet-hotel-service:latest .
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to build Pet Hotel Service Docker image
    pause
    exit /b 1
)

echo.
echo ========================================
echo All Docker images built successfully!
echo ========================================
echo.
echo Available images:
echo   - animal-shelter-api-gateway:latest
echo   - animal-shelter-user-service:latest
echo   - animal-shelter-animal-service:latest
echo   - animal-shelter-shelter-service:latest
echo   - animal-shelter-naming-server:latest
echo   - animal-shelter-pet-hotel-service:latest
echo.
echo To run with docker-compose:
echo   docker-compose up -d
echo.
pause
