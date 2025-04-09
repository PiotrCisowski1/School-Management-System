package com.cisowski.schoolmanagement.schedule.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.sql.Time;

@Data
public class PatchScheduleRequest {
    private Integer subjectId;
    private Integer teacherId;
    private Integer classroomId;
    private Integer yearbookId;
    @Min(value = 1, message = "Day of week must be a Integer value between 1 and 7")
    @Max(value = 7, message = "Day of week must be a Integer value between 1 and 7")
    private Integer dayOfWeek;
    private Time startTime;
    private Time endTime;
    private ScheduleRecurrenceType recurrenceType;
}
