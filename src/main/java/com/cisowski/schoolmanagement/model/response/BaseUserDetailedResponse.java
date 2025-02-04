package com.cisowski.schoolmanagement.model.response;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.enums.Gender;
import lombok.Data;

import java.util.Collection;
import java.util.Date;
@Data
public abstract class BaseUserDetailedResponse {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Date birthDate;
    private Gender gender;
    private Collection<Authority> authority;
    private AddressResponse address;
}
