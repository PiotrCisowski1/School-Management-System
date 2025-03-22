package com.cisowski.schoolmanagement.users.teacher.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherPatchRequest extends EmployeePatchRequest {

    private Collection<Integer> teachingSubjectsIdsToAdd;
    private Collection<Integer> teachingSubjectsIdsToRemove;


}
