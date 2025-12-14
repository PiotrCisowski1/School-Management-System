package com.cisowski.schoolmanagement.timetable.attendance.service;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.timetable.attendance.mapper.AttendanceMapper;
import com.cisowski.schoolmanagement.timetable.attendance.model.*;
import com.cisowski.schoolmanagement.timetable.attendance.repository.AttendanceRepository;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
import com.cisowski.schoolmanagement.timetable.shared.TimetableHelper;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AppConfigService appConfigService;
    private final StudentService studentService;
    private final AttendanceMapper attendanceMapper;
    private final ScheduleOccurrenceService occurrenceService;

    @Override
    @Transactional
    public void initializeAttendances(ScheduleOccurrenceEntity scheduleOccurrence) {
        if(scheduleOccurrence == null)
            throw new SpecificationBrokenException("Cannot initialize Attendance because given ScheduleOccurrence is empty");
        DbLogger.info("Initializing Attendance for ScheduleOccurrence with ID: " + scheduleOccurrence.getId());
        checkInitializationPossible(scheduleOccurrence);
        List<AttendanceEntity> attendances = getAttendanceEntities(scheduleOccurrence);
        List<AttendanceEntity> savedAttendances = attendanceRepository.saveAll(attendances);
        if(attendances.size() != savedAttendances.size())
            throw new SpecificationBrokenException("Size of saved Attendances is not same as expected size, ScheduleOccurrence ID: " + scheduleOccurrence.getId());
    }

    private void checkInitializationPossible(ScheduleOccurrenceEntity scheduleOccurrence) {
        if(!OccurrenceStatus.SCHEDULED.equals(scheduleOccurrence.getStatus()))
            throw new SpecificationBrokenException(String.format(
                    "Cannot initialize Attendance because ScheduleOccurrence with ID %s has status %s. Expected status is %s",
                    scheduleOccurrence.getId(),
                    scheduleOccurrence.getStatus().name(),
                    OccurrenceStatus.SCHEDULED));
        AppConfigDetailedResponse minInitTimeConfig = appConfigService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue());
        Integer minInitTimeValue = Integer.valueOf(minInitTimeConfig.getValue());
        LocalTime timeNow = LocalTime.now();
        LocalTime startTime = scheduleOccurrence.getOccurrenceDateTime().toLocalTime();
        boolean canInitialize = TimetableHelper.isStartWithinTimeWindow(timeNow, startTime, minInitTimeValue);
        if (!canInitialize) {
            throw new SpecificationBrokenException(String.format(
                    "Attendances cannot be initialized for ScheduleOccurrence with ID %s. " +
                            "Current time: %s, occurrence starts at: %s. " +
                            "Initialization window: from %s",
                    scheduleOccurrence.getId(),
                    timeNow.toString(),
                    startTime.toString(),
                    timeNow.plusMinutes(minInitTimeValue).toString()
            ));
        }
    }

    private List<AttendanceEntity> getAttendanceEntities(ScheduleOccurrenceEntity scheduleOccurrence) {
        List<StudentEntity> scheduleStudents = new ArrayList<>(scheduleOccurrence.getSchedule().getScheduleVersion().getYearbook().getStudentsInYearbook());
        List<AttendanceEntity> attendances = new ArrayList<>();
        scheduleStudents.forEach(student -> {
            DbLogger.info("Creating Attendance for Student with ID: " + student.getId());
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setOccurrence(scheduleOccurrence);
            attendance.setStudent(student);
            attendance.setAttendanceStatus(AttendanceStatus.UNMARKED);
            attendances.add(attendance);
        });
        return attendances;
    }

    @Override
    @Transactional
    public List<AttendanceSummaryResponse> setAttendanceAbsenceStatusForStudents(Long scheduleOccurrenceId, MarkAttendanceRequest request) {
        List<Integer> studentsId = request.getStudentIds();
        AttendanceStatus status = request.getStatus();
        if(CollectionUtils.isEmpty(studentsId) || status == null)
            throw new SpecificationBrokenException(String.format("Cannot mark attendance for: %s, %s", status, studentsId));

        DbLogger.info(String.format(
                "Marking Attendances status as %s for %s students",
                status.name(),
                studentsId.size()));

        ScheduleOccurrenceEntity scheduleOccurrence = occurrenceService.fetchScheduleOccurrence(scheduleOccurrenceId);
        if(!scheduleOccurrence.getStatus().equals(OccurrenceStatus.ONGOING))
            throw new SpecificationBrokenException(String.format(
                    "Cannot mark status for ScheduleOccurrence with ID: %s, because it has status: %s. Proper status for marking is %s",
                    scheduleOccurrence.getId(),
                    scheduleOccurrence.getStatus(),
                    OccurrenceStatus.ONGOING));
        List<StudentEntity> students = studentService.fetchStudents(studentsId);
        checkStudentsBelongToSchedule(scheduleOccurrence, students);

        List<AttendanceEntity> studentsAttendances = attendanceRepository.findAllByStudentIn(students);
        updateAttendancesStatus(studentsAttendances, status);
        List<AttendanceEntity> updated = attendanceRepository.saveAll(studentsAttendances);
        return attendanceMapper.toSummaryResponseList(updated);
    }

    private void checkStudentsBelongToSchedule(ScheduleOccurrenceEntity occurrence, List<StudentEntity> students) {
        List<Integer> studentIds = students.stream().map(StudentEntity::getId).toList();
        DbLogger.info(String.format("Checking if ScheduleOccurrence with ID: %s, is assigned for every Student with ID: %s", occurrence.getId(), studentIds));
        YearbookEntity yearbook = occurrence.getSchedule().getScheduleVersion().getYearbook();
        List<Integer> yearbooksStudentsId = yearbook.getStudentsInYearbook().stream().map(UserEntity::getId).toList();
        List<Integer> notAssignedStudents = studentIds.stream()
                .filter(id -> !yearbooksStudentsId.contains(id))
                .toList();
        if(!CollectionUtils.isEmpty(notAssignedStudents))
            throw new SpecificationBrokenException(String.format("Not every Student is assigned to ScheduleOccurrence with ID: %s, unacceptable Student's ID: %s", occurrence.getId(), notAssignedStudents));
    }

    private void updateAttendancesStatus(List<AttendanceEntity> attendances, AttendanceStatus status) {
        DbLogger.info(String.format("Updating %s Attendances status to %s", attendances.size(), status));
        AttendanceStatus oppositeStatus = status == AttendanceStatus.ABSENT ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT;
        List<AttendanceEntity> invalidStatusAtts = attendances.stream()
                .filter(att -> att.getAttendanceStatus().equals(status))
                .toList();
        DbLogger.info(String.format(
                "Some of Attendances already has invalid status: %s and are ignored as it is expected new status. These Attendances ID: %s",
                status,
                invalidStatusAtts.stream().map(AttendanceEntity::getId).toList()));
        attendances.stream()
                .filter(att -> att.getAttendanceStatus().equals(oppositeStatus))
                .forEach(att -> att.setAttendanceStatus(status));
    }

    @Override
    public List<AttendanceSummaryResponse> getActiveAttendanceForScheduleOccurrence(Long scheduleOccurrenceId) {
        DbLogger.info("Searching for Attendances for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        ScheduleOccurrenceEntity scheduleOccurrence = occurrenceService.fetchScheduleOccurrence(scheduleOccurrenceId);
        if(!scheduleOccurrence.getStatus().equals(OccurrenceStatus.ONGOING))
            throw new SpecificationBrokenException(String.format("Schedule occurrence with ID: %s is not active for updates as it's status is %s", scheduleOccurrenceId, scheduleOccurrence.getStatus().name()));
        List<AttendanceEntity> attendances = attendanceRepository.findAllByOccurrence(scheduleOccurrence);
        DbLogger.info(String.format("Found %s Attendances for ScheduleOccurrence with ID %s", attendances.size(), scheduleOccurrenceId));
        return attendanceMapper.toSummaryResponseList(attendances);
    }

    @Override
    public AttendanceDetailedResponse getAttendanceById(Long attendanceId) {
        AttendanceEntity attendance = fetchAttendance(attendanceId);
        return attendanceMapper.toDetailedResponse(attendance);
    }

    @Override
    public List<AttendanceSummaryResponse> getCompletedAttendanceForScheduleOccurrence(Long scheduleOccurrenceId) {
        DbLogger.info("Searching for completed Attendances for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        ScheduleOccurrenceEntity scheduleOccurrence = occurrenceService.fetchScheduleOccurrence(scheduleOccurrenceId);
        if(!scheduleOccurrence.getStatus().equals(OccurrenceStatus.COMPLETED))
            throw new SpecificationBrokenException(String.format(
                    "Cannot search for Attendances for Schedule occurrence with ID %s because it has status %s. Expected status is %s",
                    scheduleOccurrenceId,
                    scheduleOccurrence.getStatus(),
                    OccurrenceStatus.COMPLETED));
        List<AttendanceEntity> attendances = attendanceRepository.findAllByOccurrence(scheduleOccurrence);
        return attendanceMapper.toSummaryResponseList(attendances);
    }

    @Override
    public AttendanceEntity fetchAttendance(Long attendanceId) {
        DbLogger.info("Searching for Attendance with ID: " + attendanceId);
        Optional<AttendanceEntity> attendance = attendanceRepository.findById(attendanceId);
        if(attendance.isEmpty())
            throw new EntityNotFoundException(AttendanceEntity.class, "ID", attendanceId.toString());
        DbLogger.info(String.format("Found Attendance with ID %s: %s", attendanceId, attendance.get().toString()));
        return attendance.get();
    }
}
