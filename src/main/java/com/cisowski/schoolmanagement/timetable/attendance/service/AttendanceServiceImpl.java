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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public List<AttendanceAbsenceByScheduleResponse> getAbsenceStatsByScheduleForStudent(Integer studentId) {
        DbLogger.info("Searching for Attendance absence stats for Student with ID: " + studentId);
        StudentEntity student = studentService.fetchStudent(studentId);
        List<AttendanceEntity> attendances = attendanceRepository.findAllByStudent(student);
        return calculateStats(attendances);
    }

    private List<AttendanceAbsenceByScheduleResponse> calculateStats(List<AttendanceEntity> attendances) {
        Map<ScheduleEntity, List<AttendanceEntity>> occurrencesBySchedule = mapOccurrencesToSchedule(attendances);
        return calculateStatsPerSchedule(occurrencesBySchedule);
    }

    private Map<ScheduleEntity, List<AttendanceEntity>> mapOccurrencesToSchedule(List<AttendanceEntity> attendances) {
        if(CollectionUtils.isEmpty(attendances))
            return new HashMap<>();
        return attendances.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        attendance -> attendance.getOccurrence().getSchedule(),
                        Collectors.toList()
                ));
    }

    private List<AttendanceAbsenceByScheduleResponse> calculateStatsPerSchedule(Map<ScheduleEntity, List<AttendanceEntity>> occurrencesPerSchedule) {
        List<AttendanceAbsenceByScheduleResponse> responseList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(occurrencesPerSchedule)) {
            for(ScheduleEntity schedule : occurrencesPerSchedule.keySet()) {
                List<AttendanceEntity> attendances = occurrencesPerSchedule.get(schedule);
                AttendanceAbsenceByScheduleResponse response = new AttendanceAbsenceByScheduleResponse();
                response.setScheduleId(schedule.getId());
                response.setScheduleName(schedule.getSubject().getName());

                int unmarkedAbsences = 0;
                int totalAbsence = 0;

                for(AttendanceEntity attendance : attendances) {
                        if(attendance.getAttendanceStatus().equals(AttendanceStatus.ABSENT))
                            totalAbsence += 1;
                        else if(attendance.getAttendanceStatus().equals(AttendanceStatus.UNMARKED))
                            unmarkedAbsences += 1;
                }
                response.setTotalAbsenceCount(totalAbsence);
                response.setUnmarkedAbsenceCount(unmarkedAbsences);
                responseList.add(response);
            }
        }
        return responseList;
    }

    @Override
    public AttendanceOverallSummaryResponse getSummaryAttendanceForStudent(Integer studentId, LocalDate periodStart, LocalDate periodEnd) {
        DbLogger.info(String.format(
                "Fetching summarized attendance stats for Student with ID: %s, from %s to %s",
                studentId,
                periodStart,
                periodEnd));

        StudentEntity student = studentService.fetchStudent(studentId);
        AttendanceOverallSummaryResponse response = new AttendanceOverallSummaryResponse();
        response.setPeriodStart(periodStart);
        response.setPeriodEnd(periodEnd);
        checkSearchingPeriod(response, student.getYearbook().getStartingYear());
        LocalDateTime start = LocalDateTime.of(response.getPeriodStart(), LocalTime.of(0,0));
        LocalDateTime end = LocalDateTime.of(response.getPeriodEnd(), LocalTime.of(23, 59));

        List<AttendanceEntity> attendanceEntities = attendanceRepository.findAllByStudent(student);
        List<AttendanceEntity> attendances = attendanceRepository.findAllByStudentAndCreatedAtBetween(student, start, end);
        DbLogger.info(String.format(
                "Found %s Attendances for Student with ID %s between %s and %s",
                attendances.size(),
                student.getId(),
                start,
                end));

        calcSummaryStats(attendances, response);
        DbLogger.info(String.format("Attendance summary stats for Student with ID %s: %s", student.getId(), response));
        return response;
    }

    private void checkSearchingPeriod(AttendanceOverallSummaryResponse response, ZonedDateTime yearbookStartingYear) {
        LocalDate periodEnd = response.getPeriodEnd();
        LocalDate periodStart = response.getPeriodStart();

        if(periodEnd == null || periodEnd.isAfter(LocalDate.now())) {
            periodEnd = LocalDate.now();
            DbLogger.info("Summarized attendance - replacing wrong periodEnd to " + periodEnd);
        }
        if(periodStart == null || periodStart.isAfter(periodEnd)) {
            if(yearbookStartingYear == null || yearbookStartingYear.isBefore(ZonedDateTime.now().minusMonths(6)))
                periodStart = LocalDate.now().minusMonths(6);
            else if(!yearbookStartingYear.isAfter(ZonedDateTime.now()))
                periodStart = yearbookStartingYear.toLocalDate();
            else
                periodStart = LocalDate.now();
            DbLogger.info(String.format("Summarized attendance - replacing periodStart to %s", periodStart));
        }
        response.setPeriodEnd(periodEnd);
        response.setPeriodStart(periodStart);
    }

    private void calcSummaryStats(List<AttendanceEntity> attendances, AttendanceOverallSummaryResponse response) {
        if(CollectionUtils.isEmpty(attendances))
            return;
        int totalAttendance = attendances.size();
        int totalAbsence = 0;
        int totalUnmarked = 0;

        for(AttendanceEntity attendance : attendances) {
            if(attendance == null)
                return;

            if(attendance.getAttendanceStatus().equals(AttendanceStatus.ABSENT))
                totalAbsence += 1;
            else if(attendance.getAttendanceStatus().equals(AttendanceStatus.UNMARKED))
                totalUnmarked += 1;
        }
        response.setTotalAttendanceCount(totalAttendance);
        response.setTotalAbsence(totalAbsence);
        response.setTotalUnmarked(totalUnmarked);

        Double presentPercent = ((double) (totalAttendance - totalAbsence) / totalAttendance * 100);

        response.setPresentPercentage(Math.round(presentPercent * 10.0) / 10.0);
    }
}
