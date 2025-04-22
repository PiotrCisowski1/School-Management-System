package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionSummaryResponse;

import java.util.Collection;

public interface ScheduleVersionService {
    Collection<ScheduleVersionSummaryResponse> getScheduleVersionsForYearbook(Integer yearbookId);
    ScheduleVersionDetailedResponse createScheduleVersion(Integer yearbookId);
    ScheduleVersionDetailedResponse cloneScheduleVersion(Integer scheduleVersionId);

    ScheduleVersionEntity fetchScheduleVersion(Integer scheduleVersionId);
}
