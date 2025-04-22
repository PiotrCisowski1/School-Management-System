package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.classroom.model.ClassroomSummaryResponse;
import com.cisowski.schoolmanagement.subject.model.SubjectSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;

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
                '}';
    }
}
