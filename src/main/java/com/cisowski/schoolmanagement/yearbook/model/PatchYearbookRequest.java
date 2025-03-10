package com.cisowski.schoolmanagement.yearbook.model;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collection;

@Data
public class PatchYearbookRequest {
    private Integer headTeacherId;
    private String symbol;
    private ZonedDateTime startingYear;
    private ZonedDateTime targetGraduationYear;
    private Collection<Integer> mainCourseSubjectsIdsToAdd;
    private Collection<Integer> mainCourseSubjectsIdsToRemove;
}
