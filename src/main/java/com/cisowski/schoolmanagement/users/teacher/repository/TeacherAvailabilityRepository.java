package com.cisowski.schoolmanagement.users.teacher.repository;

import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

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
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    List<TeacherAvailabilityEntity> findByTeacherId(Integer teacherId);

    List<TeacherAvailabilityEntity> findByDayOfWeek(DayOfWeek dayOfWeek);

    @Query("SELECT ta FROM TeacherAvailabilityEntity ta " +
            "WHERE ta.teacher IN :teachers " +
            "AND ta.dayOfWeek = :dayOfWeek " +
            "AND ta.isAvailable = true " +
            "AND (" +
            "  (CAST(:startTime AS time) >= ta.startTime AND CAST(:startTime AS time) < ta.endTime) " +
            "  OR (CAST(:endTime AS time) > ta.startTime AND CAST(:endTime AS time) <= ta.endTime) " +
            "  OR (ta.startTime >= CAST(:startTime AS time) AND ta.startTime < CAST(:endTime AS time))" +
            ")")
    List<TeacherAvailabilityEntity> findByTeachersAndTimeRange(
            @Param("teachers") List<TeacherEntity> teachers,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
}
