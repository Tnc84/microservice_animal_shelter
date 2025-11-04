# Multi-stage build for API Gateway
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy and install tnc-security-lib module first (dependency)
COPY tnc-shared-libraries/tnc-security-lib/pom.xml ./tnc-shared-libraries/tnc-security-lib/
COPY tnc-shared-libraries/tnc-security-lib/src ./tnc-shared-libraries/tnc-security-lib/src
RUN cd tnc-shared-libraries/tnc-security-lib && mvn clean install -DskipTests

# Copy service POM and source
COPY api-gateway-as/pom.xml ./api-gateway-as/
COPY api-gateway-as/src ./api-gateway-as/src

# Build the application
RUN cd api-gateway-as && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-slim

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/api-gateway-as/target/*.jar app.jar

# Expose the port
EXPOSE 8765

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8765/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
