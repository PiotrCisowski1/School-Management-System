package com.cisowski.schoolmanagement.timetable.attendance.service;

import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;

import java.util.List;

public interface AttendanceService {
    void initializeAttendances(ScheduleOccurrenceEntity scheduleOccurrence);
    List<AttendanceSummaryResponse> setAttendanceAbsenceStatusForStudents(Long scheduleOccurrenceId, MarkAttendanceRequest request);
}
