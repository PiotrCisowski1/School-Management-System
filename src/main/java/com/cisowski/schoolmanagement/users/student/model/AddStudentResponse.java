package com.cisowski.schoolmanagement.users.student.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddStudentResponse extends StudentDetailedResponse {

    private String password;

}
