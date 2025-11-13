# Multi-stage build for User Management Microservice
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy parent POM and all shared libraries
COPY tnc-shared-libraries/pom.xml ./tnc-shared-libraries/
COPY tnc-shared-libraries/tnc-security-lib ./tnc-shared-libraries/tnc-security-lib
COPY tnc-shared-libraries/tnc-resilience-lib ./tnc-shared-libraries/tnc-resilience-lib
COPY tnc-shared-libraries/tnc-swagger-lib ./tnc-shared-libraries/tnc-swagger-lib
COPY tnc-shared-libraries/tnc-docker-lib ./tnc-shared-libraries/tnc-docker-lib

# Build all shared libraries first
RUN cd tnc-shared-libraries && mvn clean install -DskipTests -T 4

# Copy service POM and source
COPY micro_as_user/pom.xml ./micro_as_user/
COPY micro_as_user/src ./micro_as_user/src

# Build the application
RUN cd micro_as_user && mvn clean package -DskipTests -T 4

# Runtime stage
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/micro_as_user/target/*.jar app.jar

# Expose the port
EXPOSE 8091

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8091/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
