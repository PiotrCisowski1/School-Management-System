package com.cisowski.schoolmanagement.timetable.schedule.service;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangeType;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangelogDto;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleStatusService {

    private final ScheduleChangelogService scheduleChangelogService;
    private final ScheduleOccurrenceService occurrenceService;

    public List<UserEntity> createListWithUsersAffectedByChange(ScheduleEntity schedule) {
        if (schedule == null)
            return java.util.Collections.emptyList();
        List<UserEntity> usersAffectedBySchedule = new ArrayList<>();
        usersAffectedBySchedule.add(schedule.getTeacher());
        Collection<StudentEntity> studentsAttendingSchedule = schedule.getScheduleVersion().getYearbook().getStudentsInYearbook();
        usersAffectedBySchedule.addAll(studentsAttendingSchedule);

        List<ParentEntity> parents = studentsAttendingSchedule.stream()
                .filter(Objects::nonNull)
                .map(StudentEntity::getParents)
                .filter(studentParents -> !Collections.isEmpty(studentParents))
                .flatMap(Collection::stream)
                .distinct()
                .toList();
        usersAffectedBySchedule.addAll(parents);

        return usersAffectedBySchedule;
    }

    public void changeStatusToDeleted(ScheduleEntity schedule) {
        if (schedule == null)
            return;
        DbLogger.info(String.format("Changing Schedule with ID %s status to DELETED", schedule.getId()));
        schedule.setStatus(ScheduleStatus.DELETED);

        List<UserEntity> usersAffected = createListWithUsersAffectedByChange(schedule);
        ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                schedule,
                ScheduleChangeType.DELETED,
                "entity",
                null,
                "entity",
                "Schedule deleted permanently",
                false,
                usersAffected);
        scheduleChangelogService.logChange(changelogDto);
    }

    public boolean isAlreadyDeleted(ScheduleEntity schedule) {
        return schedule != null && schedule.getStatus().equals(ScheduleStatus.DELETED);
    }

    public List<ScheduleEntity> filterDeletedSchedules(List<ScheduleEntity> schedules) {
        if (Collections.isEmpty(schedules))
            return schedules;

        return schedules.stream()
                .filter(Objects::nonNull)
                .filter(schedule -> !isAlreadyDeleted(schedule))
                .collect(Collectors.toList());
    }

    public void markSchedulesAsDeleted(Collection<ScheduleEntity> schedules) {
        if (CollectionUtils.isEmpty(schedules))
            return;

        schedules.stream()
                .filter(Objects::nonNull)
                .filter(schedule -> !isAlreadyDeleted(schedule))
                .forEach(this::changeStatusToDeleted);
    }

    public void changeStatusToCanceled(ScheduleEntity schedule, String reason) {
        if (schedule == null)
            return;
        String oldStatus = schedule.getStatus().name();
        DbLogger.info(String.format("Changing Schedule with ID %s status to CANCELED", schedule.getId()));
        schedule.setStatus(ScheduleStatus.CANCELLED);

        List<UserEntity> usersAffected = createListWithUsersAffectedByChange(schedule);
        ScheduleChangelogDto changelogDto = new ScheduleChangelogDto(
                schedule,
                ScheduleChangeType.CANCELLED,
                "schedule status",
                oldStatus,
                ScheduleChangeType.CANCELLED.name(),
                reason,
                false,
                usersAffected);
        scheduleChangelogService.logChange(changelogDto);
    }

    public void cancelOccurrencesForSchedule(ScheduleEntity schedule) {
        if(schedule == null)
            return;
        DbLogger.info("Trying to cancel ScheduleOccurrences for Schedule with ID: " + schedule.getId());
        List<ScheduleOccurrenceEntity> occurrences = occurrenceService.fetchOccurrencesForSchedule(schedule);
        occurrenceService.changeOccurrencesStatus(occurrences, OccurrenceStatus.CANCELLED);
    }
}
