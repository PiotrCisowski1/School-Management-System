package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleSummaryResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ScheduleOccurrenceSummaryResponse {
    private Long id;
    private ScheduleSummaryResponse schedule;
    private LocalDateTime occurrenceDateTime;
    private LocalTime occurrenceEndTime;
    private OccurrenceStatus status;

    @Override
    public String toString() {
        return "ScheduleOccurrenceSummaryResponse{" +
                "id=" + id +
                ", occurrenceDateTime=" + occurrenceDateTime +
                ", occurrenceEndTime=" + occurrenceEndTime +
                ", status=" + status +
                '}';
    }
}
