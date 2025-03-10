package com.cisowski.schoolmanagement.users.teacher.model;


import com.cisowski.schoolmanagement.model.response.SubjectSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import com.cisowski.schoolmanagement.users.common.model.BaseUserDetailedResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;
@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherDetailedResponse extends BaseUserDetailedResponse {
    private YearbookSummaryResponse leadingYearbook;
    private Set<SubjectSummaryResponse> teachingSubjects;


}

