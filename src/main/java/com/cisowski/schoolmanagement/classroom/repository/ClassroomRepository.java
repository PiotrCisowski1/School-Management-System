package com.cisowski.schoolmanagement.classroom.repository;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<ClassroomEntity, Integer> {

    boolean existsByClassroomEquipmentsIdEquipmentId(Integer equipmentId);

    @Query("SELECT c FROM ClassroomEntity c " +
            "WHERE c.id NOT IN (" +
            "    SELECT s.classroom.id FROM ScheduleEntity s " +
            "    WHERE s.dayOfWeek = :day " +
            "    AND s.startTime < :endTime " +
            "    AND s.endTime > :startTime " +
            "    AND s.effectiveDate <= :dateEnd " +
            "    AND (s.expirationDate IS NULL OR s.expirationDate >= :dateStart) " +
            "    AND s.status IN ('SCHEDULED', 'RESCHEDULED', 'UPDATED') " +
            ")")
    List<ClassroomEntity> findAllClassroomsWithinTimePeriod(@Param("day") DayOfWeek day,
                                                            @Param("startTime") LocalTime startTime,
                                                            @Param("endTime") LocalTime endTime,
                                                            @Param("dateStart") LocalDate dateStart,
                                                            @Param("dateEnd") LocalDate dateEnd);
}
