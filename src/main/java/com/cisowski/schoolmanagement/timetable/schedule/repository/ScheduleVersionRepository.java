package com.cisowski.schoolmanagement.timetable.schedule.repository;

import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleVersionRepository extends JpaRepository<ScheduleVersionEntity, Integer> {
    Optional<ScheduleVersionEntity> findByIsActiveTrueAndYearbookId(Integer yearbookId);
    List<ScheduleVersionEntity> findByYearbookId(Integer yearbookId);
}
