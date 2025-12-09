package com.cisowski.schoolmanagement.timetable.attendance.service;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.timetable.attendance.mapper.AttendanceMapper;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceStatus;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.attendance.repository.AttendanceRepository;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.timetable.shared.TimetableHelper;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AppConfigService appConfigService;
    private final ScheduleService scheduleService;
    private final StudentService studentService;
    private final AttendanceMapper attendanceMapper;

    @Value("#{'${attendance.init.acceptable.schedule.statuses}'.split(',')}")
    private List<ScheduleStatus> acceptableInitScheduleStatusList;

    @Override
    @Transactional
    public void initializeAttendances(ScheduleEntity schedule) {
        if(schedule == null)
            throw new SpecificationBrokenException("Cannot initialize Attendance because given Schedule is empty");
        DbLogger.info("Initializing Attendance for Schedule with ID: " + schedule.getId());
        checkInitializationPossible(schedule);
        List<AttendanceEntity> attendances = getAttendanceEntities(schedule);
        List<AttendanceEntity> savedAttendances = attendanceRepository.saveAll(attendances);
        if(attendances.size() != savedAttendances.size())
            throw new SpecificationBrokenException("Size of saved Attendances is not same as expected size, Schedule ID: " + schedule.getId());
    }

    private void checkInitializationPossible(ScheduleEntity schedule) {
        if(!CollectionUtils.isEmpty(acceptableInitScheduleStatusList))
            throw new SpecificationBrokenException(String.format("Cannot check possibility of initialization for Schedule with ID: %s, because acceptable status list is empty", schedule.getId()));
        if(!acceptableInitScheduleStatusList.contains(schedule.getStatus()))
            throw new SpecificationBrokenException(String.format(
                    "Cannot initialize Attendance if Schedule with ID %s is in status %s. Allowed statuses are: %s",
                    schedule.getId(),
                    schedule.getStatus().name(),
                    acceptableInitScheduleStatusList));
        AppConfigDetailedResponse minInitTimeConfig = appConfigService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue());
        Integer minInitTimeValue = Integer.valueOf(minInitTimeConfig.getValue());
        LocalTime scheduleStartTime = schedule.getStartTime();
        LocalTime timeNow = LocalTime.now();
        LocalTime latestInitTime = scheduleStartTime.minusMinutes(minInitTimeValue);
        boolean canInitialize = TimetableHelper.isStartWithinTimeWindow(timeNow, scheduleStartTime, minInitTimeValue);
        if (!canInitialize) {
            throw new SpecificationBrokenException(String.format(
                    "Attendances cannot be initialized for Schedule with ID %s. " +
                            "Current time: %s, schedule starts at: %s. " +
                            "Initialization window: from %s (15 minutes before start)",
                    schedule.getId(),
                    timeNow.toString(),
                    scheduleStartTime.toString(),
                    latestInitTime.toString()
            ));
        }
    }

    private List<AttendanceEntity> getAttendanceEntities(ScheduleEntity schedule) {
        List<StudentEntity> scheduleStudents = new ArrayList<>(schedule.getScheduleVersion().getYearbook().getStudentsInYearbook());
        List<AttendanceEntity> attendances = new ArrayList<>();
        scheduleStudents.forEach(student -> {
            DbLogger.info("Creating Attendance for Student with ID: " + student.getId());
            AttendanceEntity attendance = new AttendanceEntity();
            attendance.setSchedule(schedule);
            attendance.setStudent(student);
            attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
            attendances.add(attendance);
        });
        return attendances;
    }

    @Override
    @Transactional
    public List<AttendanceSummaryResponse> setAttendanceAbsenceStatusForStudents(Integer scheduleId, MarkAttendanceRequest request) {
        List<Integer> studentsId = request.getStudentIds();
        AttendanceStatus status = request.getStatus();
        if(CollectionUtils.isEmpty(studentsId) || status == null)
            throw new SpecificationBrokenException(String.format("Cannot mark attendance for: %s, %s", status, studentsId));

        DbLogger.info(String.format(
                "Marking Attendances status as %s for %s students",
                status.name(),
                studentsId.size()));

        ScheduleEntity schedule = scheduleService.fetchSchedule(scheduleId);
        if(!schedule.getStatus().equals(ScheduleStatus.ONGOING))
            throw new SpecificationBrokenException(String.format(
                    "Cannot mark status for Schedule with ID: %s, because it has status: %s. Proper status for marking is %s",
                    schedule.getId(),
                    schedule.getStatus(),
                    ScheduleStatus.ONGOING));
        List<StudentEntity> students = studentService.fetchStudents(studentsId);
        checkStudentsBelongToSchedule(schedule, students);

        List<AttendanceEntity> studentsAttendances = attendanceRepository.findAllByStudentIn(students);
        updateAttendancesStatus(studentsAttendances, status);
        List<AttendanceEntity> updated = attendanceRepository.saveAll(studentsAttendances);
        return attendanceMapper.toSummaryResponseList(updated);
    }

    private void checkStudentsBelongToSchedule(ScheduleEntity schedule, List<StudentEntity> students) {
        List<Integer> studentIds = students.stream().map(StudentEntity::getId).toList();
        DbLogger.info(String.format("Checking if Schedule with ID: %s, is assigned for every Student with ID: %s", schedule.getId(), studentIds));
        YearbookEntity yearbook = schedule.getScheduleVersion().getYearbook();
        List<Integer> yearbooksStudentsId = yearbook.getStudentsInYearbook().stream().map(UserEntity::getId).toList();
        List<Integer> notAssignedStudents = studentIds.stream()
                .filter(id -> !yearbooksStudentsId.contains(id))
                .toList();
        if(!CollectionUtils.isEmpty(notAssignedStudents))
            throw new SpecificationBrokenException(String.format("Not every Student is assigned to Schedule with ID: %s, unacceptable Student's ID: %s", schedule.getId(), notAssignedStudents));
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
}
