package com.cisowski.schoolmanagement.schedule.model;

import com.cisowski.schoolmanagement.yearbook.model.YearbookDetailedResponse;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Collection;

@Data
public class ScheduleVersionDetailedResponse {
    private Integer id;
    private String name;
    private Timestamp createDate;
    private boolean isActive;
    private YearbookDetailedResponse yearbook;
    private Collection<ScheduleDetailedResponse> schedules;
}
