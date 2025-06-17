package com.cisowski.schoolmanagement.schedule.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleSummaryResponse;
import com.cisowski.schoolmanagement.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final  ScheduleService scheduleService;

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
}
