package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;

import java.util.List;

public interface ScheduleOccurrenceService {
    ScheduleOccurrenceEntity fetchScheduleOccurrence(Long scheduleOccurrenceId);
    void initializeScheduleOccurrence(ScheduleEntity schedule, Integer minInitDays);
    List<ScheduleOccurrenceEntity> fetchUninitializedOccurrencesForAttendance();
    void changeOccurrenceStatus(ScheduleOccurrenceEntity occurrence, OccurrenceStatus occurrenceStatus);
    List<ScheduleOccurrenceSummaryResponse> getOccurrencesForYearbook(Integer yearbookId);
    ScheduleOccurrenceSummaryResponse getOccurrenceById(Long scheduleOccurrenceId);
    ScheduleOccurrenceEntity fetchOccurrence(Long scheduleOccurrenceId);
    List<ScheduleOccurrenceEntity> findOccurrencesReadyToComplete(List<OccurrenceStatus> acceptableStatuses, Integer daysGap);
    void changeOccurrencesStatus(List<ScheduleOccurrenceEntity> occurrences, OccurrenceStatus targetStatus);
}
