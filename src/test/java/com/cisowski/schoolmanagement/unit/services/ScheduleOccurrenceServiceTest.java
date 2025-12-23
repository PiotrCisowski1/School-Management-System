package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleRecurrenceType;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleVersionRepository;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleStatusService;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.mapper.ScheduleOccurrenceMapper;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.repository.ScheduleOccurrenceRepository;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service.ScheduleOccurrenceServiceImpl;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleOccurrenceServiceTest {

    private static final Integer MIN_INIT_DAYS = 7;
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(12, 0);

    @Mock
    private ScheduleOccurrenceRepository occurrenceRepository;

    @Mock
    private AppConfigService configService;

    @Mock
    private YearbookService yearbookService;

    @Mock
    private ScheduleVersionRepository scheduleVersionRepository;

    @Mock
    private ScheduleOccurrenceMapper occurrenceMapper;

    @InjectMocks
    private ScheduleOccurrenceServiceImpl scheduleOccurrenceService;

    private ScheduleOccurrenceEntity mockOccurrence;
    private Long mockOccurrenceId;

    @BeforeEach
    void setUp() {
        mockOccurrenceId = Instancio.create(Long.class);
        mockOccurrence = Instancio.of(ScheduleOccurrenceEntity.class)
                .set(field(ScheduleOccurrenceEntity::getId), mockOccurrenceId)
                .create();
    }

    @Test
    void fetchScheduleOccurrence_shouldReturnOccurrenceWhenFound() {
        when(occurrenceRepository.findById(mockOccurrenceId)).thenReturn(Optional.of(mockOccurrence));

        ScheduleOccurrenceEntity result = scheduleOccurrenceService.fetchScheduleOccurrence(mockOccurrenceId);

        assertNotNull(result);
        assertEquals(mockOccurrence, result);
    }

    @Test
    void fetchScheduleOccurrence_shouldThrowExceptionWhenNotFound() {
        when(occurrenceRepository.findById(mockOccurrenceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> scheduleOccurrenceService.fetchScheduleOccurrence(mockOccurrenceId));
    }

    @Test
    void changeOccurrenceStatus_shouldChangeStatusAndSaveWhenDifferent() {
        OccurrenceStatus newStatus = OccurrenceStatus.ONGOING;
        mockOccurrence.setStatus(OccurrenceStatus.SCHEDULED);
        when(occurrenceRepository.save(any(ScheduleOccurrenceEntity.class))).thenReturn(mockOccurrence);

        scheduleOccurrenceService.changeOccurrenceStatus(mockOccurrence, newStatus);

        assertEquals(newStatus, mockOccurrence.getStatus());
        verify(occurrenceRepository).save(mockOccurrence);
    }

    @Test
    void changeOccurrenceStatus_shouldNotSaveWhenStatusIsSame() {
        OccurrenceStatus sameStatus = OccurrenceStatus.SCHEDULED;
        mockOccurrence.setStatus(sameStatus);

        scheduleOccurrenceService.changeOccurrenceStatus(mockOccurrence, sameStatus);

        verify(occurrenceRepository, never()).save(any(ScheduleOccurrenceEntity.class));
    }

    @Test
    void fetchUninitializedOccurrencesForAttendance_shouldReturnOccurrencesWhenFound() {
        final Integer MIN_INIT_TIME = 60;
        AppConfigDetailedResponse mockConfig = Instancio.of(AppConfigDetailedResponse.class)
                .set(field(AppConfigDetailedResponse::getValue), MIN_INIT_TIME.toString())
                .create();
        List<ScheduleOccurrenceEntity> expectedList = Instancio.ofList(ScheduleOccurrenceEntity.class).size(3).create();

        when(configService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue())).thenReturn(mockConfig);
        when(occurrenceRepository.findOccurrencesReadyForInitialization(any(OccurrenceStatus.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedList);

        List<ScheduleOccurrenceEntity> result = scheduleOccurrenceService.fetchUninitializedOccurrencesForAttendance();

        assertEquals(expectedList.size(), result.size());
        assertEquals(expectedList, result);
        verify(configService).getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue());
        verify(occurrenceRepository).findOccurrencesReadyForInitialization(eq(OccurrenceStatus.SCHEDULED), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void fetchUninitializedOccurrencesForAttendance_shouldReturnEmptyListWhenNoneFound() {
        final Integer MIN_INIT_TIME = 60;
        AppConfigDetailedResponse mockConfig = Instancio.of(AppConfigDetailedResponse.class)
                .set(field(AppConfigDetailedResponse::getValue), MIN_INIT_TIME.toString())
                .create();

        when(configService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue())).thenReturn(mockConfig);
        when(occurrenceRepository.findOccurrencesReadyForInitialization(any(OccurrenceStatus.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<ScheduleOccurrenceEntity> result = scheduleOccurrenceService.fetchUninitializedOccurrencesForAttendance();

        assertTrue(result.isEmpty());
    }

    @Test
    void initializeScheduleOccurrence_shouldThrowExceptionWhenScheduleIsNull() {
        assertThrows(SpecificationBrokenException.class, () -> scheduleOccurrenceService.initializeScheduleOccurrence(null, MIN_INIT_DAYS));
        verify(occurrenceRepository, never()).save(any());
    }

    @Test
    void initializeScheduleOccurrence_none_shouldCreateOccurrenceWhenValid() {
        ArgumentCaptor<ScheduleOccurrenceEntity> occurrenceCaptor = ArgumentCaptor.forClass(ScheduleOccurrenceEntity.class);
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getStartTime), START_TIME)
                .set(field(ScheduleEntity::getEndTime), END_TIME)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.plusDays(1))
                .set(field(ScheduleEntity::getDayOfWeek), CURRENT_DATE.plusDays(1).getDayOfWeek())
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        when(occurrenceRepository.existsByScheduleAndOccurrenceDateTime(any(), any())).thenReturn(false);
        when(occurrenceRepository.save(occurrenceCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS);

        verify(occurrenceRepository, times(1)).save(any(ScheduleOccurrenceEntity.class));
        assertEquals(schedule, occurrenceCaptor.getValue().getSchedule());
        assertEquals(LocalDateTime.of(schedule.getEffectiveDate(), START_TIME), occurrenceCaptor.getValue().getOccurrenceDateTime());
    }

    @Test
    void initializeScheduleOccurrence_none_shouldNotCreateOccurrenceWhenDateIsOutsideGenerationRange() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.plusDays(MIN_INIT_DAYS + 1))
                .set(field(ScheduleEntity::getDayOfWeek), CURRENT_DATE.plusDays(MIN_INIT_DAYS + 1).getDayOfWeek())
                .create();

        scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS);

        verify(occurrenceRepository, never()).save(any(ScheduleOccurrenceEntity.class));
    }

    @Test
    void initializeScheduleOccurrence_none_shouldThrowExceptionWhenDayOfWeekDoesNotMatch() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.plusDays(1))
                .set(field(ScheduleEntity::getExpirationDate), CURRENT_DATE.plusDays(5))
                .create();
        if (schedule.getEffectiveDate().getDayOfWeek() == DayOfWeek.MONDAY) {
            schedule.setDayOfWeek(DayOfWeek.TUESDAY);
        }
        schedule.setDayOfWeek(schedule.getEffectiveDate().getDayOfWeek().plus(1));

        assertThrows(SpecificationBrokenException.class, () -> scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS));
        verify(occurrenceRepository, never()).save(any(ScheduleOccurrenceEntity.class));
    }

    @Test
    void initializeScheduleOccurrence_none_shouldNotCreateWhenAlreadyExists() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.plusDays(1))
                .set(field(ScheduleEntity::getExpirationDate), CURRENT_DATE.plusDays(10))
                .set(field(ScheduleEntity::getDayOfWeek), CURRENT_DATE.plusDays(1).getDayOfWeek())
                .create();

        when(occurrenceRepository.existsByScheduleAndOccurrenceDateTime(any(), any())).thenReturn(true);

        scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS);

        verify(occurrenceRepository, never()).save(any(ScheduleOccurrenceEntity.class));
    }

    @Test
    void initializeScheduleOccurrence_weekly_shouldCreateCorrectNumberOfOccurrences() {
        ArgumentCaptor<ScheduleOccurrenceEntity> occurrenceCaptor = ArgumentCaptor.forClass(ScheduleOccurrenceEntity.class);
        DayOfWeek dayOfWeek = CURRENT_DATE.getDayOfWeek();
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.minusDays(7))
                .set(field(ScheduleEntity::getDayOfWeek), dayOfWeek)
                .set(field(ScheduleEntity::getExpirationDate), null)
                .create();

        when(occurrenceRepository.existsByScheduleAndOccurrenceDateTime(any(), any())).thenReturn(false);
        when(occurrenceRepository.save(occurrenceCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS);

        verify(occurrenceRepository, times(2)).save(any(ScheduleOccurrenceEntity.class));
        List<ScheduleOccurrenceEntity> savedOccurrences = occurrenceCaptor.getAllValues();
        assertTrue(savedOccurrences.stream().anyMatch(o -> o.getOccurrenceDateTime().toLocalDate().isEqual(CURRENT_DATE)));
        assertTrue(savedOccurrences.stream().anyMatch(o -> o.getOccurrenceDateTime().toLocalDate().isEqual(CURRENT_DATE.plusWeeks(1))));
    }

    @Test
    void initializeScheduleOccurrence_biweekly_shouldCreateCorrectNumberOfOccurrences() {
        DayOfWeek dayOfWeek = CURRENT_DATE.getDayOfWeek();
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.BIWEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.minusDays(14))
                .set(field(ScheduleEntity::getDayOfWeek), dayOfWeek)
                .set(field(ScheduleEntity::getExpirationDate), null)
                .create();

        when(occurrenceRepository.existsByScheduleAndOccurrenceDateTime(any(), any())).thenReturn(false);
        when(occurrenceRepository.save(any(ScheduleOccurrenceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS);

        verify(occurrenceRepository, times(1)).save(any(ScheduleOccurrenceEntity.class));
    }

    @Test
    void initializeScheduleOccurrence_recurrent_shouldRespectExpirationDate() {
        DayOfWeek dayOfWeek = CURRENT_DATE.getDayOfWeek();
        LocalDate expirationDate = CURRENT_DATE.plusDays(1);
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), CURRENT_DATE.minusDays(7))
                .set(field(ScheduleEntity::getDayOfWeek), dayOfWeek)
                .set(field(ScheduleEntity::getExpirationDate), expirationDate)
                .create();

        when(occurrenceRepository.existsByScheduleAndOccurrenceDateTime(any(), any())).thenReturn(false);
        when(occurrenceRepository.save(any(ScheduleOccurrenceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        scheduleOccurrenceService.initializeScheduleOccurrence(schedule, MIN_INIT_DAYS);

        verify(occurrenceRepository, times(1)).save(any(ScheduleOccurrenceEntity.class));
    }

    @Test
    void shouldReturnOccurrencesForYearbook() {
        Integer yearbookId = 1;
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setId(yearbookId);

        ScheduleEntity activeSchedule = Instancio.create(ScheduleEntity.class);
        activeSchedule.setStatus(ScheduleStatus.SCHEDULED);

        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        scheduleVersion.setSchedules(List.of(activeSchedule));

        AppConfigDetailedResponse config = Instancio.create(AppConfigDetailedResponse.class);
        config.setValue("7");

        Set<ScheduleOccurrenceEntity> occurrences = Instancio.ofSet(ScheduleOccurrenceEntity.class).size(3).create();
        List<ScheduleOccurrenceSummaryResponse> expectedResponse = Instancio.ofList(ScheduleOccurrenceSummaryResponse.class).size(3).create();

        when(yearbookService.fetchYearbookEntity(yearbookId)).thenReturn(yearbook);
        when(scheduleVersionRepository.findByIsActiveTrueAndYearbookId(yearbookId)).thenReturn(Optional.of(scheduleVersion));
        when(configService.getConfigByKey(AppConfigKeys.SCHEDULE_INIT_SEARCH_TIME.getValue())).thenReturn(config);

        LocalDate endThresholdDate = LocalDate.now().plusDays(7);
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
        LocalDateTime end = LocalDateTime.of(endThresholdDate, LocalTime.of(23, 59));

        when(occurrenceRepository.findByScheduleInAndOccurrenceDateTimeBetween(any(), eq(start), eq(end))).thenReturn(occurrences);
        when(occurrenceMapper.toSummaryResponseList(occurrences)).thenReturn(expectedResponse);

        List<ScheduleOccurrenceSummaryResponse> result = scheduleOccurrenceService.getOccurrencesForYearbook(yearbookId);

        assertEquals(expectedResponse, result);
        assertEquals(3, result.size());
    }

    @Test
    void shouldThrowExceptionWhenScheduleVersionNotFound() {
        Integer yearbookId = 1;
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setId(yearbookId);

        when(yearbookService.fetchYearbookEntity(yearbookId)).thenReturn(yearbook);
        when(scheduleVersionRepository.findByIsActiveTrueAndYearbookId(yearbookId)).thenReturn(Optional.empty());

        assertThrows(SpecificationBrokenException.class, () -> scheduleOccurrenceService.getOccurrencesForYearbook(yearbookId));
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveSchedulesFound() {
        Integer yearbookId = 1;
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setId(yearbookId);

        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);
        scheduleVersion.setSchedules(Collections.emptyList());

        AppConfigDetailedResponse config = Instancio.create(AppConfigDetailedResponse.class);
        config.setValue("5");

        when(yearbookService.fetchYearbookEntity(yearbookId)).thenReturn(yearbook);
        when(scheduleVersionRepository.findByIsActiveTrueAndYearbookId(yearbookId)).thenReturn(Optional.of(scheduleVersion));
        when(configService.getConfigByKey(AppConfigKeys.SCHEDULE_INIT_SEARCH_TIME.getValue())).thenReturn(config);
        when(occurrenceRepository.findByScheduleInAndOccurrenceDateTimeBetween(eq(Collections.emptyList()), any(), any())).thenReturn(Collections.emptySet());
        when(occurrenceMapper.toSummaryResponseList(Collections.emptySet())).thenReturn(Collections.emptyList());

        List<ScheduleOccurrenceSummaryResponse> result = scheduleOccurrenceService.getOccurrencesForYearbook(yearbookId);

        assertEquals(0, result.size());
    }

    @Nested
    class FindOccurrencesTests {

        @Test
        void shouldReturnOccurrencesReadyToComplete() {
            int daysGap = 5;
            List<OccurrenceStatus> statuses = List.of(OccurrenceStatus.SCHEDULED, OccurrenceStatus.ONGOING);
            List<ScheduleOccurrenceEntity> expectedOccurrences = Instancio.createList(ScheduleOccurrenceEntity.class);

            when(occurrenceRepository.findByStatusInAndOccurrenceDateTimeBefore(eq(statuses), any(LocalDateTime.class)))
                    .thenReturn(expectedOccurrences);

            List<ScheduleOccurrenceEntity> result = scheduleOccurrenceService.findOccurrencesReadyToComplete(statuses, daysGap);

            assertThat(result).hasSameSizeAs(expectedOccurrences);

            ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(occurrenceRepository).findByStatusInAndOccurrenceDateTimeBefore(eq(statuses), dateCaptor.capture());
            assertThat(dateCaptor.getValue()).isBeforeOrEqualTo(LocalDateTime.now().minusDays(daysGap));
        }
    }

    @Nested
    class ChangeStatusTests {

        @Test
        void shouldDoNothingWhenInputIsInvalid() {
            scheduleOccurrenceService.changeOccurrencesStatus(Collections.emptyList(), OccurrenceStatus.COMPLETED);
            scheduleOccurrenceService.changeOccurrencesStatus(List.of(new ScheduleOccurrenceEntity()), null);

            verifyNoInteractions(occurrenceRepository);
        }

        @Test
        void shouldChangeStatusFromScheduledToCompleted() {
            OccurrenceStatus targetStatus = OccurrenceStatus.COMPLETED;
            List<ScheduleOccurrenceEntity> occurrences = Instancio.ofList(ScheduleOccurrenceEntity.class)
                    .size(3)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.ONGOING)
                    .create();

            when(occurrenceRepository.save(any())).thenReturn(occurrences.get(0));

            scheduleOccurrenceService.changeOccurrencesStatus(occurrences, targetStatus);

            occurrences.forEach(occ -> assertThat(occ.getStatus()).isEqualTo(targetStatus));
        }

        @Test
        void shouldThrowExceptionForIllegalTransition() {
            OccurrenceStatus targetStatus = OccurrenceStatus.SCHEDULED;
            ScheduleOccurrenceEntity entity = Instancio.of(ScheduleOccurrenceEntity.class)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.COMPLETED)
                    .create();

            List<ScheduleOccurrenceEntity> occurrences = List.of(entity);

            assertThatThrownBy(() -> scheduleOccurrenceService.changeOccurrencesStatus(occurrences, targetStatus))
                    .isInstanceOf(SpecificationBrokenException.class);
        }

        @Test
        void shouldAllowOngoingToCompleted() {
            OccurrenceStatus targetStatus = OccurrenceStatus.COMPLETED;
            ScheduleOccurrenceEntity entity = Instancio.of(ScheduleOccurrenceEntity.class)
                    .set(field(ScheduleOccurrenceEntity::getStatus), OccurrenceStatus.ONGOING)
                    .create();

            when(occurrenceRepository.save(any())).thenReturn(entity);

            scheduleOccurrenceService.changeOccurrencesStatus(List.of(entity), targetStatus);

            assertThat(entity.getStatus()).isEqualTo(targetStatus);
        }
    }
}
