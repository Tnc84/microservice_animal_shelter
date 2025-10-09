# Multi-stage build for User Management Microservice
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8091

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8091/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
