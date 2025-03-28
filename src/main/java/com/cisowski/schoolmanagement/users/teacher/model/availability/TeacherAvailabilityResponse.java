package com.cisowski.schoolmanagement.users.teacher.model.availability;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;

@Data
public class TeacherAvailabilityResponse {
    private Integer id;
    private TeacherSummaryResponse teacher;
    private DayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private boolean isAvailable;
    private String notes;
}
