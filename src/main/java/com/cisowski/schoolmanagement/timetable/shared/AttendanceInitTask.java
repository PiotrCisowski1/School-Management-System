package com.cisowski.schoolmanagement.timetable.shared;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceService;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
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
    private final ScheduleService scheduleService;

    @Scheduled(cron = "${attendance.init.task.cron}")
    public void initializeAttendanceForStartingSchedules() {
        DbLogger.info("Checking for any uninitialized attendance for schedules, time: " + LocalDateTime.now());
        try {
            List<ScheduleEntity> uninitializedSchedules = scheduleService.findUninitializedSchedules();
            initializeAttendance(uninitializedSchedules);
        } catch (Exception ex) {
            DbLogger.error("Error while initializing attendance: " + ex.getMessage());
        }
    }

    @Transactional
    private void initializeAttendance(List<ScheduleEntity> schedules) {
        if(!CollectionUtils.isEmpty(schedules)) {
            schedules.stream()
                    .filter(Objects::nonNull)
                    .forEach(schedule -> {
                        attendanceService.initializeAttendances(schedule);
                        scheduleService.changeStatusToOngoing(schedule);
                    });
        }
    }
}
