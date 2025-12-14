package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.timetable.attendance.mapper.AttendanceMapper;
import com.cisowski.schoolmanagement.timetable.attendance.model.*;
import com.cisowski.schoolmanagement.timetable.attendance.repository.AttendanceRepository;
import com.cisowski.schoolmanagement.timetable.attendance.service.AttendanceServiceImpl;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleService;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceService;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    private StudentService studentService;
    @Mock
    private AttendanceMapper attendanceMapper;
    @Mock
    private ScheduleOccurrenceService occurrenceService;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private static final int MIN_INIT_TIME_VALUE = 15;


    @Nested
    class InitializeAttendancesTests {

        private ScheduleOccurrenceEntity validScheduleOccurrence;
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

            ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                    .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                    .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                    .set(field(ScheduleEntity::getStartTime), LocalTime.now().plusMinutes(MIN_INIT_TIME_VALUE + 5))
                    .create();
            validScheduleOccurrence = Instancio.of(ScheduleOccurrenceEntity.class)
                    .set(field(ScheduleOccurrenceEntity::getSchedule), schedule)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.SCHEDULED)
                    .set(field(ScheduleOccurrenceEntity::getAttendances), null)
                    .set(field(ScheduleOccurrenceEntity::getOccurrenceDateTime), LocalDateTime.of(schedule.getEffectiveDate(), schedule.getStartTime()))
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

                assertDoesNotThrow(() -> attendanceService.initializeAttendances(validScheduleOccurrence));

                verify(attendanceRepository, times(1)).saveAll(anyList());
            }
        }

        @Test
        void initializeAttendances_ScheduleIsNull_ThrowsException() {
            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.initializeAttendances(null));

            assertTrue(exception.getMessage().contains("Cannot initialize Attendance because given ScheduleOccurrence is empty"));
            verify(attendanceRepository, never()).saveAll(any());
        }

        @Test
        void initializeAttendances_StatusNotScheduled_ThrowsException() {
            validScheduleOccurrence.setStatus(OccurrenceStatus.CANCELLED);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.initializeAttendances(validScheduleOccurrence));

            verify(attendanceRepository, never()).saveAll(any());
        }

    }

    @Nested
    @DisplayName("Tests for setAttendanceAbsenceStatusForStudents")
    class SetAttendanceStatusTests {

        private ScheduleEntity schedule;
        private List<StudentEntity> students;
        private List<AttendanceEntity> attendances;
        private MarkAttendanceRequest request;
        private ScheduleOccurrenceEntity occurrence;

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

            occurrence = Instancio.of(ScheduleOccurrenceEntity.class)
                    .set(field(ScheduleOccurrenceEntity::getSchedule), schedule)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.ONGOING)
                    .set(field(ScheduleOccurrenceEntity::getAttendances), null)
                    .set(field(ScheduleOccurrenceEntity::getOccurrenceDateTime), LocalDateTime.of(schedule.getEffectiveDate(), schedule.getStartTime()))
                    .create();
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
            when(occurrenceService.fetchScheduleOccurrence(any())).thenReturn(occurrence);

            List<AttendanceSummaryResponse> result = attendanceService.setAttendanceAbsenceStatusForStudents(occurrence.getId(), request);

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
                    () -> attendanceService.setAttendanceAbsenceStatusForStudents(occurrence.getId(), request));

            assertTrue(exception.getMessage().contains("Cannot mark attendance for:"));
        }

        @Test
        void setAttendanceAbsenceStatusForStudents_StatusIsNull_ThrowsException() {
            request.setStatus(null);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.setAttendanceAbsenceStatusForStudents(occurrence.getId(), request));

            assertTrue(exception.getMessage().contains("Cannot mark attendance for:"));
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
            when(occurrenceService.fetchScheduleOccurrence(any())).thenReturn(occurrence);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.setAttendanceAbsenceStatusForStudents(occurrence.getId(), request));

            assertTrue(exception.getMessage().contains("Not every Student is assigned to ScheduleOccurrence with ID:"));
            assertTrue(exception.getMessage().contains(String.valueOf(unauthorizedStudent.getId())));
            verify(attendanceRepository, never()).findAllByStudentIn(anyList());
        }
    }

    @Nested
    @DisplayName("Tests for getActiveAttendanceForScheduleOccurrence")
    class GetActiveAttendanceTests {
        private final Long occurrenceId = 10L;
        private ScheduleOccurrenceEntity ongoingOccurrence;
        private List<AttendanceEntity> attendances;
        private List<AttendanceSummaryResponse> expectedResponses;

        @BeforeEach
        void setup() {
            ongoingOccurrence = Instancio.of(ScheduleOccurrenceEntity.class)
                    .set(field(ScheduleOccurrenceEntity::getId), occurrenceId)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.ONGOING)
                    .create();
            attendances = Instancio.ofList(AttendanceEntity.class).size(3).create();
            expectedResponses = Instancio.ofList(AttendanceSummaryResponse.class).size(3).create();
        }

        @Test
        void getActiveAttendanceForScheduleOccurrence_Success() {
            when(occurrenceService.fetchScheduleOccurrence(occurrenceId)).thenReturn(ongoingOccurrence);
            when(attendanceRepository.findAllByOccurrence(ongoingOccurrence)).thenReturn(attendances);
            when(attendanceMapper.toSummaryResponseList(attendances)).thenReturn(expectedResponses);

            List<AttendanceSummaryResponse> result = attendanceService.getActiveAttendanceForScheduleOccurrence(occurrenceId);

            assertNotNull(result);
            assertEquals(expectedResponses.size(), result.size());
            assertEquals(expectedResponses, result);
            verify(occurrenceService, times(1)).fetchScheduleOccurrence(occurrenceId);
            verify(attendanceRepository, times(1)).findAllByOccurrence(ongoingOccurrence);
            verify(attendanceMapper, times(1)).toSummaryResponseList(attendances);
        }

        @Test
        void getActiveAttendanceForScheduleOccurrence_StatusNotOngoing_ThrowsException() {
            ongoingOccurrence.setStatus(OccurrenceStatus.COMPLETED);
            when(occurrenceService.fetchScheduleOccurrence(occurrenceId)).thenReturn(ongoingOccurrence);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.getActiveAttendanceForScheduleOccurrence(occurrenceId));

            assertTrue(exception.getMessage().contains("is not active for updates as it's status is COMPLETED"));
            verify(attendanceRepository, never()).findAllByOccurrence(any());
        }

        @Test
        void getActiveAttendanceForScheduleOccurrence_NoAttendancesFound_ReturnsEmptyList() {
            when(occurrenceService.fetchScheduleOccurrence(occurrenceId)).thenReturn(ongoingOccurrence);
            when(attendanceRepository.findAllByOccurrence(ongoingOccurrence)).thenReturn(Collections.emptyList());
            when(attendanceMapper.toSummaryResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<AttendanceSummaryResponse> result = attendanceService.getActiveAttendanceForScheduleOccurrence(occurrenceId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(attendanceRepository, times(1)).findAllByOccurrence(ongoingOccurrence);
        }
    }

    @Nested
    @DisplayName("Tests for getCompletedAttendanceForScheduleOccurrence")
    class GetCompletedAttendanceTests {
        private final Long occurrenceId = 20L;
        private ScheduleOccurrenceEntity completedOccurrence;
        private List<AttendanceEntity> attendances;
        private List<AttendanceSummaryResponse> expectedResponses;

        @BeforeEach
        void setup() {
            completedOccurrence = Instancio.of(ScheduleOccurrenceEntity.class)
                    .set(field(ScheduleOccurrenceEntity::getId), occurrenceId)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.COMPLETED)
                    .create();
            attendances = Instancio.ofList(AttendanceEntity.class).size(4).create();
            expectedResponses = Instancio.ofList(AttendanceSummaryResponse.class).size(4).create();
        }

        @Test
        void getCompletedAttendanceForScheduleOccurrence_Success() {
            when(occurrenceService.fetchScheduleOccurrence(occurrenceId)).thenReturn(completedOccurrence);
            when(attendanceRepository.findAllByOccurrence(completedOccurrence)).thenReturn(attendances);
            when(attendanceMapper.toSummaryResponseList(attendances)).thenReturn(expectedResponses);

            List<AttendanceSummaryResponse> result = attendanceService.getCompletedAttendanceForScheduleOccurrence(occurrenceId);

            assertNotNull(result);
            assertEquals(expectedResponses.size(), result.size());
            assertEquals(expectedResponses, result);
            verify(occurrenceService, times(1)).fetchScheduleOccurrence(occurrenceId);
            verify(attendanceRepository, times(1)).findAllByOccurrence(completedOccurrence);
            verify(attendanceMapper, times(1)).toSummaryResponseList(attendances);
        }

        @Test
        void getCompletedAttendanceForScheduleOccurrence_StatusNotCompleted_ThrowsException() {
            completedOccurrence.setStatus(OccurrenceStatus.ONGOING);
            when(occurrenceService.fetchScheduleOccurrence(occurrenceId)).thenReturn(completedOccurrence);

            SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                    () -> attendanceService.getCompletedAttendanceForScheduleOccurrence(occurrenceId));

            assertTrue(exception.getMessage().contains("Expected status is COMPLETED"));
            verify(attendanceRepository, never()).findAllByOccurrence(any());
        }

        @Test
        void getCompletedAttendanceForScheduleOccurrence_NoAttendancesFound_ReturnsEmptyList() {
            when(occurrenceService.fetchScheduleOccurrence(occurrenceId)).thenReturn(completedOccurrence);
            when(attendanceRepository.findAllByOccurrence(completedOccurrence)).thenReturn(Collections.emptyList());
            when(attendanceMapper.toSummaryResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<AttendanceSummaryResponse> result = attendanceService.getCompletedAttendanceForScheduleOccurrence(occurrenceId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(attendanceRepository, times(1)).findAllByOccurrence(completedOccurrence);
        }
    }

    @Nested
    @DisplayName("Tests for fetchAttendance")
    class FetchAttendanceTests {
        private final Long attendanceId = 30L;
        private AttendanceEntity attendanceEntity;

        @BeforeEach
        void setup() {
            attendanceEntity = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getId), attendanceId)
                    .create();
        }

        @Test
        void fetchAttendance_Success() {
            when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendanceEntity));

            AttendanceEntity result = attendanceService.fetchAttendance(attendanceId);

            assertNotNull(result);
            assertEquals(attendanceId, result.getId());
            verify(attendanceRepository, times(1)).findById(attendanceId);
        }

        @Test
        void fetchAttendance_NotFound_ThrowsException() {
            when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> attendanceService.fetchAttendance(attendanceId));

            verify(attendanceRepository, times(1)).findById(attendanceId);
        }
    }

    @Nested
    @DisplayName("Tests for getAttendanceById")
    class GetAttendanceByIdTests {
        private final Long attendanceId = 40L;
        private AttendanceEntity attendanceEntity;
        private AttendanceDetailedResponse expectedResponse;

        @BeforeEach
        void setup() {
            attendanceEntity = Instancio.of(AttendanceEntity.class)
                    .set(field(AttendanceEntity::getId), attendanceId)
                    .create();
            expectedResponse = Instancio.create(AttendanceDetailedResponse.class);
        }

        @Test
        void getAttendanceById_Success() {
            when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendanceEntity));
            when(attendanceMapper.toDetailedResponse(attendanceEntity)).thenReturn(expectedResponse);

            AttendanceDetailedResponse result = attendanceService.getAttendanceById(attendanceId);

            assertNotNull(result);
            assertEquals(expectedResponse, result);
            verify(attendanceRepository, times(1)).findById(attendanceId);
            verify(attendanceMapper, times(1)).toDetailedResponse(attendanceEntity);
        }

        @Test
        void getAttendanceById_NotFound_ThrowsException() {
            when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> attendanceService.getAttendanceById(attendanceId));

            verify(attendanceRepository, times(1)).findById(attendanceId);
            verify(attendanceMapper, never()).toDetailedResponse(any());
        }
    }
}
