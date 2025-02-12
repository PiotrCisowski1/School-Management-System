package com.cisowski.schoolmanagement.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collection;

@Data
public class YearbookRequest {
    @NotNull
    private Integer headTeacherId;
    @NotNull
    private String symbol;
    @NotNull
    private ZonedDateTime startingYear;
    private ZonedDateTime targetGraduationYear;
    private Collection<Integer> studentsIdInYearbook;
    @NotEmpty
    @NotNull
    private Collection<Integer> mainCourseSubjectsIds;

}
