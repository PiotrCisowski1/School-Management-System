package com.cisowski.schoolmanagement.timetable.schedule.model;


import java.util.List;

public enum ScheduleRecurrenceType {
    NONE,
    WEEKLY,
    BIWEEKLY,
    MONTHLY;

    public static List<ScheduleRecurrenceType> getProperRecurrenceTypes() {
        return List.of(WEEKLY, BIWEEKLY, MONTHLY);
    }
}
