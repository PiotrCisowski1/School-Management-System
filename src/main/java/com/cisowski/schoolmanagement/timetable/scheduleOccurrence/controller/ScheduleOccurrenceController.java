package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
@Tag(name = "Schedule Occurrences", description = "Management of specific lesson instances. Handles individual session details, substitutions, and real-time schedule updates.")
public class ScheduleOccurrenceController {

    private final ScheduleOccurrenceService occurrenceService;

    @GetMapping("/yearbook/{yearbookId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE_OCCURRENCE)
    @SecurityResponses
    @Operation(
            summary = "Get schedule occurrences for yearbook",
            description = "Retrieves all schedule (active) occurrences planned for given Yearbook in particular time range. Time range is configured in app configuration value. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns objects list (empty result as well)")
    @ApiResponse(responseCode = "404", description = "Yearbook not found with given ID")
    @ApiResponse(responseCode = "404", description = "Required app config not found")
    @ApiResponse(responseCode = "406", description = "Cannot fetch occurrences because there is no active schedule version")
    ResponseEntity<List<ScheduleOccurrenceSummaryResponse>> getOccurrencesForYearbook(Integer yearbookId) {
        DbLogger.info("Received Occurrence GET request for Yearbook with ID " + yearbookId);
        List<ScheduleOccurrenceSummaryResponse> response = occurrenceService.getOccurrencesForYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{scheduleOccurrenceId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE_OCCURRENCE)
    @SecurityResponses
    @Operation(
            summary = "Get occurrence with ID",
            description = "Retrieves existing schedule occurrence with given ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Not found with given ID")
    ResponseEntity<ScheduleOccurrenceSummaryResponse> getOccurrenceById(Long scheduleOccurrenceId) {
        DbLogger.info("Received Occurrence GET request for ID " + scheduleOccurrenceId);
        ScheduleOccurrenceSummaryResponse response = occurrenceService.getOccurrenceById(scheduleOccurrenceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
