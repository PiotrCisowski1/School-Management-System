package com.cisowski.schoolmanagement.subject.model;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import lombok.Data;

import java.util.Collection;

@Data
public class SubjectDetailedResponse {
    private Integer id;
    private String name;
    private String code;
    private Collection<TeacherSummaryResponse> teachers;
    private String description;
    private Collection<YearbookSummaryResponse> yearbooksTakingSubject;
    private String subjectType;
}
