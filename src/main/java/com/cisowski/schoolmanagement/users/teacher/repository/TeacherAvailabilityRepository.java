package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.DayOfWeek;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailabilityEntity, Integer> {

    @Query("SELECT ta FROM TeacherAvailabilityEntity ta " +
            "WHERE ta.teacher.id = :teacherId " +
            "AND ta.dayOfWeek = :dayOfWeek " +
            "AND ((CAST(:startTime AS time) >= ta.startTime AND CAST(:startTime AS time) < ta.endTime) " +
            "OR (CAST(:endTime AS time) > ta.startTime AND CAST(:endTime AS time) <= ta.endTime) " +
            "OR (ta.startTime >= CAST(:startTime AS time) AND ta.startTime < CAST(:endTime AS time)))")
    TeacherAvailabilityEntity findOverlappingAvailability(
            @Param("teacherId") Integer teacherId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") Time startTime,
            @Param("endTime") Time endTime
    );
}
