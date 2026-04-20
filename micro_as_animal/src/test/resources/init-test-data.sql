-- Test data initialization script for TestContainers
-- This script runs when the MySQL container starts

-- Create test database if not exists
CREATE DATABASE IF NOT EXISTS test_animal_shelter;

-- Use the test database
USE test_animal_shelter;

-- Create test tables (if not using JPA auto-creation)
-- The actual table creation will be handled by JPA/Hibernate
-- This file is mainly for any additional test data setup

-- Insert any required test data
-- INSERT INTO animals (name, species, breed) VALUES ('Test Animal', 'Dog', 'Test Breed');
