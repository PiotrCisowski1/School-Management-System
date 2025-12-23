package com.cisowski.schoolmanagement.timetable.attendance.model;

import lombok.Data;

@Data
public class AttendanceAbsenceByScheduleResponse {
    private Integer scheduleId;
    private String scheduleName;
    private Integer totalAbsenceCount;
    private Integer unmarkedAbsenceCount;
}
