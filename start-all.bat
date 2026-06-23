@echo off
echo Starting all microservices...

start "Naming Server" cmd /k "cd naming-server-as && mvn spring-boot:run"
timeout /t 10 /nobreak > nul

start "API Gateway" cmd /k "cd api-gateway-as && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

start "Animal Microservice" cmd /k "cd micro_as_animal && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

start "Shelter Microservice" cmd /k "cd micro_as_shelter && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

start "User Microservice" cmd /k "cd micro_as_user && mvn spring-boot:run"
timeout /t 5 /nobreak > nul

start "Pet Hotel Microservice" cmd /k "cd pet-hotel-microservice && mvn spring-boot:run"

echo All microservices started!
pause