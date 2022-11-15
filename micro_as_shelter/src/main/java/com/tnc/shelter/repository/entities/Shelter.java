package com.tnc.shelter.repository.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shelter {

    @Positive(message = "Must be positive number")
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotBlank(message = "This field cannot be empty")
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
    private String city;

//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinColumn(name = "shelter_id")
//    private List<Animal> animals = new ArrayList<>();

    private String environment;
}


