# Multi-stage build for Naming Server (Eureka)
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
COPY naming-server-as/pom.xml ./naming-server-as/
COPY naming-server-as/src ./naming-server-as/src

# Build the application
RUN cd naming-server-as && mvn clean package -DskipTests -T 4

# Runtime stage
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/naming-server-as/target/*.jar app.jar

# Expose the port
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
