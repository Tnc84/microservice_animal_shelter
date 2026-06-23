@echo off
setlocal enabledelayedexpansion

echo ========================================
echo Building all microservices for Docker
echo ========================================

echo.
echo [1/6] Building Common Library (tnc-shared-libraries/tnc-security-lib)...
cd tnc-shared-libraries\tnc-security-lib
call mvn clean install -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build Common Library (tnc-security-lib)
    pause
    exit /b 1
)
cd ..
cd ..
echo ✓ Common Library (tnc-security-lib) built successfully

echo.
echo [2/6] Building Naming Server (Eureka)...
cd naming-server-as
call mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build Naming Server
    pause
    exit /b 1
)
cd ..
echo ✓ Naming Server built successfully

echo.
echo [3/6] Building API Gateway...
cd api-gateway-as
call mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build API Gateway
    pause
    exit /b 1
)
cd ..
echo ✓ API Gateway built successfully

echo.
echo [4/6] Building Animal Microservice...
cd micro_as_animal
call mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build Animal Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ Animal Microservice built successfully

echo.
echo [5/6] Building Shelter Microservice...
cd micro_as_shelter
call mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build Shelter Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ Shelter Microservice built successfully

echo.
echo [6/7] Building User Management Microservice...
cd micro_as_user
call mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build User Management Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ User Management Microservice built successfully

echo.
echo [7/7] Building Pet Hotel Microservice...
cd pet-hotel-microservice
call mvn clean package -DskipTests
if !errorlevel! neq 0 (
    echo ERROR: Failed to build Pet Hotel Microservice
    pause
    exit /b 1
)
cd ..
echo ✓ Pet Hotel Microservice built successfully

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