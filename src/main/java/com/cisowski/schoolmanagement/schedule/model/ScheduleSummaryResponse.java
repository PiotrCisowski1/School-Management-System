package com.cisowski.schoolmanagement.schedule.model;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
public class ScheduleSummaryResponse {
    private Integer id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String classroomName;
    private String teacherName;
    private String subjectName;
    private Integer scheduleVersionId;
    private ScheduleStatus status;

    public LocalTime getEndTime() {
        if(endTime != null)
            return endTime.truncatedTo(ChronoUnit.SECONDS);
        return null;
    }

    public LocalTime getStartTime() {
        if(startTime != null)
            return startTime.truncatedTo(ChronoUnit.SECONDS);
        return null;
    }
}
