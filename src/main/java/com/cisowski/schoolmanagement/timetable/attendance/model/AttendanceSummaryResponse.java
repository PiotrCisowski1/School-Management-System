package com.cisowski.schoolmanagement.timetable.attendance.model;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import lombok.Data;

@Data
public class AttendanceSummaryResponse {
    private Long id;
    private ScheduleSummaryResponse schedule;
    private StudentSummaryResponse student;
    private AttendanceStatus status;
}
