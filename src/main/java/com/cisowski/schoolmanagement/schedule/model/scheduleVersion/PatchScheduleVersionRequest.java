package com.cisowski.schoolmanagement.schedule.model.scheduleVersion;

import lombok.Data;

@Data
public class PatchScheduleVersionRequest {
    private String name;
    private boolean isActive;
    private Integer yearbookId;

    @Override
    public String toString() {
        return "PatchScheduleVersionRequest{" +
                ", name='" + name + '\'' +
                "isActive=" + isActive + '\'' +
                ", yearbookId=" + yearbookId +
                '}';
    }
}
