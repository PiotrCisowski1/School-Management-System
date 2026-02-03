package com.cisowski.schoolmanagement.classroom.model;

import com.cisowski.schoolmanagement.users.teacher.model.availability.TimeRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetClassroomAtRequest {
    @Valid
    TimeRange timeRange;

    @NotNull()
    LocalDate startDate;

    @NotNull()
    LocalDate endDate;
}
