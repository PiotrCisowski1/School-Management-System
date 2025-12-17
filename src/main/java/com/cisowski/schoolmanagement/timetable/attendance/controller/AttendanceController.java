package com.cisowski.schoolmanagement.timetable.attendance.controller;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.model.*;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/student/{studentId}/absence-by-schedule")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    ResponseEntity<List<AttendanceAbsenceByScheduleResponse>> getAbsenceStatsByScheduleForStudent(@PathVariable Integer studentId) {
        DbLogger.info("Received GET request for absence stats for Student with ID: " + studentId);
        List<AttendanceAbsenceByScheduleResponse> response = attendanceService.getAbsenceStatsByScheduleForStudent(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/summary")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    ResponseEntity<AttendanceOverallSummaryResponse> getSummaryStatsForStudent(@PathVariable Integer studentId, @RequestParam("startDate") Optional<LocalDate> startDate, @RequestParam("endDate") Optional<LocalDate> endDate) {
        LocalDate start = startDate.orElse(null);
        LocalDate end = endDate.orElse(null);
        DbLogger.info(String.format("Received GET request for summary attendance stats for Student with ID: %s, between %s and %s", studentId, start, end));
        AttendanceOverallSummaryResponse response = attendanceService.getSummaryAttendanceForStudent(studentId, start, end);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
