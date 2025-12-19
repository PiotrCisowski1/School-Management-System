package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.controller;

import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/occurrences")
public class ScheduleOccurrenceController {

    ResponseEntity<List<ScheduleOccurrenceSummaryResponse>> get
}
