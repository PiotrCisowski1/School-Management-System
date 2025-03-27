package com.cisowski.schoolmanagement.users.teacher.model.availability;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TeacherAvailabilityRequest {

    @Min(value = 1, message = "Day of week must be number between 1 and 7")
    @Max(value = 7, message = "Day of week must be number between 1 and 7")
    private Integer dayOfWeek;

    @NotNull
    @NotBlank
    private LocalTime startTime;

    @NotNull
    @NotBlank
    private LocalTime endTime;

    @NotNull
    @NotBlank
    private boolean isAvailable;

    private String notes = "";

    @AssertTrue(message = "Start time must be before end time")
    public boolean isValidTimeRange(){
        return startTime.isBefore(endTime);
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
