package com.cisowski.schoolmanagement.users.common.model;

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
    private Collection<AuthorityEntity> authority;
    private AddressResponse address;
}
