package com.cisowski.schoolmanagement.timetable.schedule.service;

import com.cisowski.schoolmanagement.timetable.schedule.model.*;

import java.util.List;

public interface ScheduleService {
    ScheduleDetailedResponse addSchedule(AddScheduleRequest request, Integer scheduleVersionId);
    void deleteSchedule(Integer scheduleId);
    ScheduleDetailedResponse patchSchedule(Integer scheduleId, PatchScheduleRequest request);
    ScheduleDetailedResponse getSchedule(Integer scheduleId);
    List<ScheduleSummaryResponse> getScheduleByDayOfWeek(Integer scheduleVersionId, Integer dayOfWeek, boolean needsFiltering, Integer userId);
    List<ScheduleEntity> fetchSchedulesByClassroomId(Integer classroomId);
    void cancelSchedule(Integer scheduleId, String reason);
    List<ScheduleEntity> findUninitializedSchedules();
    void changeStatusToOngoing(ScheduleEntity schedule);
    ScheduleEntity fetchSchedule(Integer schedule);
}
