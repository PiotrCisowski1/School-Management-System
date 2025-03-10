package com.cisowski.schoolmanagement.yearbook.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collection;

@Data
public class AddYearbookRequest {
    @NotNull
    private Integer headTeacherId;
    @NotNull
    private String symbol;
    @NotNull
    private ZonedDateTime startingYear;
    private ZonedDateTime graduationYear;
    @NotEmpty
    @NotNull
    private Collection<Integer> mainCourseSubjectsIds;

}
