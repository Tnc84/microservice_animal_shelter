package com.tnc.userManagement.controller.dto;

import com.tnc.userManagement.service.validation.OnCreate;
import com.tnc.userManagement.service.validation.OnUpdate;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.*;
import java.util.Date;

@Validated
public record UserDTO(

        @Null(message = "Id must be null", groups = OnCreate.class)
        @NotNull(message = "Id must not be null", groups = OnUpdate.class)
        @Positive(message = "Id should be positive number")
        Long id,
        
        String userId,
        
        @NotNull(message = "First name cannot be null")
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name contains invalid characters")
        String firstName,
        
        @NotNull(message = "Last name cannot be null")
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name contains invalid characters")
        String lastName,
        
        @NotNull(message = "Email cannot be null")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email format is invalid")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email,
        
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number format is invalid")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        String phone,
        
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
                 message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String password,
        Date lastLoginDate,
        Date lastLoginDateDisplay,
        Date joinDate,
        
        @Pattern(regexp = "^(ADMIN|USER|SHELTER_MANAGER|VET)$", message = "Invalid role")
        String role,
        
        String[] authorities,
        boolean isActive,
        boolean isNotLocked
) {
}
