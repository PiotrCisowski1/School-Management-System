package com.cisowski.schoolmanagement.schedule.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final  ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<ScheduleDetailedResponse> addSchedule(@Valid @RequestBody AddScheduleRequest request, @RequestParam Integer scheduleVersionId){
        DbLogger.info("Received POST Schedule request for: " + request.toString());
        ScheduleDetailedResponse response = scheduleService.addSchedule(request, scheduleVersionId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity deleteSchedule(@RequestParam Integer scheduleId){
        DbLogger.info("Received DELETE Schedule request for ID: " + scheduleId);
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
