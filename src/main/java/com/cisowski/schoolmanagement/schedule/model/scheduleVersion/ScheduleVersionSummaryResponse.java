package com.cisowski.schoolmanagement.schedule.model.scheduleVersion;

import lombok.Data;

@Data
public class ScheduleVersionSummaryResponse {
    private Integer id;
    private String name;
    private boolean isActive;
}
