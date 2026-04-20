-- =====================================================
-- Animal Shelter Microservices Database Setup Script
-- =====================================================
-- This script creates databases, users, and initial schemas
-- for the Animal Shelter microservices application

-- =====================================================
-- 1. CREATE DATABASES
-- =====================================================
CREATE DATABASE IF NOT EXISTS `animal`;
CREATE DATABASE IF NOT EXISTS `shelter`;
CREATE DATABASE IF NOT EXISTS `user_management`;
CREATE DATABASE IF NOT EXISTS `pet-hotel`;

-- =====================================================
-- 2. CREATE DEDICATED USER AND GRANT PRIVILEGES
-- =====================================================
-- Create user for the application (production-ready)
CREATE USER IF NOT EXISTS 'animalshelter'@'localhost' IDENTIFIED BY 'SecurePassword123!';
CREATE USER IF NOT EXISTS 'animalshelter'@'%' IDENTIFIED BY 'SecurePassword123!';

-- Grant privileges on all databases
GRANT ALL PRIVILEGES ON `animal`.* TO 'animalshelter'@'localhost';
GRANT ALL PRIVILEGES ON `shelter`.* TO 'animalshelter'@'localhost';
GRANT ALL PRIVILEGES ON `user_management`.* TO 'animalshelter'@'localhost';
GRANT ALL PRIVILEGES ON `pet-hotel`.* TO 'animalshelter'@'localhost';

GRANT ALL PRIVILEGES ON `animal`.* TO 'animalshelter'@'%';
GRANT ALL PRIVILEGES ON `shelter`.* TO 'animalshelter'@'%';
GRANT ALL PRIVILEGES ON `user_management`.* TO 'animalshelter'@'%';
GRANT ALL PRIVILEGES ON `pet-hotel`.* TO 'animalshelter'@'%';

FLUSH PRIVILEGES;

-- =====================================================
-- 3. ANIMAL DATABASE SCHEMA
-- =====================================================
USE `animal`;

-- Animal table
CREATE TABLE IF NOT EXISTS `animal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `breed` VARCHAR(100) NOT NULL,
    `species` VARCHAR(100) NOT NULL,
    `photo` VARCHAR(500) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_animal_name` (`name`),
    INDEX `idx_animal_species` (`species`),
    INDEX `idx_animal_breed` (`breed`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 4. SHELTER DATABASE SCHEMA
-- =====================================================
USE `shelter`;

-- Shelter table
CREATE TABLE IF NOT EXISTS `shelter` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    `environment` VARCHAR(50) NULL,
    PRIMARY KEY (`id`),
    INDEX `idx_shelter_name` (`name`),
    INDEX `idx_shelter_city` (`city`),
    INDEX `idx_shelter_environment` (`environment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 5. USER MANAGEMENT DATABASE SCHEMA
-- =====================================================
USE `user_management`;

-- Users table
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` VARCHAR(20) NULL,
    `first_name` VARCHAR(50) NOT NULL,
    `last_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `phone` VARCHAR(20) NULL,
    `password` VARCHAR(255) NULL,
    `last_login_date` DATETIME NULL,
    `last_login_date_display` DATETIME NULL,
    `join_date` DATETIME NULL,
    `role` ENUM('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OWNER') NOT NULL DEFAULT 'ROLE_USER',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `is_not_locked` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    INDEX `idx_users_user_id` (`user_id`),
    INDEX `idx_users_first_name` (`first_name`),
    INDEX `idx_users_last_name` (`last_name`),
    INDEX `idx_users_role` (`role`),
    INDEX `idx_users_active` (`is_active`),
    INDEX `idx_users_join_date` (`join_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 6. INSERT INITIAL DATA (OPTIONAL)
-- =====================================================

-- Insert sample shelter data
USE `shelter`;
INSERT IGNORE INTO `shelter` (`name`, `city`, `environment`) VALUES
('Happy Paws Shelter', 'New York', 'production'),
('Safe Haven Animal Rescue', 'Los Angeles', 'production'),
('Paws & Claws Sanctuary', 'Chicago', 'production');

-- Insert sample animal data
USE `animal`;
INSERT IGNORE INTO `animal` (`name`, `breed`, `species`, `photo`) VALUES
('Buddy', 'Golden Retriever', 'Dog', 'buddy_photo.jpg'),
('Whiskers', 'Persian', 'Cat', 'whiskers_photo.jpg'),
('Charlie', 'Labrador', 'Dog', 'charlie_photo.jpg'),
('Luna', 'Siamese', 'Cat', 'luna_photo.jpg');

-- Insert sample user data (with hashed passwords - these are examples)
USE `user_management`;
INSERT IGNORE INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `phone`, `password`, `role`, `is_active`, `is_not_locked`, `join_date`) VALUES
('1234567890', 'John', 'Doe', 'john.doe@animalshelter.com', '+1234567890', '$2a$10$example_hashed_password', 'ROLE_ADMIN', TRUE, TRUE, NOW()),
('0987654321', 'Jane', 'Smith', 'jane.smith@animalshelter.com', '+0987654321', '$2a$10$example_hashed_password', 'ROLE_MANAGER', TRUE, TRUE, NOW()),
('1122334455', 'Bob', 'Johnson', 'bob.johnson@animalshelter.com', '+1122334455', '$2a$10$example_hashed_password', 'ROLE_USER', TRUE, TRUE, NOW());

-- =====================================================
-- 7. CREATE ADDITIONAL INDEXES FOR PERFORMANCE
-- =====================================================

-- Animal database indexes
USE `animal`;
CREATE INDEX IF NOT EXISTS `idx_animal_name_species` ON `animal` (`name`, `species`);
CREATE INDEX IF NOT EXISTS `idx_animal_breed_species` ON `animal` (`breed`, `species`);

-- Shelter database indexes
USE `shelter`;
CREATE INDEX IF NOT EXISTS `idx_shelter_name_city` ON `shelter` (`name`, `city`);

-- User management database indexes
USE `user_management`;
CREATE INDEX IF NOT EXISTS `idx_users_name_email` ON `users` (`first_name`, `last_name`, `email`);
CREATE INDEX IF NOT EXISTS `idx_users_role_active` ON `users` (`role`, `is_active`);

-- =====================================================
-- 8. VERIFICATION QUERIES
-- =====================================================

-- Show created databases
SHOW DATABASES;

-- Show tables in each database
SELECT 'Animal Database Tables:' as info;
USE `animal`;
SHOW TABLES;

SELECT 'Shelter Database Tables:' as info;
USE `shelter`;
SHOW TABLES;

SELECT 'User Management Database Tables:' as info;
USE `user_management`;
SHOW TABLES;

SELECT 'Pet Hotel Database Tables:' as info;
USE `pet-hotel`;
SHOW TABLES;

-- Show user privileges
SELECT 'Database Users:' as info;
SELECT User, Host FROM mysql.user WHERE User = 'animalshelter';

-- =====================================================
-- 9. SECURITY RECOMMENDATIONS
-- =====================================================
-- Note: For production environments, consider the following:
-- 1. Use stronger passwords (minimum 12 characters with special characters)
-- 2. Enable SSL/TLS connections
-- 3. Restrict user access to specific IP ranges
-- 4. Implement connection pooling
-- 5. Use environment variables for sensitive configuration
-- 6. Enable audit logging
-- 7. Regular security updates and patches
-- 8. Implement backup and recovery procedures

-- =====================================================
-- END OF SCRIPT
-- =====================================================
