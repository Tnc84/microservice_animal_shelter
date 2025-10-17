package com.tnc.shelter.controller.dto;

import com.tnc.shelter.service.validation.OnCreate;
import com.tnc.shelter.service.validation.OnUpdate;
// import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
// @Validated
public class ShelterDTO {
    
    @NotNull(message = "Id must not be null", groups = OnUpdate.class)
    @Null(message = "Id must be null", groups = OnCreate.class)
    @Positive(message = "Id should be positive number")
    private Long id;
    
    private String name;
    
    private String city;
    
    // Statistics fields for tracking animal events
    private Integer animalCount;
    
    private Integer maxCapacity;
    
    private Integer adoptionCount;
    
    private LocalDateTime lastModified;
}
