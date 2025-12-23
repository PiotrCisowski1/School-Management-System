package com.cisowski.schoolmanagement.timetable.shared;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AttendanceInitTask {

    private final AttendanceService attendanceService;
    private final ScheduleOccurrenceService occurrenceService;

    @Scheduled(cron = "${attendance.init.task.cron}")
    public void initializeAttendanceForStartingSchedules() {
        DbLogger.info("Checking for any uninitialized ScheduleOccurrence and Attendance for Schedules, time: " + LocalDateTime.now());
        try {
            List<ScheduleOccurrenceEntity> uninitializedOccurrences = occurrenceService.fetchUninitializedOccurrencesForAttendance();
            initializeAttendance(uninitializedOccurrences);
        } catch (Exception ex) {
            DbLogger.error("Error while initializing attendance: " + ex.getMessage());
        }
    }

    @Transactional
    private void initializeAttendance(List<ScheduleOccurrenceEntity> scheduleOccurrences) {
        if(!CollectionUtils.isEmpty(scheduleOccurrences)) {
            scheduleOccurrences.stream()
                    .filter(Objects::nonNull)
                    .forEach(occurrence -> {
                        attendanceService.initializeAttendances(occurrence);
                        occurrenceService.changeOccurrenceStatus(occurrence, OccurrenceStatus.ONGOING);
                    });
        }
    }
}
