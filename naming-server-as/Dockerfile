# Multi-stage build for Naming Server (Eureka)
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app

# Copy and install tnc-security-lib module first (dependency)
COPY tnc-shared-libraries/tnc-security-lib/pom.xml ./tnc-shared-libraries/tnc-security-lib/
COPY tnc-shared-libraries/tnc-security-lib/src ./tnc-shared-libraries/tnc-security-lib/src
RUN cd tnc-shared-libraries/tnc-security-lib && mvn clean install -DskipTests

# Copy service POM and source
COPY naming-server-as/pom.xml ./naming-server-as/
COPY naming-server-as/src ./naming-server-as/src

# Build the application
RUN cd naming-server-as && mvn clean package -DskipTests

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
