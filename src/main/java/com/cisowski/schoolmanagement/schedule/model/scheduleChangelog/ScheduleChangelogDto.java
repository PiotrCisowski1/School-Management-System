package com.cisowski.schoolmanagement.schedule.model.scheduleChangelog;

import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleChangelogDto {

    @NotNull
    private ScheduleEntity schedule;

    @NotNull
    private ScheduleChangeType changeType;

    @NotNull
    private String fieldName;

    private String oldValue;

    @NotNull
    @NotEmpty
    private String newValue;

    private String reason;

    @NotNull
    private boolean automaticChange;

    private List<UserEntity> affectedUsers;

    @Override
    public String toString() {
        return "ScheduleChangelogDto{" +
                ", scheduleId=" + schedule.getId() +
                ", changeType=" + changeType +
                ", fieldName='" + fieldName + '\'' +
                ", newValue='" + newValue + '\'' +
                ", automaticChange=" + automaticChange +
                '}';
    }
}
