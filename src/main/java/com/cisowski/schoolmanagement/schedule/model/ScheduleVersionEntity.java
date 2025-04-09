package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Collection;

@Data
@Entity
@Table(name = "schedule_version")
public class ScheduleVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Timestamp createDate;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "yearbook_id", nullable = false)
    private YearbookEntity yearbook;

    @OneToMany(mappedBy = "scheduleVersion", cascade = CascadeType.ALL)
    private Collection<ScheduleEntity> schedules;

}
