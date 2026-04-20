#!/usr/bin/env bash

set -euo pipefail

echo "Starting all microservices..."

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PIDS=()

install_shared_libraries() {
  local libs_dir="$ROOT_DIR/tnc-shared-libraries"
  if [[ ! -d "$libs_dir" ]]; then
    echo "Skipping shared libraries install: directory 'tnc-shared-libraries' not found."
    return
  fi

  echo "Installing shared libraries locally..."
  (
    cd "$libs_dir"
    mvn clean install -DskipTests
  )
}

start_service() {
  local service_name="$1"
  local service_dir="$2"
  local delay_seconds="${3:-0}"

  if [[ ! -d "$ROOT_DIR/$service_dir" ]]; then
    echo "Skipping $service_name: directory '$service_dir' not found."
    return
  fi

  echo "Starting $service_name..."
  (
    cd "$ROOT_DIR/$service_dir"
    mvn spring-boot:run
  ) &
  PIDS+=("$!")

  if [[ "$delay_seconds" -gt 0 ]]; then
    sleep "$delay_seconds"
  fi
}

install_shared_libraries

start_service "Naming Server" "naming-server-as" 10
start_service "API Gateway" "api-gateway-as" 5
start_service "Animal Microservice" "micro_as_animal" 5
start_service "Shelter Microservice" "micro_as_shelter" 5
start_service "User Microservice" "micro_as_user" 5
start_service "Pet Hotel Microservice" "pet-hotel-microservice"

echo "All microservices started!"
echo "Running PIDs: ${PIDS[*]}"
echo "Press Ctrl+C to stop this script. Services will continue running unless stopped manually."

wait
