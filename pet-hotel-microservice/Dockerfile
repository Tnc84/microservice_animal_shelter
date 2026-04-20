# Multi-stage build for Pet Hotel Microservice
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy parent POM and all shared libraries
COPY tnc-shared-libraries/pom.xml ./tnc-shared-libraries/
COPY tnc-shared-libraries/tnc-security-lib ./tnc-shared-libraries/tnc-security-lib
COPY tnc-shared-libraries/tnc-resilience-lib ./tnc-shared-libraries/tnc-resilience-lib
COPY tnc-shared-libraries/tnc-swagger-lib ./tnc-shared-libraries/tnc-swagger-lib
COPY tnc-shared-libraries/tnc-events-lib ./tnc-shared-libraries/tnc-events-lib
COPY tnc-shared-libraries/tnc-docker-lib ./tnc-shared-libraries/tnc-docker-lib

# Build all shared libraries first
RUN cd tnc-shared-libraries && mvn clean install -DskipTests -T 4

# Copy service POM and source
COPY pet-hotel-microservice/pom.xml ./pet-hotel-microservice/
COPY pet-hotel-microservice/src ./pet-hotel-microservice/src

# Build the application
RUN cd pet-hotel-microservice && mvn clean package -DskipTests -T 4

# Runtime stage
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Install curl for container healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built jar from build stage
COPY --from=build /app/pet-hotel-microservice/target/*.jar app.jar

# Expose the port
EXPOSE 8096

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8096/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
