package com.cisowski.schoolmanagement.timetable.shared;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;

import java.time.LocalDate;

public class ScheduleHelper {

    public static boolean isActiveSchedule(ScheduleEntity schedule) {
        return schedule.getEffectiveDate().isBefore(LocalDate.now())
                && schedule.getExpirationDate() == null || (schedule.getExpirationDate() != null && schedule.getExpirationDate().isAfter(LocalDate.now()))
                && !schedule.getStatus().equals(ScheduleStatus.CANCELLED) || !schedule.getStatus().equals(ScheduleStatus.DELETED);
    }
}
