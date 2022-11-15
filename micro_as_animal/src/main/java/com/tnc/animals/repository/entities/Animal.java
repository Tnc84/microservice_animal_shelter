package com.tnc.animals.repository.entities;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Positive(message = "Must be positive number")
    private Long id;
    @NotBlank(message = "This field cannot be blank")
    @NotEmpty(message = "This field must not be empty.")
    @Pattern(message = "Name should contain only letters.", regexp = "(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)")
    @NotNull(message = "Must not be null")
    @Length(message = "The name must be between 3 and 100 chars.", min = 3, max = 100)
    private String name;
    @NotBlank(message = "This field cannot be empty")
    @NotEmpty(message = "This field must not be empty.")
    @Pattern(message = "Name should contain only letters.", regexp = "(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)")
    @NotNull(message = "Must not be null")
    @Length(message = "The name must be between 3 and 100 chars.", min = 3, max = 100)
    private String breed;
    @NotBlank(message = "This field cannot be empty")
    @NotEmpty(message = "This field must not be empty.")
    @Pattern(message = "Name should contain only letters.", regexp = "(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)")
    @NotNull(message = "Must not be null")
    @Length(message = "The name must be between 3 and 100 chars.", min = 3, max = 100)
    private String species;

    private String photo;

}
