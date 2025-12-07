package com.cisowski.schoolmanagement.appConfig.model;

public enum AppConfigKeys {
    ATTENDANCE_INITIALIZATION_MIN_TIME("attendance.initialization.start.time");

    private final String value;

    AppConfigKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
