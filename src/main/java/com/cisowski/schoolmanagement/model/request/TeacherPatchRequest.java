package com.cisowski.schoolmanagement.model.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherPatchRequest extends EmployeePatchRequest {

    private Set<Integer> teachingSubjectsIdsToAdd;
    private Set<Integer> teachingSubjectsIdsToRemove;


}
