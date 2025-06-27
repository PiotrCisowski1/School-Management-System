package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;

import java.util.Collection;

public interface ScheduleVersionService {
    Collection<ScheduleVersionSummaryResponse> getScheduleVersionsForYearbook(Integer yearbookId);
    ScheduleVersionDetailedResponse getScheduleVersion(Integer scheduleVersionId);
    ScheduleVersionDetailedResponse createScheduleVersion(Integer yearbookId, String scheduleName, boolean isActive);
    ScheduleVersionDetailedResponse cloneScheduleVersion(Integer scheduleVersionId);
    ScheduleVersionEntity fetchScheduleVersion(Integer scheduleVersionId);
    void deleteScheduleVersion(Integer scheduleVersionId);
}
