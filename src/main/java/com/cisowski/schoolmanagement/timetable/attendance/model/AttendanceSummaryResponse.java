package com.cisowski.schoolmanagement.timetable.attendance.model;

import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import lombok.Data;

@Data
public class AttendanceSummaryResponse {
    private Long id;
    private ScheduleOccurrenceSummaryResponse scheduleOccurrence;
    private StudentSummaryResponse student;
    private AttendanceStatus status;
}
