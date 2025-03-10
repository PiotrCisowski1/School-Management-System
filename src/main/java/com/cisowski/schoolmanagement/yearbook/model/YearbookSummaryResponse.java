package com.cisowski.schoolmanagement.yearbook.model;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class YearbookSummaryResponse {
    private Integer id;
    private String symbol;
    private TeacherSummaryResponse headTeacher;
    private ZonedDateTime startingYear;
    private ZonedDateTime graduationYear;
}
