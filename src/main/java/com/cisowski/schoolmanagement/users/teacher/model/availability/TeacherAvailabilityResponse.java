package com.cisowski.schoolmanagement.users.teacher.model.availability;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class TeacherAvailabilityResponse {
    private Integer id;
    private TeacherSummaryResponse teacher;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isAvailable;
    private String notes;
}
