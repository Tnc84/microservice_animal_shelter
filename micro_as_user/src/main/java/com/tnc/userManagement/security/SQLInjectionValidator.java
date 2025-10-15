package com.tnc.userManagement.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

/**
 * SQL Injection Validator for User Management Microservice
 * Provides comprehensive SQL injection detection and prevention
 */
@Slf4j
@Component
public class SQLInjectionValidator {

    // SQL injection patterns
    private static final List<Pattern> SQL_INJECTION_PATTERNS = Arrays.asList(
        // Basic SQL keywords
        Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s+"),
        Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s*\\("),
        
        // Boolean-based blind SQL injection
        Pattern.compile("(?i)'\\s*(or|and)\\s*'"),
        Pattern.compile("(?i)'\\s*(or|and)\\s*1\\s*=\\s*1"),
        Pattern.compile("(?i)'\\s*(or|and)\\s*'1'\\s*=\\s*'1"),
        
        // Time-based blind SQL injection
        Pattern.compile("(?i)waitfor\\s+delay"),
        Pattern.compile("(?i)benchmark\\s*\\("),
        Pattern.compile("(?i)sleep\\s*\\("),
        
        // Comment-based injection
        Pattern.compile("(?i)--"),
        Pattern.compile("(?i)/\\*.*?\\*/"),
        Pattern.compile("(?i)#"),
        
        // Function-based injection
        Pattern.compile("(?i)substring\\s*\\("),
        Pattern.compile("(?i)ascii\\s*\\("),
        Pattern.compile("(?i)char\\s*\\("),
        Pattern.compile("(?i)concat\\s*\\("),
        Pattern.compile("(?i)length\\s*\\("),
        
        // Database-specific functions
        Pattern.compile("(?i)version\\s*\\("),
        Pattern.compile("(?i)database\\s*\\("),
        Pattern.compile("(?i)user\\s*\\("),
        Pattern.compile("(?i)current_user\\s*\\("),
        
        // System tables and information schema
        Pattern.compile("(?i)information_schema"),
        Pattern.compile("(?i)sys\\."),
        Pattern.compile("(?i)mysql\\."),
        Pattern.compile("(?i)pg_"),
        
        // Dangerous operators
        Pattern.compile("(?i);\\s*drop\\s+table"),
        Pattern.compile("(?i);\\s*truncate\\s+table"),
        Pattern.compile("(?i);\\s*delete\\s+from"),
        Pattern.compile("(?i);\\s*insert\\s+into"),
        Pattern.compile("(?i);\\s*update\\s+"),
        
        // Stacked queries
        Pattern.compile("(?i);\\s*exec\\s+"),
        Pattern.compile("(?i);\\s*execute\\s+"),
        
        // Error-based injection
        Pattern.compile("(?i)extractvalue\\s*\\("),
        Pattern.compile("(?i)updatexml\\s*\\("),
        Pattern.compile("(?i)exp\\s*\\("),
        Pattern.compile("(?i)floor\\s*\\("),
        
        // Union-based injection
        Pattern.compile("(?i)union\\s+all\\s+select"),
        Pattern.compile("(?i)union\\s+select"),
        
        // Bypass techniques
        Pattern.compile("(?i)/\\*!.*?\\*/"),
        Pattern.compile("(?i)/\\*!50000.*?\\*/"),
        Pattern.compile("(?i)/\\*!50001.*?\\*/"),
        
        // Encoding bypasses
        Pattern.compile("(?i)%27"), // Single quote
        Pattern.compile("(?i)%22"), // Double quote
        Pattern.compile("(?i)%3b"), // Semicolon
        Pattern.compile("(?i)%2d%2d"), // Double dash
        Pattern.compile("(?i)%2f%2a"), // /* comment start
        Pattern.compile("(?i)%2a%2f"), // */ comment end
        Pattern.compile("(?i)%23"), // Hash
        Pattern.compile("(?i)%20"), // Space
        
        // Case variation bypasses
        Pattern.compile("(?i)SeLeCt"),
        Pattern.compile("(?i)UnIoN"),
        Pattern.compile("(?i)InSeRt"),
        Pattern.compile("(?i)UpDaTe"),
        Pattern.compile("(?i)DeLeTe"),
        Pattern.compile("(?i)DrOp"),
        
        // Whitespace bypasses
        Pattern.compile("(?i)select\\s+\\+\\s+"),
        Pattern.compile("(?i)union\\s+\\+\\s+"),
        
        // Alternative comment syntax
        Pattern.compile("(?i)--\\s*\\+"),
        Pattern.compile("(?i)--\\s*\\*"),
        Pattern.compile("(?i)--\\s*\\+\\*"),
        
        // Null byte injection
        Pattern.compile("(?i)%00"),
        Pattern.compile("(?i)\\x00"),
        
        // Line feed and carriage return
        Pattern.compile("(?i)%0a"),
        Pattern.compile("(?i)%0d"),
        Pattern.compile("(?i)\\n"),
        Pattern.compile("(?i)\\r"),
        
        // Tab character
        Pattern.compile("(?i)%09"),
        Pattern.compile("(?i)\\t")
    );

    /**
     * Checks if input contains SQL injection patterns
     */
    public boolean containsSQLInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(lowerInput).find()) {
                log.warn("SQL injection pattern detected: {}", input);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Validates multiple inputs for SQL injection
     */
    public boolean containsSQLInjection(String... inputs) {
        for (String input : inputs) {
            if (containsSQLInjection(input)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitizes input by removing SQL injection patterns
     */
    public String sanitizeForSQL(String input) {
        if (input == null) {
            return null;
        }
        
        String sanitized = input;
        
        // Remove SQL injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }
        
        // Additional sanitization
        sanitized = sanitized.replace("'", "''")  // Escape single quotes
                           .replace("\"", "\"\"")  // Escape double quotes
                           .replace("\\", "\\\\")  // Escape backslashes
                           .replace(";", "")       // Remove semicolons
                           .replace("--", "")      // Remove comment markers
                           .replace("/*", "")      // Remove comment start
                           .replace("*/", "")      // Remove comment end
                           .replace("#", "")       // Remove hash comments
                           .trim();
        
        if (!sanitized.equals(input)) {
            log.info("Input sanitized for SQL: original length={}, sanitized length={}", 
                input.length(), sanitized.length());
        }
        
        return sanitized;
    }

    /**
     * Validates input and throws exception if SQL injection detected
     */
    public void validateInput(String input, String fieldName) {
        if (containsSQLInjection(input)) {
            String errorMessage = String.format(
                "SQL injection pattern detected in field '%s': %s", 
                fieldName, 
                input
            );
            log.error(errorMessage);
            throw new SecurityException(errorMessage);
        }
    }

    /**
     * Validates multiple inputs and throws exception if any contains SQL injection
     */
    public void validateInputs(java.util.Map<String, String> inputs) {
        for (java.util.Map.Entry<String, String> entry : inputs.entrySet()) {
            validateInput(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Custom exception for SQL injection detection
     */
    public static class SecurityException extends RuntimeException {
        public SecurityException(String message) {
            super(message);
        }
    }
}
