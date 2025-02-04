package com.cisowski.schoolmanagement.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddStudentResponse extends StudentDetailedResponse {

    private String password;

}
