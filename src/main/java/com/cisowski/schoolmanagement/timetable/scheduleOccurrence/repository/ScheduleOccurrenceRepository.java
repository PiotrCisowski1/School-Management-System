package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.repository;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ScheduleOccurrenceRepository extends JpaRepository<ScheduleOccurrenceEntity, Long> {
    boolean existsByScheduleAndOccurrenceDateTime(ScheduleEntity schedule, LocalDateTime occurrenceDateTime);

    @Query("SELECT so FROM schedule_occurrences so " +
            "WHERE so.status = :scheduledStatus " +
            "AND so.occurrenceDateTime > :timeNow " +
            "AND so.occurrenceDateTime <= :timeThreshold")
    List<ScheduleOccurrenceEntity> findOccurrencesReadyForInitialization(
            @Param("scheduledStatus") OccurrenceStatus scheduledStatus,
            @Param("timeNow") LocalDateTime timeNow,
            @Param("timeThreshold") LocalDateTime timeThreshold
    );

    Set<ScheduleOccurrenceEntity> findByScheduleInAndOccurrenceDateTimeBetween(List<ScheduleEntity> schedules, LocalDateTime thresholdStartTime, LocalDateTime thresholdEndTime);

    List<ScheduleOccurrenceEntity> findByStatusInAndOccurrenceDateTimeBefore(List<OccurrenceStatus> statuses, LocalDateTime occurrenceExpirationTime);

    boolean existsByScheduleAndStatusIn(ScheduleEntity schedule, List<OccurrenceStatus> statuses);

    List<ScheduleOccurrenceEntity> findAllBySchedule(ScheduleEntity schedule);
}
