package com.cisowski.schoolmanagement.timetable.attendance.service;

import com.cisowski.schoolmanagement.timetable.attendance.model.*;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;

import java.util.List;

public interface AttendanceService {
    void initializeAttendances(ScheduleOccurrenceEntity scheduleOccurrence);
    List<AttendanceSummaryResponse> setAttendanceAbsenceStatusForStudents(Long scheduleOccurrenceId, MarkAttendanceRequest request);
    List<AttendanceSummaryResponse> getActiveAttendanceForScheduleOccurrence(Long scheduleOccurrenceId);
    AttendanceDetailedResponse getAttendanceById(Long attendanceId);
    List<AttendanceSummaryResponse> getCompletedAttendanceForScheduleOccurrence(Long scheduleOccurrenceId);
    AttendanceEntity fetchAttendance(Long attendanceId);
    List<AttendanceAbsenceByScheduleResponse> getAbsenceStatsByScheduleForStudent(Integer studentId);
}
