package com.cisowski.schoolmanagement.appConfig.model;

public enum AppConfigKeys {
    ATTENDANCE_INITIALIZATION_MIN_TIME("attendance.initialization.start.time"),
    SCHEDULE_INIT_SEARCH_TIME("schedule.init.search.time"),
    SCHEDULE_OCCURRENCE_COMPLETE_DAYS("schedule.occurrence.complete.days");

    private final String value;

    AppConfigKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
