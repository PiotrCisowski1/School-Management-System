package com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity(name = "schedule_change_log")
public class ScheduleChangeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ScheduleEntity schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleChangeType changeType;

    @Column(nullable = false)
    private String fieldName;

    private String oldValue;

    @Column(nullable = false)
    private String newValue;

    @ManyToOne
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private UserEntity changedByUser;

    @Setter(AccessLevel.NONE)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(length = 200)
    private String reason;

    @Column(nullable = false)
    private boolean automaticChange;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "schedule_changelog_affected_users",
            joinColumns = @JoinColumn(name = "schedule_changelog_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<UserEntity> affectedUsers;

    @Override
    public String toString() {
        return "ScheduleChangeLogEntity{" +
                ", id=" + id +
                ", schedule=" + schedule +
                ", changeType=" + changeType +
                ", fieldName='" + fieldName + '\'' +
                ", newValue='" + newValue + '\'' +
                ", changedByUser=" + changedByUser.toString() +
                ", changedAt=" + changedAt +
                ", automaticChange=" + automaticChange +
                '}';
    }
}
