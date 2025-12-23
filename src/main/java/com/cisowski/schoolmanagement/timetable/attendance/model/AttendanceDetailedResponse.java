package com.cisowski.schoolmanagement.timetable.attendance.model;

import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.users.common.model.UserSummaryResponse;
import com.cisowski.schoolmanagement.users.student.model.StudentSummaryResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceDetailedResponse {
    private Long id;
    private StudentSummaryResponse student;
    private AttendanceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UserSummaryResponse lastModifiedBy;
    private ScheduleOccurrenceSummaryResponse occurrence;
}
