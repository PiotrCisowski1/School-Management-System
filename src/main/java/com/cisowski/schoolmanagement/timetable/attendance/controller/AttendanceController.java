package com.cisowski.schoolmanagement.timetable.attendance.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.model.*;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Attendance", description = "Tracking and reporting student presence. Manages absence records and attendance summaries. Attendance is linked to schedule occurrence and student. Attendance is automatically initialized X minutes before occurrence starts - configured in application configuration")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/scheduleOccurrence/{scheduleOccurrenceId}/mark")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.CREATE)
    @SecurityResponses
    @Operation(
            summary = "Set attendance status for students",
            description = "Mark attendance for given students with particular status. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Marked successfully")
    @ApiResponse(responseCode = "404", description = "Schedule occurrence not found")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @ApiResponse(responseCode = "406", description = "Invalid status or empty student list")
    @ApiResponse(responseCode = "406", description = "Marking attendance status for occurrence with invalid status")
    @ApiResponse(responseCode = "406", description = "Student does not belong to given occurrence")
    ResponseEntity<List<AttendanceSummaryResponse>> setAttendanceAbsenceStatusForStudents(@PathVariable Long scheduleOccurrenceId, @RequestBody @Valid MarkAttendanceRequest markRequest) {
        DbLogger.info(String.format("Received POST request for Attendance marking with ID: %s and MarkAttendanceRequest: %s", scheduleOccurrenceId, markRequest.toString()));
        List<AttendanceSummaryResponse> response = attendanceService.setAttendanceAbsenceStatusForStudents(scheduleOccurrenceId, markRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scheduleOccurrence/{scheduleOccurrenceId}/active")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Find attendances for schedule occurrence",
            description = "Retrieves list of attendance for particular schedule occurrence. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Returns attendances for ongoing occurrence")
    @ApiResponse(responseCode = "404", description = "Schedule occurrence not found")
    @ApiResponse(responseCode = "406", description = "Invalid status for schedule occurrence (not started yet)")
    ResponseEntity<List<AttendanceSummaryResponse>> getActiveAttendanceForScheduleOccurrence(@PathVariable Long scheduleOccurrenceId) {
        DbLogger.info("Received GET request for active ScheduleAttendances for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        List<AttendanceSummaryResponse> response = attendanceService.getActiveAttendanceForScheduleOccurrence(scheduleOccurrenceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{attendanceId}")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Find attendance with ID",
            description = "Retrieves existing attendance with given ID. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Returns existing attendance")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    ResponseEntity<AttendanceDetailedResponse> getAttendanceById(@PathVariable Long attendanceId) {
        DbLogger.info("Received GET request for Attendance with ID: " + attendanceId);
        AttendanceDetailedResponse response = attendanceService.getAttendanceById(attendanceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scheduleOccurrence/{scheduleOccurrenceId}/complete")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Find completed attendances for schedule occurrence",
            description = "Retrieves existing, completed attendance for particular schedule occurrence. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Returns completed attendances")
    @ApiResponse(responseCode = "404", description = "Schedule occurrence not found")
    @ApiResponse(responseCode = "406", description = "Occurrence is not yet complete")
    ResponseEntity<List<AttendanceSummaryResponse>> getCompletedAttendanceForScheduleOccurrence(@PathVariable Long scheduleOccurrenceId) {
        DbLogger.info("Received GET request for active ScheduleAttendances for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        List<AttendanceSummaryResponse> response = attendanceService.getCompletedAttendanceForScheduleOccurrence(scheduleOccurrenceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/absence-by-schedule")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Absence statistics for student",
            description = "Calculate absence statistics of particular student. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns statistics list (empty result as well)")
    @ApiResponse(responseCode = "404", description = "Student not found")
    ResponseEntity<List<AttendanceAbsenceByScheduleResponse>> getAbsenceStatsByScheduleForStudent(@PathVariable Integer studentId) {
        DbLogger.info("Received GET request for absence stats for Student with ID: " + studentId);
        List<AttendanceAbsenceByScheduleResponse> response = attendanceService.getAbsenceStatsByScheduleForStudent(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/summary")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Statistics of student's attendance",
            description = "Calculate overall attendance statistics for particular student. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns statistic")
    @ApiResponse(responseCode = "404", description = "Student not found")
    ResponseEntity<AttendanceOverallSummaryResponse> getSummaryStatsForStudent(@PathVariable Integer studentId, @RequestParam("startDate") Optional<LocalDate> startDate, @RequestParam("endDate") Optional<LocalDate> endDate) {
        LocalDate start = startDate.orElse(null);
        LocalDate end = endDate.orElse(null);
        DbLogger.info(String.format("Received GET request for summary attendance stats for Student with ID: %s, between %s and %s", studentId, start, end));
        AttendanceOverallSummaryResponse response = attendanceService.getSummaryAttendanceForStudent(studentId, start, end);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
