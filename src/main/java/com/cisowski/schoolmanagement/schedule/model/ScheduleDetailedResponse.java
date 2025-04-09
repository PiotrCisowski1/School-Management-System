package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.classroom.model.ClassroomSummaryResponse;
import com.cisowski.schoolmanagement.subject.model.SubjectSummaryResponse;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherSummaryResponse;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;

@Data
public class ScheduleDetailedResponse {
    private Integer id;
    private SubjectSummaryResponse subject;
    private TeacherSummaryResponse teacher;
    private ClassroomSummaryResponse classroom;
    private DayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private ScheduleRecurrenceType recurrenceType;
}
