package com.cisowski.schoolmanagement.model.response;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class YearbookSummaryResponse {
    private Integer id;
    private String symbol;
    private TeacherSummaryResponse headTeacher;
    private ZonedDateTime startingYear;

}
