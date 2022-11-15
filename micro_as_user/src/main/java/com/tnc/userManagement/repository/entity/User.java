package com.tnc.userManagement.repository.entity;
/**
 * User
 * @author tnc
 */

import com.tnc.userManagement.service.constant.RoleEnum;
import com.tnc.userManagement.service.validation.OnCreate;
import com.tnc.userManagement.service.validation.OnUpdate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "users")
public class User implements Serializable {

    /**
     * Long id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "Must be positive number")
    @Null(message = "Id must be null", groups = OnCreate.class)
    @NotNull(message = "Id must not be null", groups = OnUpdate.class)
    private Long id;
    /**
     * String userId
     */
    private String userId;
    @NotBlank(message = "This field cannot be empty")
    @NotNull(message = "Must not be null")
    @NotEmpty(message = "This field must not be empty.")
    @Length(message = "The name must be between 5 and 50 chars.", min = 5, max = 50)
    @Pattern(message = "Must contain only letters.", regexp = "(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)")
    private String firstName;
    @NotBlank(message = "This field cannot be empty")
    @NotNull(message = "Must not be null")
    @NotEmpty(message = "This field must not be empty.")
    @Length(message = "The name must be between 5 and 50 chars.", min = 5, max = 50)
    @Pattern(message = "Must contain only letters.", regexp = "(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)")
    private String lastName;
    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Pattern(message = "Must contains only digits.", regexp = "\"^(\\\\+\\\\d{1,3}( )?)?((\\\\(\\\\d{1,3}\\\\))|\\\\d{1,3})[- .]?\\\\d{3,4}[- .]?\\\\d{4}$\"")
    private String phone;
    private String password;
    private Date lastLoginDate;
    private Date lastLoginDateDisplay;
    private Date joinDate;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Transient
    private String[] authorities = new String[0];
    private boolean isActive;
    private boolean isNotLocked;
}