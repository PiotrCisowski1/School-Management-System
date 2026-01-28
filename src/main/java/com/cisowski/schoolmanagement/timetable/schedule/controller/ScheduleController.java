package com.cisowski.schoolmanagement.timetable.schedule.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherSchedulePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleSummaryResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.AddScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.PatchScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "Schedules", description = "Core engine for school time management. Handles lesson occurrences, class cancellations etc.")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleVersionService scheduleVersionService;
    private final TeacherSchedulePermissionHandler permissionHandler;

    @PostMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Create new schedule",
            description = "Adds new schedule representing recurrent lesson unit applicable in a given time period. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Schedule version not found by ID")
    @ApiResponse(responseCode = "404", description = "Subject not found by ID")
    @ApiResponse(responseCode = "404", description = "Teacher not found by ID")
    @ApiResponse(responseCode = "404", description = "Classroom not found by ID")
    @ApiResponse(responseCode = "406", description = "Schedule already appointed in a given time period")
    @ApiResponse(responseCode = "406", description = "Teacher not available in a given time period")
    @ApiResponse(responseCode = "406", description = "Classroom not available in a given time period")
    public ResponseEntity<ScheduleDetailedResponse> addSchedule(@Valid @RequestBody AddScheduleRequest request, @PathVariable Integer scheduleVersionId){
        DbLogger.info("Received POST Schedule request for: " + request.toString());
        ScheduleDetailedResponse response = scheduleService.addSchedule(request, scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Delete schedule",
            description = "Remove existing schedule if has no ongoing occurrences, and cancels booked occurrences. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found by ID")
    @ApiResponse(responseCode = "406", description = "Active occurrences ongoing")
    public ResponseEntity deleteSchedule(@PathVariable Integer scheduleId){
        DbLogger.info("Received DELETE Schedule request for ID: " + scheduleId);
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Update schedule",
            description = "Modify existing schedule, if time period is applicable and already booked. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modification successful")
    @ApiResponse(responseCode = "404", description = "Not found by ID")
    @ApiResponse(responseCode = "404", description = "Given modified objects not found by ID")
    @ApiResponse(responseCode = "406", description = "Modification canceled because of actual schedule status")
    @ApiResponse(responseCode = "406", description = "Teacher not available in given time period")
    @ApiResponse(responseCode = "406", description = "Classroom not available in given time period")
    @ApiResponse(responseCode = "406", description = "Modification canceled because of already booked schedule in given time period")
    public ResponseEntity<ScheduleDetailedResponse> patchSchedule(@PathVariable Integer scheduleId, @Valid @RequestBody PatchScheduleRequest request){
        DbLogger.info("Received PATCH Schedule request for ID: " + scheduleId);
        ScheduleDetailedResponse response = scheduleService.patchSchedule(scheduleId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{scheduleVersionId}/{scheduleId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE)
    @SecurityResponses
    @Operation(
            summary = "Find schedule by ID",
            description = "Find existing schedule with ID. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Not found by ID")
    public ResponseEntity<ScheduleDetailedResponse> getSchedule(@PathVariable Integer scheduleId, @PathVariable Integer scheduleVersionId){
        DbLogger.info("Received GET Schedule request for ID: " + scheduleId);
        ScheduleDetailedResponse response = scheduleService.getSchedule(scheduleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/{scheduleVersionId}/day/{dayOfWeek}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE)
    @SecurityResponses
    @Operation(
            summary = "Get list of schedules for day of week",
            description = "Find all schedules booked on particular day of week for schedule version. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns existing objects (empty result as well)")
    public ResponseEntity<List<ScheduleSummaryResponse>> getScheduleList(@PathVariable Integer scheduleVersionId, @PathVariable Integer dayOfWeek){
        DbLogger.info(String.format("Received GET Schedule request for day: %s in ScheduleVersion with ID: %s ", dayOfWeek, scheduleVersionId));
        boolean needsFiltering = false;
        Integer userId = -1;
        if(permissionHandler != null) {
            needsFiltering = permissionHandler.requiresFiltering();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            userId = ((UserDetailsEntity) auth.getPrincipal()).getId();
        }
        List<ScheduleSummaryResponse> response = scheduleService.getScheduleByDayOfWeek(scheduleVersionId, dayOfWeek, needsFiltering, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/version")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Create schedule version",
            description = "Adds new schedule version representing lesson plan for particular period of time associated with particular yearbook. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Successfully created")
    @ApiResponse(responseCode = "404", description = "Yearbook not found by ID")
    public ResponseEntity<ScheduleVersionDetailedResponse> createScheduleVersion(@RequestBody @Valid AddScheduleVersionRequest request){
        DbLogger.info(String.format(
                "Received POST ScheduleVersion request (named: %s) for Yearbook with ID %s, isActive - %s",
                request.getScheduleName(), request.getYearbookId(), request.isActive()));
        ScheduleVersionDetailedResponse response = scheduleVersionService.createScheduleVersion(request.getYearbookId(), request.getScheduleName(), request.isActive());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/version/{scheduleVersionId}/clone")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Clone schedule version",
            description = "Creates new schedule version based on date in cloned version with all schedules, that is set to unactive by default. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Successfully created")
    @ApiResponse(responseCode = "404", description = "Schedule version to clone not found by ID")
    @ApiResponse(responseCode = "406", description = "Schedule version to clone is already marked as removed")
    public ResponseEntity<ScheduleVersionDetailedResponse> cloneScheduleVersion(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received POST (clone) ScheduleVersion request for ScheduleVersion with ID %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.cloneScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/version/yearbook/{yearbookId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Get all schedule versions for yearbook",
            description = "Retrieves all schedule versions for Yearbook that are not marked as removed. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns list of objects (empty result as well)")
    public ResponseEntity<Collection<ScheduleVersionSummaryResponse>> getScheduleVersionsForYearbook(@PathVariable Integer yearbookId){
        DbLogger.info(String.format("Received GET all ScheduleVersions for Yearbook with ID: %s", yearbookId));
        Collection<ScheduleVersionSummaryResponse> response = scheduleVersionService.getScheduleVersionsForYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/yearbook/{yearbookId}/active")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE_VERSION)
    @SecurityResponses
    @Operation(
            summary = "Get active schedule version for Yearbook",
            description = "Retrieves active schedule version for Yearbook. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns existing active schedule version for given yearbook")
    @ApiResponse(responseCode = "406", description = "No active schedule version for given yearbook")
    public ResponseEntity<ScheduleVersionDetailedResponse> getActiveScheduleVersionsForYearbook(@PathVariable Integer yearbookId){
        DbLogger.info(String.format("Received GET active ScheduleVersion for Yearbook with ID: %s", yearbookId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.getActiveScheduleVersionForYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/{scheduleVersionId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE_VERSION)
    @SecurityResponses
    @Operation(
            summary = "Get schedule version by ID",
            description = "Retrieves existing schedule version for given ID. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns existing schedule version")
    @ApiResponse(responseCode = "404", description = "Schedule version not found for given ID")
    @ApiResponse(responseCode = "406", description = "Schedule version already marked as removed")
    public ResponseEntity<ScheduleVersionDetailedResponse> getScheduleVersionsForId(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received GET ScheduleVersions for ID: %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.getScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Delete schedule version",
            description = "Remove existing schedule version with given ID, if not already marked as removed. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found by ID")
    @ApiResponse(responseCode = "406", description = "Schedule version already marked as removed")
    public ResponseEntity deleteScheduleVersion(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received DELETE ScheduleVersion request for ID: %s", scheduleVersionId));
        scheduleVersionService.deleteScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Update schedule version",
            description = "Modify existing schedule version. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modified successfully")
    @ApiResponse(responseCode = "404", description = "Schedule version not found by ID")
    @ApiResponse(responseCode = "404", description = "Yearbook not found by ID")
    public ResponseEntity<ScheduleVersionDetailedResponse> patchScheduleVersion(@PathVariable Integer scheduleVersionId, @Valid @RequestBody PatchScheduleVersionRequest request){
        DbLogger.info(String.format("Received PATCH ScheduleVersion request for ID: %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.patchScheduleVersion(scheduleVersionId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{scheduleId}/CANCEL")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Cancel schedule",
            description = "Cancel booked schedule and all it's occurrences. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Canceled successfully")
    @ApiResponse(responseCode = "404", description = "Schedule not found")
    @ApiResponse(responseCode = "406", description = "Cancellation not possible because of schedule's status")
    @ApiResponse(responseCode = "406", description = "Schedule already held")
    @ApiResponse(responseCode = "406", description = "Cancellation not possible because has active occurrences")
    public ResponseEntity cancelSchedule(@PathVariable Integer scheduleId, @RequestBody @Size(min = 3, max = 200) String reason) {
        DbLogger.info("Received PUT Schedule request to CANCEL for ID: " + scheduleId);
        scheduleService.cancelSchedule(scheduleId, reason);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
