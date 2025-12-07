package com.cisowski.schoolmanagement.timetable.schedule.repository;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleChangelogRepository extends JpaRepository<ScheduleChangeLogEntity, Long> {

    @Query("SELECT scl FROM schedule_change_log scl " +
            "LEFT JOIN FETCH scl.affectedUsers " +
            "WHERE scl.schedule = :schedule")
    List<ScheduleChangeLogEntity> findAllBySchedule(@Param("schedule") ScheduleEntity schedule);
}
