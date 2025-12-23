package com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Collection;

@Data
public class ScheduleVersionDetailedResponse {
    private Integer id;
    private String name;
    private ZonedDateTime createDate;
    private boolean isActive;
    private YearbookSummaryResponse yearbook;
    private Collection<ScheduleDetailedResponse> schedules;
}
