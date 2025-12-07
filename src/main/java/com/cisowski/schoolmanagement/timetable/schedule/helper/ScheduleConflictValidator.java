package com.cisowski.schoolmanagement.timetable.schedule.helper;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleRecurrenceType;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ScheduleConflictValidator {

    @Value("#{'${schedule.cancel.excluded.statuses}'.split(',')}")
    private List<ScheduleStatus> acceptableInitScheduleStatusList;

    public void checkIfScheduleAlreadyAppointed(ScheduleVersionEntity scheduleVersion, ScheduleEntity schedule){
        if(scheduleVersion == null || schedule == null)
            throw new IllegalArgumentException("Given ScheduleVersion or Schedule is not a valid object");
        DbLogger.info(String.format("Checking if Schedule with ID %s may clash with another Schedule", schedule.getId()));
        if(scheduleVersion.getSchedules() != null){
            Optional<ScheduleEntity> existingSchedule = scheduleVersion.getSchedules().stream()
                    .filter(Objects::nonNull)
                    .filter(existing -> !existing.getId().equals(schedule.getId()))
                    .filter(existing -> hasDayAndTimeConflict(existing, schedule))
                    .filter(existing -> hasDateOverlap(existing, schedule))
                    .findFirst();
            if(existingSchedule.isPresent())
                throw new SpecificationBrokenException(String.format(
                        "Schedule already appointed between %s and %s on %s",
                        existingSchedule.get().getStartTime().toString(),
                        existingSchedule.get().getEndTime().toString(),
                        existingSchedule.get().getDayOfWeek().toString()));
        }
    }

    private boolean hasDayAndTimeConflict(ScheduleEntity existing, ScheduleEntity newSchedule) {
        return existing.getDayOfWeek().equals(newSchedule.getDayOfWeek()) &&
                existing.getStartTime().isBefore(newSchedule.getEndTime()) &&
                existing.getEndTime().isAfter(newSchedule.getStartTime());
    }

    private boolean hasDateOverlap(ScheduleEntity existing, ScheduleEntity newSchedule) {
        LocalDate existingStart = existing.getEffectiveDate();
        LocalDate existingEnd = existing.getExpirationDate();
        LocalDate newStart = newSchedule.getEffectiveDate();
        LocalDate newEnd = newSchedule.getExpirationDate();

        boolean existingHasNoEnd = existingEnd == null;
        boolean newHasNoEnd = newEnd == null;

        return (existingHasNoEnd || !existingStart.isAfter(newEnd)) &&
                (newHasNoEnd || !newStart.isAfter(existingEnd));
    }

    public void checkScheduleCancellationPossible(ScheduleEntity schedule) {
        if (schedule == null)
            throw new IllegalArgumentException("To check expiration Schedule cannot be null");
        DbLogger.info("Checking if cancellation is possible for Schedule with ID " + schedule.getId());
        List<ScheduleStatus> cancelExcludedStatuses = acceptableInitScheduleStatusList;
        if(checkScheduleStatusInList(schedule, cancelExcludedStatuses))
            throw new SpecificationBrokenException(String.format(
                    "Schedule with ID %s, cannot be canceled because is in status: %s",
                    schedule.getId(),
                    schedule.getStatus().name()));
    }

    public boolean checkScheduleStatusInList(ScheduleEntity schedule, List<ScheduleStatus> excludedStatuses) {
        if(schedule == null)
            throw new IllegalArgumentException("Schedule cannot be null to check its status");
        if(excludedStatuses == null) {
            String logMessage = String.format("There is no excluded status list to check status of Schedule with ID %s", schedule.getId());
            DbLogger.error(logMessage);
            throw new IllegalArgumentException("Cannot check schedule status if exclusion list is not initialized");
        }
        return excludedStatuses.contains(schedule.getStatus());
    }

    public boolean isLessonAlreadyHeld(ScheduleEntity schedule) {
        if (schedule == null)
            throw new IllegalArgumentException("Schedule cannot be null to check if is already held");

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if (schedule.getRecurrenceType() == ScheduleRecurrenceType.NONE) {
            return isSingleLessonHeld(schedule, today, currentTime);
        }

        return isRecurringLessonHeld(schedule, today, currentTime);
    }

    private boolean isSingleLessonHeld(ScheduleEntity schedule, LocalDate today, LocalTime currentTime) {
        if (schedule.getEffectiveDate() == null) {
            return false;
        }

        if (schedule.getEffectiveDate().isBefore(today)) {
            return true;
        }

        return schedule.getEffectiveDate().equals(today) &&
                schedule.getEndTime() != null &&
                schedule.getEndTime().isBefore(currentTime);
    }

    private boolean isRecurringLessonHeld(ScheduleEntity schedule, LocalDate today, LocalTime currentTime) {
        if (schedule.getEffectiveDate() == null)
            return false;

        if (schedule.getExpirationDate().isBefore(today) && schedule.getEffectiveDate().isBefore(today))
            return true;

        if (schedule.getEffectiveDate().equals(today))
            return isTodayLessonHeld(schedule, currentTime);

        return false;
    }

    private boolean isTodayLessonHeld(ScheduleEntity schedule, LocalTime currentTime) {
        if (schedule.getDayOfWeek() != LocalDate.now().getDayOfWeek())
            return false;

        return schedule.getEndTime() != null && schedule.getEndTime().isBefore(currentTime);
    }
}
