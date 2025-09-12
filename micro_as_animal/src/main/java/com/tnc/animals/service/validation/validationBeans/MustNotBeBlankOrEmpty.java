package com.tnc.animals.service.validation.validationBeans;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@NotBlank(message = "This field cannot be blank")
@NotEmpty(message = "This field cannot be empty")
@NotNull(message = "This field cannot be null")
public @interface MustNotBeBlankOrEmpty {
}
