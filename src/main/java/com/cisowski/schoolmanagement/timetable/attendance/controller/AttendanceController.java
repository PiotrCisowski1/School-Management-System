package com.cisowski.schoolmanagement.timetable.attendance.controller;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceDetailedResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/scheduleOccurrence/{scheduleOccurrenceId}/mark")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.CREATE)
    ResponseEntity<List<AttendanceSummaryResponse>> setAttendanceAbsenceStatusForStudents(@PathVariable Long scheduleOccurrenceId, @RequestBody @Valid MarkAttendanceRequest markRequest) {
        DbLogger.info(String.format("Received POST request for Attendance marking with ID: %s and MarkAttendanceRequest: %s", scheduleOccurrenceId, markRequest.toString()));
        List<AttendanceSummaryResponse> response = attendanceService.setAttendanceAbsenceStatusForStudents(scheduleOccurrenceId, markRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scheduleOccurrence/{scheduleOccurrenceId}/active")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    ResponseEntity<List<AttendanceSummaryResponse>> getActiveAttendanceForScheduleOccurrence(@PathVariable Long scheduleOccurrenceId) {
        DbLogger.info("Received GET request for active ScheduleAttendances for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        List<AttendanceSummaryResponse> response = attendanceService.getActiveAttendanceForScheduleOccurrence(scheduleOccurrenceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{attendanceId}")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    ResponseEntity<AttendanceDetailedResponse> getAttendanceById(@PathVariable Long attendanceId) {
        DbLogger.info("Received GET request for Attendance with ID: " + attendanceId);
        AttendanceDetailedResponse response = attendanceService.getAttendanceById(attendanceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scheduleOccurrence/{scheduleOccurrenceId}/complete")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    ResponseEntity<List<AttendanceSummaryResponse>> getCompletedAttendanceForScheduleOccurrence(@PathVariable Long scheduleOccurrenceId) {
        DbLogger.info("Received GET request for active ScheduleAttendances for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        List<AttendanceSummaryResponse> response = attendanceService.getCompletedAttendanceForScheduleOccurrence(scheduleOccurrenceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
