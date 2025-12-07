package com.cisowski.schoolmanagement.timetable.shared;

import java.time.LocalTime;

public class TimetableHelper {

    public static boolean isStartWithinTimeWindow(LocalTime currentTime, LocalTime startTime, int minutesBefore) {
        LocalTime windowStart = startTime.minusMinutes(minutesBefore);
        LocalTime windowEnd = startTime;

        if (!windowStart.isAfter(windowEnd)) {
            return !currentTime.isBefore(windowStart) && !currentTime.isAfter(windowEnd);
        }
        else {
            return !currentTime.isBefore(windowStart) || !currentTime.isAfter(windowEnd);
        }
}
}
