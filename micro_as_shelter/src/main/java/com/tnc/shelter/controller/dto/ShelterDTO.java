package com.tnc.shelter.controller.dto;

import com.tnc.shelter.service.validation.OnCreate;
import com.tnc.shelter.service.validation.OnUpdate;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Validated
public record ShelterDTO(
        @NotNull(message = "Id must not be null", groups = OnUpdate.class)
        @Null(message = "Id must be null", groups = OnCreate.class)
        @Positive(message = "Id should be positive number")
        Long id,
        String name,
        String city,
//        List<AnimalDTO> animals,
        String environment
) {
}
