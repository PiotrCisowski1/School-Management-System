package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.timetable.attendance.mapper.AttendanceMapper;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceEntity;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceStatus;
import com.cisowski.schoolmanagement.timetable.attendance.model.AttendanceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.attendance.model.MarkAttendanceRequest;
import com.cisowski.schoolmanagement.timetable.attendance.repository.AttendanceRepository;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceServiceImpl;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.timetable.shared.TimetableHelper;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private AppConfigService appConfigService;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private StudentService studentService;
    @Mock
    private AttendanceMapper attendanceMapper;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private static final int MIN_INIT_TIME_VALUE = 15;


    @Nested
    class InitializeAttendancesTests {

        private ScheduleEntity validSchedule;
        private List<StudentEntity> students;

        @BeforeEach
        void setup() {
            students = Instancio.ofList(StudentEntity.class)
                    .size(5)
                    .generate(field(UserEntity::getId), gen -> gen.ints().range( 1, 100))
                    .create();

            YearbookEntity yearbook = Instancio.of(YearbookEntity.class)
                    .set(field(YearbookEntity::getStudentsInYearbook), new HashSet<>(students))
                    .create();

            ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                    .set(field(ScheduleVersionEntity::getYearbook), yearbook)
                    .create();

            validSchedule = Instancio.of(ScheduleEntity.class)
                    .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                    .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                    .set(field(ScheduleEntity::getStartTime), LocalTime.now().plusMinutes(MIN_INIT_TIME_VALUE + 5))
                    .create();
        }

        @Test
        void initializeAttendances_Success() {
            try (MockedStatic<TimetableHelper> mockedTimetableHelper = mockStatic(TimetableHelper.class)) {
                mockedTimetableHelper.when(() -> TimetableHelper.isStartWithinTimeWindow(any(LocalTime.class), any(LocalTime.class), anyInt()))
                        .thenReturn(true);

                int expectedSize = students.size();
                List<AttendanceEntity> savedAttendances = Instancio.ofList(AttendanceEntity.class).size(expectedSize).create();

                AppConfigDetailedResponse config = Instancio.create(AppConfigDetailedResponse.class);
                config.setValue(String.valueOf(MIN_INIT_TIME_VALUE));

                when(appConfigService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue()))
                        .thenReturn(config);
                when(attendanceRepository.saveAll(anyList())).thenReturn(savedAttendances);

                assertDoesNotThrow(() -> attendanceService.initializeAttendances(validSchedule));

                verify(attendanceRepository, times(1)).saveAll(anyList());
            }
        }

        @Test
        void initializeAttendances_ScheduleIsNull_ThrowsException() {
            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.initializeAttendances(null));

            assertTrue(exception.getMessage().contains("Cannot initialize Attendance because given Schedule is empty"));
            verify(attendanceRepository, never()).saveAll(any());
        }

        @Test
        void initializeAttendances_StatusNotScheduled_ThrowsException() {
            validSchedule.setStatus(ScheduleStatus.COMPLETED);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.initializeAttendances(validSchedule));

            assertTrue(exception.getMessage().contains("Allowed status is SCHEDULED"));
            verify(attendanceRepository, never()).saveAll(any());
        }

        @Test
        void initializeAttendances_TimeWindowExceeded_ThrowsException() {
            try (MockedStatic<TimetableHelper> mockedTimetableHelper = mockStatic(TimetableHelper.class)) {
                mockedTimetableHelper.when(() -> TimetableHelper.isStartWithinTimeWindow(any(LocalTime.class), any(LocalTime.class), anyInt()))
                        .thenReturn(false);

                LocalTime startTime = LocalTime.now().minusMinutes(MIN_INIT_TIME_VALUE + 1);
                validSchedule.setStartTime(startTime);

                AppConfigDetailedResponse config = Instancio.create(AppConfigDetailedResponse.class);
                config.setValue(String.valueOf(MIN_INIT_TIME_VALUE));

                when(appConfigService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue()))
                        .thenReturn(config);

                SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                        () -> attendanceService.initializeAttendances(validSchedule));

                assertTrue(exception.getMessage().contains("Attendances cannot be initialized"));
                verify(attendanceRepository, never()).saveAll(any());
            }
        }

        @Test
        void initializeAttendances_SaveSizeMismatch_ThrowsException() {
            try (MockedStatic<TimetableHelper> mockedTimetableHelper = mockStatic(TimetableHelper.class)) {
                mockedTimetableHelper.when(() -> TimetableHelper.isStartWithinTimeWindow(any(LocalTime.class), any(LocalTime.class), anyInt()))
                        .thenReturn(true);

                int expectedSize = students.size();
                List<AttendanceEntity> savedAttendances = Instancio.ofList(AttendanceEntity.class).size(expectedSize - 1).create();

                AppConfigDetailedResponse config = Instancio.create(AppConfigDetailedResponse.class);
                config.setValue(String.valueOf(MIN_INIT_TIME_VALUE));

                when(appConfigService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue()))
                        .thenReturn(config);
                when(attendanceRepository.saveAll(anyList())).thenReturn(savedAttendances);

                SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                        () -> attendanceService.initializeAttendances(validSchedule));

                assertTrue(exception.getMessage().contains("Size of saved Attendances is not same as expected size"));
            }
        }
    }

    @Nested
    @DisplayName("Tests for setAttendanceAbsenceStatusForStudents")
    class SetAttendanceStatusTests {

        private ScheduleEntity schedule;
        private List<StudentEntity> students;
        private List<AttendanceEntity> attendances;
        private MarkAttendanceRequest request;

        @BeforeEach
        void setup() {
            students = Instancio.ofList(StudentEntity.class)
                    .size(3)
                    .generate(field(UserEntity::getId), gen -> gen.ints().range(10, 30))
                    .create();

            YearbookEntity yearbook = Instancio.of(YearbookEntity.class)
                    .set(field(YearbookEntity::getStudentsInYearbook), new HashSet<>(students))
                    .create();

            ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                    .set(field(ScheduleVersionEntity::getYearbook), yearbook)
                    .create();

            schedule = Instancio.of(ScheduleEntity.class)
                    .set(field(ScheduleEntity::getId), 1)
                    .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                    .create();

            AttendanceEntity attAbsent1 = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getStudent), students.get(0))
                    .set(field(AttendanceEntity::getAttendanceStatus), AttendanceStatus.ABSENT)
                    .create();
            AttendanceEntity attAbsent2 = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getStudent), students.get(1))
                    .set(field(AttendanceEntity::getAttendanceStatus), AttendanceStatus.ABSENT)
                    .create();
            AttendanceEntity attPresent = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getStudent), students.get(2))
                    .set(field(AttendanceEntity::getAttendanceStatus), AttendanceStatus.PRESENT)
                    .create();

            attendances = List.of(attAbsent1, attAbsent2, attPresent);

            List<Integer> studentIds = students.stream().map(StudentEntity::getId).toList();
            request = new MarkAttendanceRequest(studentIds, AttendanceStatus.PRESENT);
        }

        @Test
        void setAttendanceAbsenceStatusForStudents_Success() {
            List<AttendanceEntity> updatedAttendances = attendances.stream()
                    .peek(att -> att.setAttendanceStatus(AttendanceStatus.PRESENT))
                    .collect(Collectors.toList());

            List<AttendanceSummaryResponse> expectedResponses = Instancio.ofList(AttendanceSummaryResponse.class)
                    .size(attendances.size())
                    .create();

            when(attendanceRepository.saveAll(anyList())).thenReturn(updatedAttendances);
            when(attendanceMapper.toSummaryResponseList(updatedAttendances)).thenReturn(expectedResponses);
            when(scheduleService.fetchSchedule(any())).thenReturn(schedule);

            List<AttendanceSummaryResponse> result = attendanceService.setAttendanceAbsenceStatusForStudents(schedule.getId(), request);

            assertNotNull(result);
            assertEquals(expectedResponses.size(), result.size());
            verify(attendanceRepository, times(1)).saveAll(anyList());
            verify(attendanceMapper, times(1)).toSummaryResponseList(anyList());

            long changedCount = updatedAttendances.stream()
                    .filter(att -> att.getAttendanceStatus().equals(AttendanceStatus.PRESENT))
                    .count();
            assertEquals(3, changedCount);
        }

        @Test
        void setAttendanceAbsenceStatusForStudents_EmptyStudentIds_ThrowsException() {
            request.setStudentIds(Collections.emptyList());

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.setAttendanceAbsenceStatusForStudents(schedule.getId(), request));

            assertTrue(exception.getMessage().contains("Cannot mark attendance for:"));
            verify(scheduleService, never()).fetchSchedule(anyInt());
        }

        @Test
        void setAttendanceAbsenceStatusForStudents_StatusIsNull_ThrowsException() {
            request.setStatus(null);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.setAttendanceAbsenceStatusForStudents(schedule.getId(), request));

            assertTrue(exception.getMessage().contains("Cannot mark attendance for:"));
            verify(scheduleService, never()).fetchSchedule(anyInt());
        }

        @Test
        void setAttendanceAbsenceStatusForStudents_StudentsNotInSchedule_ThrowsException() {
            StudentEntity unauthorizedStudent = Instancio.of(StudentEntity.class)
                    .set(field(UserEntity::getId), 999)
                    .create();
            List<StudentEntity> allStudents = List.of(students.get(0), unauthorizedStudent);

            List<Integer> allStudentIds = allStudents.stream().map(StudentEntity::getId).toList();
            request.setStudentIds(allStudentIds);

            when(studentService.fetchStudents(allStudentIds)).thenReturn(allStudents);
            when(scheduleService.fetchSchedule(any())).thenReturn(schedule);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.setAttendanceAbsenceStatusForStudents(schedule.getId(), request));

            assertTrue(exception.getMessage().contains("Not every Student is assigned to Schedule with ID:"));
            assertTrue(exception.getMessage().contains(String.valueOf(999)));
            verify(attendanceRepository, never()).findAllByStudentIn(anyList());
        }
    }
}
