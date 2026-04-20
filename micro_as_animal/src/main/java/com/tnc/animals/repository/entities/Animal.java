package com.tnc.animals.repository.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Animal animal = (Animal) o;
        return getId() != null && Objects.equals(getId(), animal.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
