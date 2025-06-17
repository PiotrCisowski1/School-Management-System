package com.cisowski.schoolmanagement.schedule.model;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

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
}
