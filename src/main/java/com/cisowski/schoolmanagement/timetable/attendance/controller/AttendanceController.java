package com.cisowski.schoolmanagement.timetable.attendance.controller;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/schedule/{scheduleId}/mark")
    @RequiresPermission(resource = ResourceType.ATTENDANCE, action = ResourceActionType.CREATE)
    ResponseEntity<List<AttendanceSummaryResponse>> setAttendanceAbsenceStatusForStudents(@PathVariable Integer scheduleId, @RequestBody @Valid MarkAttendanceRequest markRequest) {
        DbLogger.info(String.format("Received POST request for Attendance marking with ID: %s and MarkAttendanceRequest: %s", scheduleId, markRequest.toString()));
        List<AttendanceSummaryResponse> response = attendanceService.setAttendanceAbsenceStatusForStudents(scheduleId, markRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
