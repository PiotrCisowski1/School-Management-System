package com.cisowski.schoolmanagement.schedule.repository;

import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleVersionRepository extends JpaRepository<ScheduleVersionEntity, Integer> {
}
