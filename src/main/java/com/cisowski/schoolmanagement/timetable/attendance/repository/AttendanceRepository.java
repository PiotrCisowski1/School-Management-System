package com.cisowski.schoolmanagement.timetable.attendance.repository;

import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findAllByStudentIn(List<StudentEntity> students);
    List<AttendanceEntity> findAllByOccurrence(ScheduleOccurrenceEntity occurrence);
    List<AttendanceEntity> findAllByStudent(StudentEntity student);
    List<AttendanceEntity> findAllByStudentAndCreatedAtBetween(StudentEntity student, LocalDateTime periodStart, LocalDateTime periodEnd);
}
