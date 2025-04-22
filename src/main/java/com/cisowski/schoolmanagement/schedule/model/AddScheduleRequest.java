package com.cisowski.schoolmanagement.schedule.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalTime;

@Data
public class AddScheduleRequest {

    @Min(value = 1, message = "Subject ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Subject ID must be valid Integer value")
    private Integer subjectId;

    @Min(value = 1, message = "Teacher ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Teacher ID must be valid Integer value")
    private Integer teacherId;

    @Min(value = 1, message = "Classroom ID must be a valid Integer value and at least 1")
    @Max(value = Integer.MAX_VALUE, message = "Classroom ID must be valid Integer value")
    private Integer classroomId;

    @Min(value = 1, message = "Day of week must be a Integer value between 1 and 7")
    @Max(value = 7, message = "Day of week must be a Integer value between 1 and 7")
    private Integer dayOfWeek;

    @NotNull(message = "Cannot be null or empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "Cannot be null or empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Invalid Recurrence type, expected values: WEEKLY, BIWEEKLY, MONTHLY or NONE")
    private ScheduleRecurrenceType recurrenceType;

    @AssertTrue(message = "Start time must be before end time")
    public boolean isValidTimeRange(){
        return startTime != null && endTime != null && startTime.compareTo(endTime) < 0;
    }

    @Override
    public String toString() {
        return "AddScheduleRequest{" +
                "subjectId=" + subjectId +
                ", teacherId=" + teacherId +
                ", classroomId=" + classroomId +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", recurrenceType=" + recurrenceType +
                '}';
    }
}
