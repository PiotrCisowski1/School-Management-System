package com.cisowski.schoolmanagement.timetable.schedule.model;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table(name = "schedules")
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    private LocalDate expirationDate;

    public ScheduleEntity(ScheduleEntity entity, ScheduleVersionEntity scheduleVersion) {
         this.scheduleVersion = scheduleVersion;
         this.subject = entity.getSubject();
         this.teacher = entity.getTeacher();
         this.classroom = entity.getClassroom();
         this.dayOfWeek = entity.getDayOfWeek();
         this.startTime = entity.getStartTime();
         this.endTime = entity.getEndTime();
         this.recurrenceType = entity.getRecurrenceType();
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
        return "ScheduleEntity{" +
                "id=" + id +
                ", scheduleVersion=" + scheduleVersion.getId() +
                ", subject=" + subject.getId() +
                ", teacher=" + teacher.getId() +
                ", classroom=" + classroom.getId() +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime.toString() +
                ", endTime=" + endTime.toString() +
                ", recurrenceType=" + recurrenceType +
                ", effectiveDate=" + effectiveDate +
                ", expirationDate=" + (expirationDate != null ? expirationDate : "permanent") +
                '}';
    }
}
