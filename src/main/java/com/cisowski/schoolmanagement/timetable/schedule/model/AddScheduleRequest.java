package com.cisowski.schoolmanagement.timetable.schedule.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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

    @FutureOrPresent
    private LocalDate effectiveDate;

    private LocalDate expirationDate;

    @AssertTrue(message = "Start time must be before end time")
    public boolean isValidTimeRange(){
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }

    @AssertTrue(message = "Expiration date must be in future")
    public boolean isValidExpirationDate() {
        if(expirationDate == null)
            return true;
        return expirationDate.isAfter(LocalDate.now());
    }

    @AssertTrue(message = "Effective date must be before expiration date")
    public boolean isEffectiveDateBeforeExpirationDate() {
        if(effectiveDate == null || expirationDate == null)
            return true;
        return effectiveDate.isBefore(expirationDate);
    }

    @AssertTrue(message = "Expiration date cannot be null if schedule is not recurrent")
    public boolean isExpirationDateIfRecurrenceNone() {
        if(recurrenceType.equals(ScheduleRecurrenceType.NONE))
            return expirationDate != null;
        return true;
    }

    @AssertTrue(message = "Effective date cannot be empty")
    public boolean isEffectiveDateNotNull() {
        return effectiveDate != null;
    }

    public LocalTime getEndTime() {
        if(endTime != null)
            return endTime.truncatedTo(ChronoUnit.SECONDS);
        return null;
    }

    public LocalTime getStartTime() {
        if(startTime != null)
            return startTime.truncatedTo(ChronoUnit.SECONDS);
        return null;
    }

    @Override
    public String toString() {
        return "AddScheduleRequest{" +
                "subjectId=" + subjectId +
                ", teacherId=" + teacherId +
                ", classroomId=" + classroomId +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime.toString() +
                ", endTime=" + endTime.toString() +
                ", recurrenceType=" + recurrenceType +
                ", effectiveDate=" + effectiveDate +
                ", expirationDate=" + (expirationDate != null ? expirationDate : "permanent") +
                '}';
    }
}
