package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.task;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleOccurrenceInitTask {

    private final ScheduleService scheduleService;
    private final AppConfigService appConfig;
    private final ScheduleOccurrenceService occurrenceService;

    @Scheduled(cron = "${schedule.occurrence.init.cron}")
    public void initializeScheduleOccurrence() {
        try{
            AppConfigDetailedResponse config = appConfig.getConfigByKey(AppConfigKeys.SCHEDULE_INIT_SEARCH_TIME.getValue());
            Integer daysGap = Integer.parseInt(config.getValue());
            DbLogger.info(String.format("Searching for any uninitialized ScheduleOccurrence within next % days", daysGap));
            List<ScheduleEntity> uninitializedSchedules = scheduleService.findUninitializedSchedules(daysGap);
            initializeScheduleOccurrences(uninitializedSchedules, daysGap);
        } catch (Exception ex) {
            DbLogger.error("Error occurred while initializing ScheduleOccurrences: " + ex.getMessage());
        }
    }


    private void initializeScheduleOccurrences(List<ScheduleEntity> schedules, Integer daysGap) {
        if(!CollectionUtils.isEmpty(schedules)) {
            schedules.forEach(schedule -> occurrenceService.initializeScheduleOccurrence(schedule, daysGap));
        }
    }
}
