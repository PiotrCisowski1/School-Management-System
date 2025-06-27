package com.cisowski.schoolmanagement.schedule.model.scheduleVersion;

import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "schedule_version")
@NoArgsConstructor
public class ScheduleVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private ZonedDateTime createDate = ZonedDateTime.now();

    private boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "yearbook_id", nullable = false)
    private YearbookEntity yearbook;

    @OneToMany(mappedBy = "scheduleVersion", cascade = CascadeType.ALL)
    private Collection<ScheduleEntity> schedules = Collections.emptyList();

    public ScheduleVersionEntity(ScheduleVersionEntity entity) {
        List<ScheduleEntity> copiedSchedules = new ArrayList<>();
        entity.schedules.forEach(schedule ->
            copiedSchedules.add(new ScheduleEntity(schedule, this)));

        this.name = entity.name != null ? entity.name.concat(" (copy)") : "Copy";
        this.createDate = ZonedDateTime.now();
        this.isActive = false;
        this.yearbook = entity.yearbook;
        this.schedules = copiedSchedules;
    }

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
