package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Collection;

@Data
@Entity
@Table(name = "schedule_version")
public class ScheduleVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private ZonedDateTime createDate;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "yearbook_id", nullable = false)
    private YearbookEntity yearbook;

    @OneToMany(mappedBy = "scheduleVersion", cascade = CascadeType.ALL)
    private Collection<ScheduleEntity> schedules;

    @Override
    public String toString() {
        return "ScheduleVersionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createDate=" + createDate +
                ", isActive=" + isActive +
                ", yearbook=" + yearbook.getId() +
                ", schedules=" + schedules.size() +
                '}';
    }
}
