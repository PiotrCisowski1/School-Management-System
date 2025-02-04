package com.cisowski.schoolmanagement.model.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherCreateRequest extends EmployeeCreateRequest {

    @NotNull(message = "Teacher's main subjects cannot be null")
    @NotEmpty(message = "Teacher's main subjects cannot be empty")
    private Set<Integer> teachingSubjectsIds;

}
