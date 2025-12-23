package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.task;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus.ONGOING;

@Component
@RequiredArgsConstructor
public class ScheduleOccurrenceCompleteTask {

    private final ScheduleOccurrenceService occurrenceService;
    private final AppConfigService configService;

    @Scheduled(cron = "${schedule.occurrence.complete.cron}")
    public void completeScheduleOccurrences() {
        try {
            AppConfigDetailedResponse config = configService.getConfigByKey(AppConfigKeys.SCHEDULE_OCCURRENCE_COMPLETE_DAYS.getValue());
            Integer configDays = Integer.parseInt(config.getValue());
            List<ScheduleOccurrenceEntity> occurrencesToComplete = occurrenceService.findOccurrencesReadyToComplete(Collections.singletonList(ONGOING), configDays);
            occurrenceService.changeOccurrencesStatus(occurrencesToComplete, OccurrenceStatus.COMPLETED);
        } catch (Exception ex) {
            DbLogger.error("Error occurred while completing ScheduleOccurrences: " + ex.getMessage());
        }
    }

}
