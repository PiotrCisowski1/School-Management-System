package com.cisowski.schoolmanagement.timetable.attendance.service;

import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;

import java.util.List;

public interface AttendanceService {
    void initializeAttendances(ScheduleEntity schedule);
    List<AttendanceSummaryResponse> setAttendanceAbsenceStatusForStudents(Integer scheduleId, MarkAttendanceRequest request);
}
