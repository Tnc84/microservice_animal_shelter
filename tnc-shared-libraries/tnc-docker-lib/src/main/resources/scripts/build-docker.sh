#!/bin/bash

# Docker build script for TNC microservices
# Usage: ./build-docker.sh <app-name> <jar-file> <port>

set -e

# Check if required parameters are provided
if [ $# -ne 3 ]; then
    echo "Usage: $0 <app-name> <jar-file> <port>"
    echo "Example: $0 micro-as-animal animals-1.0.0.jar 8080"
    exit 1
fi

APP_NAME=$1
JAR_FILE=$2
PORT=$3

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Building Docker image for $APP_NAME...${NC}"

# Check if Dockerfile exists
if [ ! -f "Dockerfile" ]; then
    echo -e "${RED}Error: Dockerfile not found in current directory${NC}"
    exit 1
fi

# Check if JAR file exists
if [ ! -f "target/$JAR_FILE" ]; then
    echo -e "${RED}Error: JAR file target/$JAR_FILE not found${NC}"
    echo "Please build the application first with: mvn clean package"
    exit 1
fi

# Build the Docker image
echo -e "${YELLOW}Building Docker image: $APP_NAME:latest${NC}"
docker build \
    --build-arg APP_NAME=$APP_NAME \
    --build-arg JAR_FILE=$JAR_FILE \
    --build-arg PORT=$PORT \
    -t $APP_NAME:latest \
    .

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Docker image built successfully: $APP_NAME:latest${NC}"
    echo -e "${GREEN}Image size: $(docker images $APP_NAME:latest --format "table {{.Size}}")${NC}"
else
    echo -e "${RED}❌ Docker build failed${NC}"
    exit 1
fi

# Optional: Run the container
read -p "Do you want to run the container? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Running container on port $PORT...${NC}"
    docker run -d -p $PORT:$PORT --name $APP_NAME-container $APP_NAME:latest
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Container started successfully${NC}"
        echo -e "${GREEN}Application available at: http://localhost:$PORT${NC}"
        echo -e "${GREEN}Health check: http://localhost:$PORT/actuator/health${NC}"
        echo -e "${GREEN}To stop: docker stop $APP_NAME-container${NC}"
        echo -e "${GREEN}To remove: docker rm $APP_NAME-container${NC}"
    else
        echo -e "${RED}❌ Failed to start container${NC}"
        exit 1
    fi
fi
