package com.cisowski.schoolmanagement.schedule.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleSummaryResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.AddScheduleVersionRequest;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleVersionService scheduleVersionService;

    @PostMapping("/version/{scheduleVersionId}")
    public ResponseEntity<ScheduleDetailedResponse> addSchedule(@Valid @RequestBody AddScheduleRequest request, @PathVariable Integer scheduleVersionId){
        DbLogger.info("Received POST Schedule request for: " + request.toString());
        ScheduleDetailedResponse response = scheduleService.addSchedule(request, scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity deleteSchedule(@PathVariable Integer scheduleId){
        DbLogger.info("Received DELETE Schedule request for ID: " + scheduleId);
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailedResponse> patchSchedule(@PathVariable Integer scheduleId, @Valid @RequestBody PatchScheduleRequest request){
        DbLogger.info("Received PATCH Schedule request for ID: " + scheduleId);
        ScheduleDetailedResponse response = scheduleService.patchSchedule(scheduleId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleDetailedResponse> getSchedule(@PathVariable Integer scheduleId){
        DbLogger.info("Received GET Schedule request for ID: " + scheduleId);
        ScheduleDetailedResponse response = scheduleService.getSchedule(scheduleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/{scheduleVersionId}/day/{dayOfWeek}")
    public ResponseEntity<List<ScheduleSummaryResponse>> getSchedule(@PathVariable Integer scheduleVersionId, @PathVariable Integer dayOfWeek){
        DbLogger.info(String.format("Received GET Schedule request for day: %s in ScheduleVersion with ID: %s ", dayOfWeek, scheduleVersionId));
        List<ScheduleSummaryResponse> response = scheduleService.getScheduleByDayOfWeek(scheduleVersionId, dayOfWeek);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/version")
    public ResponseEntity<ScheduleVersionDetailedResponse> createScheduleVersion(@RequestBody @Valid AddScheduleVersionRequest request){
        DbLogger.info(String.format(
                "Received POST ScheduleVersion request (named: %s) for Yearbook with ID %s, isActive - %s",
                request.getScheduleName(), request.getYearbookId(), request.isActive()));
        ScheduleVersionDetailedResponse response = scheduleVersionService.createScheduleVersion(request.getYearbookId(), request.getScheduleName(), request.isActive());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/version/{scheduleVersionId}/clone")
    public ResponseEntity<ScheduleVersionDetailedResponse> cloneScheduleVersion(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received POST (clone) ScheduleVersion request for ScheduleVersion with ID %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.cloneScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/version/yearbook/{yearbookId}")
    public ResponseEntity<Collection<ScheduleVersionSummaryResponse>> getScheduleVersionsForYearbook(@PathVariable Integer yearbookId){
        DbLogger.info(String.format("Received GET all ScheduleVersions for Yearbook with ID: %s", yearbookId));
        Collection<ScheduleVersionSummaryResponse> response = scheduleVersionService.getScheduleVersionsForYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/version/{scheduleVersionId}")
    public ResponseEntity<ScheduleVersionDetailedResponse> getScheduleVersionsForId(@PathVariable Integer scheduleVersionId){
        DbLogger.info(String.format("Received GET ScheduleVersions for ID: %s", scheduleVersionId));
        ScheduleVersionDetailedResponse response = scheduleVersionService.getScheduleVersion(scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
