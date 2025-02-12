package com.cisowski.schoolmanagement.users.common.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

@Data
public abstract class BasePatchUserRequest {
    @Email(message = "Email has to be in proper form like: test@example.com")
    String email;

    @Digits(integer = 9, fraction = 0, message = "Given phone number has to be 9 digits")
    @Positive(message = "All the digits has to be positive numbers")
    String phoneNumber;

    @Size(min = 1, max = 50, message = "First name has to be between 1 and 50 characters")
    String firstName;

    @Size(min = 1, max = 50, message = "Last name has to be between 1 and 50 characters")
    String lastName;

    @Past(message = "Given birth date has to be in the past")
    Date birthDate;

    Gender gender;

    AddressRequest address;

    Collection<AuthorityEntity> authority;

    @Setter(AccessLevel.NONE)
    ZonedDateTime dateOfUpdate = ZonedDateTime.now();

}
