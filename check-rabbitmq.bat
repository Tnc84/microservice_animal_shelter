@echo off
echo Checking RabbitMQ Status...
echo.

echo 1. Checking Docker container...
docker ps --filter "name=rabbitmq" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo.

echo 2. Testing RabbitMQ connection...
docker exec animal_shelter_rabbitmq rabbitmq-diagnostics ping 2>nul
if %errorlevel% equ 0 (
    echo [OK] RabbitMQ is running and responding
) else (
    echo [ERROR] RabbitMQ is not responding or container is not running
)
echo.

echo 3. Management UI available at: http://localhost:15672
echo    Username: admin
echo    Password: admin123
echo.

pause
