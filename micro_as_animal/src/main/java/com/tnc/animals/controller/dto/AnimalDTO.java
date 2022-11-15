package com.tnc.animals.controller.dto;

import com.tnc.animals.service.validation.OnCreate;
import com.tnc.animals.service.validation.OnUpdate;
import com.tnc.animals.service.validation.validationBeans.MustNotBeBlankOrEmpty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

@Validated
public record AnimalDTO(
        @Null(message = "Id must be null", groups = OnCreate.class)
        @NotNull(message = "Id must not be null", groups = OnUpdate.class)
        @Positive(message = "Id should be positive number")
        Long id,
        String name,
        String breed,
        String species,
        String photo,

        String environment
) {
}
