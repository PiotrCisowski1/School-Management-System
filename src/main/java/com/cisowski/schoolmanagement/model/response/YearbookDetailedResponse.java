package com.cisowski.schoolmanagement.model.response;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collection;

@Data
public class YearbookDetailedResponse {
    private Integer id;
    private TeacherSummaryResponse headTeacher;
    private String symbol;
    private ZonedDateTime startingYear;
    private ZonedDateTime graduationYear;
    private Collection<StudentSummaryResponse> studentsInYearbook;
    private Collection<SubjectSummaryResponse> mainCourseSubjects;

}
