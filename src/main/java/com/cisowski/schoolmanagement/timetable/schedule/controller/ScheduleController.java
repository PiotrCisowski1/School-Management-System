package com.cisowski.schoolmanagement.timetable.schedule.controller;

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
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleVersionService scheduleVersionService;
    private final TeacherSchedulePermissionHandler permissionHandler;

    @PostMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ScheduleDetailedResponse> addSchedule(@Valid @RequestBody AddScheduleRequest request, @PathVariable Integer scheduleVersionId){
        DbLogger.info("Received POST Schedule request for: " + request.toString());
        ScheduleDetailedResponse response = scheduleService.addSchedule(request, scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity deleteSchedule(@PathVariable Integer scheduleId){
        DbLogger.info("Received DELETE Schedule request for ID: " + scheduleId);
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{scheduleId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ScheduleDetailedResponse> patchSchedule(@PathVariable Integer scheduleId, @Valid @RequestBody PatchScheduleRequest request){
        DbLogger.info("Received PATCH Schedule request for ID: " + scheduleId);
        ScheduleDetailedResponse response = scheduleService.patchSchedule(scheduleId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{scheduleVersionId}/{scheduleId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE)
    public ResponseEntity<ScheduleDetailedResponse> getSchedule(@PathVariable Integer scheduleId, @PathVariable Integer scheduleVersionId){
        DbLogger.info("Received GET Schedule request for ID: " + scheduleId);
        ScheduleDetailedResponse response = scheduleService.getSchedule(scheduleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/{scheduleVersionId}/day/{dayOfWeek}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE)
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
    public ResponseEntity<ScheduleVersionDetailedResponse> createScheduleVersion(@RequestBody @Valid AddScheduleVersionRequest request){
        DbLogger.info(String.format(
                "Received POST ScheduleVersion request (named: %s) for Yearbook with ID %s, isActive - %s",
                request.getScheduleName(), request.getYearbookId(), request.isActive()));
        ScheduleVersionDetailedResponse response = scheduleVersionService.createScheduleVersion(request.getYearbookId(), request.getScheduleName(), request.isActive());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/version/{scheduleVersionId}/clone")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ScheduleVersionDetailedResponse> cloneScheduleVersion(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received POST (clone) ScheduleVersion request for ScheduleVersion with ID %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.cloneScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/version/yearbook/{yearbookId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Collection<ScheduleVersionSummaryResponse>> getScheduleVersionsForYearbook(@PathVariable Integer yearbookId){
        DbLogger.info(String.format("Received GET all ScheduleVersions for Yearbook with ID: %s", yearbookId));
        Collection<ScheduleVersionSummaryResponse> response = scheduleVersionService.getScheduleVersionsForYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/{scheduleVersionId}")
    @RequiresPermission(action = ResourceActionType.READ, resource = ResourceType.SCHEDULE_VERSION)
    public ResponseEntity<ScheduleVersionDetailedResponse> getScheduleVersionsForId(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received GET ScheduleVersions for ID: %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.getScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity deleteScheduleVersion(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received DELETE ScheduleVersion request for ID: %s", scheduleVersionId));
        scheduleVersionService.deleteScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/version/{scheduleVersionId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ScheduleVersionDetailedResponse> patchScheduleVersion(@PathVariable Integer scheduleVersionId, @Valid @RequestBody PatchScheduleVersionRequest request){
        DbLogger.info(String.format("Received PATCH ScheduleVersion request for ID: %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.patchScheduleVersion(scheduleVersionId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{scheduleId}/CANCEL")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity cancelSchedule(@PathVariable Integer scheduleId, @RequestBody @Size(min = 3, max = 200) String reason) {
        DbLogger.info("Received PUT Schedule request to CANCEL for ID: " + scheduleId);
        scheduleService.cancelSchedule(scheduleId, reason);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
