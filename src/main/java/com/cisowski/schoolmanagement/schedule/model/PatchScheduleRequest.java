package com.cisowski.schoolmanagement.schedule.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
public class PatchScheduleRequest {
    private Integer subjectId;
    private Integer teacherId;
    private Integer classroomId;
    private Integer yearbookId;
    @Min(value = 1, message = "Day of week must be a Integer value between 1 and 7")
    @Max(value = 7, message = "Day of week must be a Integer value between 1 and 7")
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    @Valid
    private ScheduleRecurrenceType recurrenceType;
    private String updateReason;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;

    @AssertTrue(message = "Expiration date must be in future")
    public boolean isValidExpirationDate() {
        if(expirationDate == null)
            return true;
        return expirationDate.isAfter(LocalDate.now());
    }

    @AssertTrue(message = "Effective date must be in future")
    public boolean isValidEffectiveDate() {
        if(effectiveDate == null)
            return true;
        return effectiveDate.isAfter(LocalDate.now()) || effectiveDate.isEqual(LocalDate.now());
    }

    @AssertTrue(message = "Effective date must be before expiration date")
    public boolean isEffectiveDateBeforeExpirationDate() {
        if(effectiveDate == null || expirationDate == null)
            return true;
        return effectiveDate.isBefore(expirationDate);
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
        return "PatchScheduleRequest{" +
                "subjectId=" + subjectId +
                ", teacherId=" + teacherId +
                ", classroomId=" + classroomId +
                ", yearbookId=" + yearbookId +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", recurrenceType=" + recurrenceType +
                ", effectiveDate=" + effectiveDate +
                ", expirationDate=" + (expirationDate != null ? expirationDate : "permanent") +
                '}';
    }
}
