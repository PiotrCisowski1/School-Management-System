package com.cisowski.schoolmanagement.users.common.model;

import lombok.Data;

import java.util.Date;

@Data
public abstract class BaseUserSummaryResponse {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private Gender gender;
    private String phoneNumber;
}
