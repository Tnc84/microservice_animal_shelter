# Multi-stage build for Animal Microservice
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy and install tnc-security-lib module first (dependency)
COPY tnc-shared-libraries/tnc-security-lib/pom.xml ./tnc-shared-libraries/tnc-security-lib/
COPY tnc-shared-libraries/tnc-security-lib/src ./tnc-shared-libraries/tnc-security-lib/src
RUN cd tnc-shared-libraries/tnc-security-lib && mvn clean install -DskipTests

# Copy service POM and source
COPY micro_as_animal/pom.xml ./micro_as_animal/
COPY micro_as_animal/src ./micro_as_animal/src

# Build the application
RUN cd micro_as_animal && mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/micro_as_animal/target/*.jar app.jar

# Expose the port
EXPOSE 8093

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8093/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
