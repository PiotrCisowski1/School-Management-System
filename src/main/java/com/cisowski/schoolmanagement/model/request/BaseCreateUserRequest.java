package com.cisowski.schoolmanagement.model.request;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

@Data
public abstract class BaseCreateUserRequest {
    @NotNull(message = "Email cannot be empty")
    @Email(message = "Email has to be in proper form like: test@example.com")
    String email;

    @Digits(integer = 9, fraction = 0, message = "Given phone number has to be 9 digits")
    @Positive(message = "All the digits has to be positive numbers")
    String phoneNumber;

    @NotNull(message = "First name cannot be empty")
    @Size(min = 1, max = 50, message = "First name has to be between 1 and 50 characters")
    String firstName;

    @NotNull(message = "Last name cannot be empty")
    @Size(min = 1, max = 50, message = "Last name has to be between 1 and 50 characters")
    String lastName;

    @NotNull(message = "Birth date cannot be empty")
    @Past(message = "Given birth date has to be in the past")
    Date birthDate;

    Gender gender;

    @NotNull(message = "Address cannot be empty")
    AddressRequest address;

    @NotEmpty(message = "Roles/authorities cannot be empty")
    @NotNull
    Collection<Authority> authority;

    @Setter(AccessLevel.NONE)
    ZonedDateTime dateOfCreation = ZonedDateTime.now();

}
