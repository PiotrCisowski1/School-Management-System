package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.classroom.model.ClassroomSummaryResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.subject.model.SubjectSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
public class ScheduleDetailedResponse {
    private Integer id;
    private SubjectSummaryResponse subject;
    private TeacherSummaryResponse teacher;
    private ClassroomSummaryResponse classroom;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private ScheduleRecurrenceType recurrenceType;
    private ScheduleVersionSummaryResponse scheduleVersion;
    private ScheduleStatus status;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;

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
        return "ScheduleDetailedResponse{" +
                "id=" + id +
                ", subject=" + subject.getId() +
                ", teacher=" + teacher.getId() +
                ", classroom=" + classroom.getId() +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", recurrenceType=" + recurrenceType +
                ", scheduleVersion=" + scheduleVersion.getId() +
                ", effectiveDate=" + effectiveDate +
                ", expirationDate=" + (expirationDate != null ? expirationDate : "permanent") +
                '}';
    }
}
