package com.tnc.userManagement.controller.dto;

import com.tnc.userManagement.service.validation.OnCreate;
import com.tnc.userManagement.service.validation.OnUpdate;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.util.Date;

@Validated
public record UserDTO(

        @Null(message = "Id must be null", groups = OnCreate.class)
        @NotNull(message = "Id must not be null", groups = OnUpdate.class)
        @Positive
        Long id,
        String userId,
        @NotNull
        @NotBlank
        @Size(min = 3, max = 50, message = "This field must have between 3 and 50 chars")
        String firstName,
        @NotNull
        @NotBlank
        @Size(min = 3, max = 50, message = "This field must have between 3 and 50 chars")
        String lastName,
        @NotNull
        @NotBlank
        @Email
        String email,
        @NotNull
        @NotBlank
        @Size(min = 10, max = 10)
        String phone,
        String password,
        Date lastLoginDate,
        Date lastLoginDateDisplay,
        Date joinDate,
        String role,
        String[] authorities,
        boolean isActive,
        boolean isNotLocked
) {
}
