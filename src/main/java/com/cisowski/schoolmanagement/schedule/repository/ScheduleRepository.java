package com.cisowski.schoolmanagement.schedule.repository;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Integer> {

    @Query("SELECT s FROM ScheduleEntity s " +
            "WHERE s.classroom = :classroom " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND ((CAST(:startTime AS time) >= s.startTime AND CAST(:startTime AS time) < s.endTime) " +
            "OR (CAST(:endTime AS time) > s.startTime AND CAST(:endTime AS time) <= s.endTime) " +
            "OR (s.startTime >= CAST(:startTime AS time) AND s.startTime < CAST(:endTime AS time)))")
    Optional<ScheduleEntity> findByClassroomAndTimeRange(
            ClassroomEntity classroom,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime);

    List<ScheduleEntity> findByScheduleVersionIdAndDayOfWeek(Integer scheduleVersionId, DayOfWeek dayOfWeek);
}
