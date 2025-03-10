package com.cisowski.schoolmanagement.model.response;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import lombok.Data;

import java.util.Collection;
import java.util.Set;

@Data
public class SubjectDetailedResponse {
    private Integer id;
    private String name;
    private String code;
    private Collection<TeacherSummaryResponse> teachers;
    private String description;
    private Set<YearbookSummaryResponse> yearbooksTakingSubject;

}
