package com.tnc.userManagement.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
// import java.util.regex.Matcher; // Not used in this class

/**
 * Input Validation Utility for User Management Microservice
 * Provides comprehensive input validation and sanitization
 */
@Slf4j
@Component
public class InputValidationUtil {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Phone number validation pattern
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );
    
    // Password strength pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    // Name validation pattern (letters, spaces, hyphens, apostrophes)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z\\s'-]+$"
    );
    
    // SQL injection patterns
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)\\s+"),
        Pattern.compile("(?i)'\\s*(or|and)\\s*'"),
        Pattern.compile("(?i);\\s*drop\\s+table"),
        Pattern.compile("(?i)--"),
        Pattern.compile("(?i)/\\*.*?\\*/"),
        Pattern.compile("(?i)waitfor\\s+delay"),
        Pattern.compile("(?i)benchmark\\s*\\(")
    };
    
    // XSS patterns
    private static final Pattern[] XSS_PATTERNS = {
        Pattern.compile("(?i)<script[^>]*>.*?</script>"),
        Pattern.compile("(?i)<iframe[^>]*>.*?</iframe>"),
        Pattern.compile("(?i)javascript:"),
        Pattern.compile("(?i)vbscript:"),
        Pattern.compile("(?i)onload\\s*="),
        Pattern.compile("(?i)onerror\\s*="),
        Pattern.compile("(?i)onclick\\s*="),
        Pattern.compile("(?i)eval\\s*\\("),
        Pattern.compile("(?i)expression\\s*\\("),
        Pattern.compile("(?i)alert\\s*\\(")
    };

    /**
     * Validates email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        boolean isValid = EMAIL_PATTERN.matcher(email.trim()).matches();
        if (!isValid) {
            log.warn("Invalid email format: {}", email);
        }
        return isValid;
    }

    /**
     * Validates phone number format
     */
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        boolean isValid = PHONE_PATTERN.matcher(phone.trim()).matches();
        if (!isValid) {
            log.warn("Invalid phone format: {}", phone);
        }
        return isValid;
    }

    /**
     * Validates password strength
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        boolean isValid = PASSWORD_PATTERN.matcher(password).matches();
        if (!isValid) {
            log.warn("Password does not meet strength requirements");
        }
        return isValid;
    }

    /**
     * Validates name format
     */
    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 50) {
            log.warn("Name length invalid: {}", name);
            return false;
        }
        
        boolean isValid = NAME_PATTERN.matcher(trimmedName).matches();
        if (!isValid) {
            log.warn("Invalid name format: {}", name);
        }
        return isValid;
    }

    /**
     * Checks for SQL injection patterns
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
     * Checks for XSS patterns
     */
    public boolean containsXSS(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                log.warn("XSS pattern detected: {}", input);
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitizes input by removing dangerous patterns
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        String sanitized = input;
        
        // Remove SQL injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }
        
        // Remove XSS patterns
        for (Pattern pattern : XSS_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }
        
        // HTML encode special characters
        sanitized = sanitized.replace("<", "&lt;")
                           .replace(">", "&gt;")
                           .replace("\"", "&quot;")
                           .replace("'", "&#x27;")
                           .replace("/", "&#x2F;");
        
        if (!sanitized.equals(input)) {
            log.info("Input sanitized: original length={}, sanitized length={}", 
                input.length(), sanitized.length());
        }
        
        return sanitized;
    }

    /**
     * Validates and sanitizes user input
     */
    public ValidationResult validateAndSanitizeUserInput(String firstName, String lastName, 
                                                       String email, String phone) {
        ValidationResult result = new ValidationResult();
        
        // Validate first name
        if (!isValidName(firstName)) {
            result.addError("firstName", "Invalid first name format");
        } else {
            result.setSanitizedFirstName(sanitizeInput(firstName));
        }
        
        // Validate last name
        if (!isValidName(lastName)) {
            result.addError("lastName", "Invalid last name format");
        } else {
            result.setSanitizedLastName(sanitizeInput(lastName));
        }
        
        // Validate email
        if (!isValidEmail(email)) {
            result.addError("email", "Invalid email format");
        } else {
            result.setSanitizedEmail(sanitizeInput(email));
        }
        
        // Validate phone
        if (phone != null && !phone.trim().isEmpty() && !isValidPhone(phone)) {
            result.addError("phone", "Invalid phone format");
        } else if (phone != null && !phone.trim().isEmpty()) {
            result.setSanitizedPhone(sanitizeInput(phone));
        }
        
        return result;
    }

    /**
     * Validation result container
     */
    public static class ValidationResult {
        private final java.util.Map<String, String> errors = new java.util.HashMap<>();
        private String sanitizedFirstName;
        private String sanitizedLastName;
        private String sanitizedEmail;
        private String sanitizedPhone;
        
        public void addError(String field, String message) {
            errors.put(field, message);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public java.util.Map<String, String> getErrors() {
            return new java.util.HashMap<>(errors);
        }
        
        // Getters and setters
        public String getSanitizedFirstName() { return sanitizedFirstName; }
        public void setSanitizedFirstName(String sanitizedFirstName) { this.sanitizedFirstName = sanitizedFirstName; }
        
        public String getSanitizedLastName() { return sanitizedLastName; }
        public void setSanitizedLastName(String sanitizedLastName) { this.sanitizedLastName = sanitizedLastName; }
        
        public String getSanitizedEmail() { return sanitizedEmail; }
        public void setSanitizedEmail(String sanitizedEmail) { this.sanitizedEmail = sanitizedEmail; }
        
        public String getSanitizedPhone() { return sanitizedPhone; }
        public void setSanitizedPhone(String sanitizedPhone) { this.sanitizedPhone = sanitizedPhone; }
    }
}
