package com.cisowski.schoolmanagement.model.response;

import com.cisowski.schoolmanagement.model.enums.Gender;
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
