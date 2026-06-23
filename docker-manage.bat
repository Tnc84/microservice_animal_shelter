@echo off
:menu
cls
echo ========================================
echo    Animal Shelter Microservices
echo         Docker Management
echo ========================================
echo.
echo 1. Build all microservices (Maven)
echo 2. Build Docker images
echo 3. Start all services
echo 4. Stop all services
echo 5. View logs
echo 6. Clean up (remove containers and images)
echo 7. Restart all services
echo 8. Exit
echo.
set /p choice="Enter your choice (1-8): "

if "%choice%"=="1" goto build_maven
if "%choice%"=="2" goto build_docker
if "%choice%"=="3" goto start_services
if "%choice%"=="4" goto stop_services
if "%choice%"=="5" goto view_logs
if "%choice%"=="6" goto clean_up
if "%choice%"=="7" goto restart_services
if "%choice%"=="8" goto exit
goto menu

:build_maven
echo.
echo Building all microservices with Maven...
call build-all.bat
pause
goto menu

:build_docker
echo.
echo Building Docker images...
docker-compose build
if %errorlevel% neq 0 (
    echo ERROR: Failed to build Docker images
    pause
    goto menu
)
echo ✓ Docker images built successfully
pause
goto menu

:start_services
echo.
echo Starting all services...
docker-compose up -d
if %errorlevel% neq 0 (
    echo ERROR: Failed to start services
    pause
    goto menu
)
echo ✓ All services started successfully
echo.
echo Services available at:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8765
echo - Animal Service: http://localhost:8093
echo - Shelter Service: http://localhost:8092
echo - User Service: http://localhost:8091
echo - Pet Hotel Service: http://localhost:8096
pause
goto menu

:stop_services
echo.
echo Stopping all services...
docker-compose down
echo ✓ All services stopped
pause
goto menu

:view_logs
echo.
echo Viewing logs (Press Ctrl+C to exit)...
docker-compose logs -f
pause
goto menu

:clean_up
echo.
echo Cleaning up Docker resources...
docker-compose down -v
docker system prune -f
echo ✓ Cleanup completed
pause
goto menu

:restart_services
echo.
echo Restarting all services...
docker-compose down
docker-compose up -d
echo ✓ All services restarted
pause
goto menu

:exit
echo.
echo Goodbye!
exit /b 0
