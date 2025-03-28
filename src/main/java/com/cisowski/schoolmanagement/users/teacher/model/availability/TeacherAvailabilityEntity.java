package com.cisowski.schoolmanagement.users.teacher.model.availability;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;

@Data
@Entity
@Table(name = "teachers_availability")
public class TeacherAvailabilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherEntity teacher;
    private DayOfWeek dayOfWeek;
    private Time startTime;
    private Time endTime;
    private boolean isAvailable;
    private String notes;
}
