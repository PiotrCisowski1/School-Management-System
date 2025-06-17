package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.PatchScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleSummaryResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleDetailedResponse addSchedule(AddScheduleRequest request, Integer scheduleVersionId);
    void deleteSchedule(Integer scheduleId);
    ScheduleDetailedResponse patchSchedule(Integer scheduleId, PatchScheduleRequest request);
    ScheduleDetailedResponse getSchedule(Integer scheduleId);
    List<ScheduleSummaryResponse> getScheduleByDayOfWeek(Integer scheduleVersionId, Integer dayOfWeek);
}
