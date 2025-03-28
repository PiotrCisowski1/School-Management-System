package com.cisowski.schoolmanagement.users.teacher.model.availability;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;

@Data
@AllArgsConstructor
public class TeacherAvailabilityRequest {

    @Min(value = 1, message = "Day of week must be number between 1 and 7")
    @Max(value = 7, message = "Day of week must be number between 1 and 7")
    private Integer dayOfWeek;

    @NotNull(message = "Cannot be null or empty")
    private Time startTime;

    @NotNull(message = "Cannot be null or empty")
    private Time endTime;

    private boolean isAvailable;

    @Size(max = 200, message = "Notes can be up to 200 characters")
    private String notes = "";

    @AssertTrue(message = "Start time must be before end time")
    public boolean isValidTimeRange(){
        return startTime != null && endTime != null && startTime.compareTo(endTime) < 0;
    }

    @Override
    public String toString() {
        return "TeacherAvailabilityRequest{" +
                "dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", isAvailable=" + isAvailable +
                ", notes size=" + notes.length() +
                '}';
    }
}
