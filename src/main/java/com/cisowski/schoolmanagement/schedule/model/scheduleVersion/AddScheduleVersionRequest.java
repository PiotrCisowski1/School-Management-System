package com.cisowski.schoolmanagement.schedule.model.scheduleVersion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddScheduleVersionRequest {
    @NotNull(message = "Cannot be null or empty")
    Integer yearbookId;

    String scheduleName;

    @NotNull(message = "Cannot be null or empty")
    boolean isActive;
}
