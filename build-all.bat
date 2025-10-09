@echo off
echo ========================================
echo Building all microservices for Docker
echo ========================================

echo.
echo [1/5] Building Naming Server (Eureka)...
cd naming-server-as
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to build Naming Server
    pause
    exit /b 1
)
cd ..
echo ✓ Naming Server built successfully

echo.
echo [2/5] Building API Gateway...
cd api-gateway-as
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to build API Gateway
    pause
    exit /b 1
)
cd ..
echo ✓ API Gateway built successfully

echo.
echo [3/5] Building Animal Microservice...
cd micro_as_animal
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to build Animal Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ Animal Microservice built successfully

echo.
echo [4/5] Building Shelter Microservice...
cd micro_as_shelter
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to build Shelter Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ Shelter Microservice built successfully

echo.
echo [5/5] Building User Management Microservice...
cd micro_as_user
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Failed to build User Management Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ User Management Microservice built successfully

echo.
echo ========================================
echo All microservices built successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Run: docker-compose build
echo 2. Run: docker-compose up -d
echo.
pause
