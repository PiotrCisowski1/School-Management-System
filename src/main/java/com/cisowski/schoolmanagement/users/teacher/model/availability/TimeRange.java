package com.cisowski.schoolmanagement.users.teacher.model.availability;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
public class TimeRange {

    @Min(value = 1, message = "Day of week must be a Integer value between 1 and 7")
    @Max(value = 7, message = "Day of week must be a Integer value between 1 and 7")
    private Integer dayOfWeek;
    @NotNull(message = "Cannot be null or empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @NotNull(message = "Cannot be null or empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
