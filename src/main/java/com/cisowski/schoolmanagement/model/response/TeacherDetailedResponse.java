package com.cisowski.schoolmanagement.model.response;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherDetailedResponse extends BaseUserDetailedResponse {
    private YearbookSummaryResponse leadingYearbook;
    private Set<SubjectSummaryResponse> teachingSubjects;


}

