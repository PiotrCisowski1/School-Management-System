package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "schedules")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_version_id", nullable = false)
    private ScheduleVersionEntity scheduleVersion;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectEntity subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherEntity teacher;

    @ManyToOne
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomEntity classroom;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private ScheduleRecurrenceType recurrenceType = ScheduleRecurrenceType.NONE;

    @Override
    public String toString() {
        return "ScheduleEntity{" +
                "id=" + id +
                ", scheduleVersion=" + scheduleVersion.getId() +
                ", subject=" + subject.getId() +
                ", teacher=" + teacher.getId() +
                ", classroom=" + classroom.getId() +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", recurrenceType=" + recurrenceType +
                '}';
    }
}
