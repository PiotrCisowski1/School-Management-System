package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/occurrences")
@RequiredArgsConstructor
public class ScheduleOccurrenceController {

    private final ScheduleOccurrenceService occurrenceService;

    ResponseEntity<List<ScheduleOccurrenceSummaryResponse>> getOccurrencesForYearbook(Integer yearbookId) {
        DbLogger.info("Received Occurrence GET request for Yearbook with ID " + yearbookId);
        List<ScheduleOccurrenceSummaryResponse> response = occurrenceService.getOccurrencesForYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
