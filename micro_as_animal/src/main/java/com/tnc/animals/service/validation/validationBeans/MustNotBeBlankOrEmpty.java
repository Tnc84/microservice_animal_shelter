package com.tnc.animals.service.validation.validationBeans;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@NotBlank(message = "This field cannot be blank")
@NotEmpty(message = "This field cannot be empty")
@NotNull(message = "This field cannot be null")
public @interface MustNotBeBlankOrEmpty {
}
