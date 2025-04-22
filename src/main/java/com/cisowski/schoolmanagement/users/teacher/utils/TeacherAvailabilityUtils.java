package com.cisowski.schoolmanagement.users.teacher.utils;

import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalTime;

@UtilityClass
public class TeacherAvailabilityUtils {

    public boolean isAvailable(TeacherAvailabilityEntity availability, DayOfWeek day, LocalTime startTime, LocalTime endTime){
        return availability.getDayOfWeek().equals(day) &&
                availability.getStartTime().isBefore(startTime) || availability.getStartTime().equals(startTime) &&
                availability.getEndTime().isAfter(endTime) || availability.getEndTime().equals(endTime);
    }
}
