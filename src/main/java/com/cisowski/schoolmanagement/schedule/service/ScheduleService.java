package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;

public interface ScheduleService {
    ScheduleDetailedResponse addSchedule(AddScheduleRequest request, Integer scheduleVersionId);
}
