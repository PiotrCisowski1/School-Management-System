package com.cisowski.schoolmanagement.timetable.schedule.repository;

import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

    @Query("SELECT s from ScheduleEntity s JOIN s.classroom c WHERE c.id = :classroomId")
    List<ScheduleEntity> findByClassroomId(Integer classroomId);

//    @Query(value = "SELECT * FROM schedules WHERE status IN :statuses " +
//            "AND ( " +
//            "   (:timeNow <= :threshold AND start_time >= :timeNow AND start_time <= :threshold) " +
//            "   OR " +
//            "   (:timeNow > :threshold AND (start_time >= :timeNow OR start_time <= :threshold)) " +
//            ") " +
//            "AND effective_date <= :dateNow " +
//            "AND (expiration_date IS NULL OR expiration_date > :dateNow) " +
//            "AND day_of_week = :day",
//            nativeQuery = true)
//    List<ScheduleEntity> findUninitializedSchedules(
//            @Param("statuses") List<String> statuses,
//            @Param("threshold") String threshold,
//            @Param("dateNow") LocalDate dateNow,
//            @Param("day") DayOfWeek day,
//            @Param("timeNow") String timeNow
//    );

    @Query(value = "SELECT s FROM ScheduleEntity s " +
            "WHERE s.status IN :statuses " +
            "AND s.effectiveDate <= :generationEndDate " +
            "AND (s.expirationDate IS NULL OR s.expirationDate >= :generationStartDate)")
    List<ScheduleEntity> findSchedulesToInitializeInGivenTimeGap(
            @Param("statuses") List<ScheduleStatus> acceptableStatuses,
            @Param("generationStartDate") LocalDate generationStartDate,
            @Param("generationEndDate") LocalDate generationEndDate
    );

    boolean existsByTeacher(TeacherEntity teacher);
}
