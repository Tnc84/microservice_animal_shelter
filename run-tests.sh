#!/bin/bash

echo "========================================"
echo "Microservices Testing Suite"
echo "========================================"

# Set environment variables
export MAVEN_OPTS="-Xmx1024m"
export JAVA_OPTS="-Xmx1024m"

echo ""
echo "1. Running Unit Tests..."
echo "========================================"
mvn clean test -Dtest=*Test -Dspring.profiles.active=test
if [ $? -ne 0 ]; then
    echo "Unit tests failed!"
    exit 1
fi

echo ""
echo "2. Running Integration Tests..."
echo "========================================"
mvn test -Dtest=*IntegrationTest -Dspring.profiles.active=test
if [ $? -ne 0 ]; then
    echo "Integration tests failed!"
    exit 1
fi

echo ""
echo "3. Running Performance Tests..."
echo "========================================"
mvn test -Dtest=*PerformanceTest -Dspring.profiles.active=test
if [ $? -ne 0 ]; then
    echo "Performance tests failed!"
    exit 1
fi

echo ""
echo "4. Running Contract Tests..."
echo "========================================"
mvn test -Dtest=*ContractTest -Dspring.profiles.active=test
if [ $? -ne 0 ]; then
    echo "Contract tests failed!"
    exit 1
fi

echo ""
echo "5. Generating Test Reports..."
echo "========================================"
mvn surefire-report:report
mvn jacoco:report

echo ""
echo "6. Running Security Scan..."
echo "========================================"
mvn dependency-check:check

echo ""
echo "========================================"
echo "All tests completed successfully!"
echo "========================================"
echo ""
echo "Test reports generated in:"
echo "- target/surefire-reports/ (Test results)"
echo "- target/site/jacoco/ (Coverage report)"
echo "- target/dependency-check-report.html (Security report)"
echo ""
