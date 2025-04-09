package com.cisowski.schoolmanagement.schedule.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Time;

@Data
public class AddScheduleRequest {

    @Min(value = 1, message = "Schedule version ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Schedule version ID must be valid Integer value")
    private Integer scheduleVersionId;

    @Min(value = 1, message = "Subject ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Subject ID must be valid Integer value")
    private Integer subjectId;

    @Min(value = 1, message = "Teacher ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Teacher ID must be valid Integer value")
    private Integer teacherId;

    @Min(value = 1, message = "Classroom ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Classroom ID must be valid Integer value")
    private Integer classroomId;

    @Min(value = 1, message = "Yearbook ID must be a valid Integer values and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Yearbook ID must be valid Integer value")
    private Integer yearbookId;

    @Min(value = 1, message = "Day of week must be a Integer value between 1 and 7")
    @Max(value = 7, message = "Day of week must be a Integer value between 1 and 7")
    private Integer dayOfWeek;

    @NotNull(message = "Cannot be null or empty")
    private Time startTime;

    @NotNull(message = "Cannot be null or empty")
    private Time endTime;

    @NotNull(message = "Invalid Recurrence type, expected values: WEEKLY, BIWEEKLY, MONTHLY or NONE")
    private ScheduleRecurrenceType recurrenceType;

    @AssertTrue(message = "Start time must be before end time")
    public boolean isValidTimeRange(){
        return startTime != null && endTime != null && startTime.compareTo(endTime) < 0;
    }


}
