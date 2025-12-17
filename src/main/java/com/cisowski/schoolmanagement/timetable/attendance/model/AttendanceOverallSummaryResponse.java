package com.cisowski.schoolmanagement.timetable.attendance.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceOverallSummaryResponse {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalAttendanceCount;
    private Integer totalAbsence;
    private Integer totalUnmarked;
    private Double presentPercentage;
}
