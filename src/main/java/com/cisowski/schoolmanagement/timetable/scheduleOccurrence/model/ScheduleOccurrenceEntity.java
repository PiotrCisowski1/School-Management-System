package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model;

import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity(name = "schedule_occurrences")
@Data
public class  ScheduleOccurrenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ScheduleEntity schedule;

    @Column(nullable = false)
    private LocalDateTime occurrenceDateTime;

    @Column(nullable = false)
    private LocalTime occurrenceEndTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OccurrenceStatus status;

    @OneToMany(mappedBy = "occurrence", fetch = FetchType.LAZY)
    private List<AttendanceEntity> attendances;

    @Override
    public String toString() {
        return "ScheduleOccurrenceEntity{" +
                "id=" + id +
                ", schedule ID=" + (schedule != null ? schedule.getId() : "empty schedule") +
                ", occurrenceDateTime=" + occurrenceDateTime +
                ", occurrenceEndTime=" + occurrenceEndTime +
                ", status=" + status +
                '}';
    }
}
