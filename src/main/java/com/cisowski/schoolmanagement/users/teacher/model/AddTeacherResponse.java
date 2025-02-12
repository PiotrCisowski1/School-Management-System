package com.cisowski.schoolmanagement.users.teacher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddTeacherResponse extends TeacherDetailedResponse {

    private String password;
}
