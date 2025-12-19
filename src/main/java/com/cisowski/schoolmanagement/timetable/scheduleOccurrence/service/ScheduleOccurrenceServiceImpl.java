package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.service;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigDetailedResponse;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigKeys;
import com.cisowski.schoolmanagement.appConfig.service.AppConfigService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleRecurrenceType;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleVersionRepository;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.mapper.ScheduleOccurrenceMapper;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.OccurrenceStatus;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.repository.ScheduleOccurrenceRepository;
import com.cisowski.schoolmanagement.timetable.shared.ScheduleHelper;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleOccurrenceServiceImpl implements ScheduleOccurrenceService {

    private final ScheduleOccurrenceRepository occurrenceRepository;
    private final AppConfigService configService;
    private final YearbookService yearbookService;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final ScheduleOccurrenceMapper occurrenceMapper;

    @Override
    public ScheduleOccurrenceEntity fetchScheduleOccurrence(Long scheduleOccurrenceId) {
        DbLogger.info("Searching for ScheduleOccurrence with ID: " + scheduleOccurrenceId);
        Optional<ScheduleOccurrenceEntity> existingOccurrence = occurrenceRepository.findById(scheduleOccurrenceId);
        if(existingOccurrence.isEmpty())
            throw new EntityNotFoundException(ScheduleOccurrenceEntity.class, "ID", scheduleOccurrenceId.toString());
        DbLogger.info(String.format("Found ScheduleOccurrence with ID: %s, entity: %s", scheduleOccurrenceId, existingOccurrence.get().toString()));
        return existingOccurrence.get();
    }

    @Override
    @Transactional
    public void initializeScheduleOccurrence(ScheduleEntity schedule, Integer minInitDays) {
        if(schedule == null)
            throw new SpecificationBrokenException("Cannot initialize Schedule occurrences for empty Schedule");
        DbLogger.info(String.format("Initializing ScheduleOccurrences for Schedule with ID: %s, for next % days", schedule.getId(), minInitDays));
        LocalDate currentDate = LocalDate.now();
        LocalDate generationEndDate = currentDate.plusDays(minInitDays);
        LocalDate effectiveStart = schedule.getEffectiveDate().isAfter(currentDate)
                ? schedule.getEffectiveDate()
                : currentDate;
        LocalDate effectiveEnd = generationEndDate;
        if (schedule.getExpirationDate() != null)
            effectiveEnd = (schedule.getExpirationDate().isBefore(generationEndDate))
                    ? schedule.getExpirationDate()
                    : generationEndDate;

        if(schedule.getRecurrenceType().equals(ScheduleRecurrenceType.NONE))
            handleRecurrenceTypeNoneInit(schedule, effectiveStart, effectiveEnd);
        else {
            handleRecurrentScheduleInit(schedule, effectiveStart, effectiveEnd);
        }
    }

    private void handleRecurrenceTypeNoneInit(ScheduleEntity schedule, LocalDate effectiveStart, LocalDate effectiveEnd) {
        LocalDate occurrenceDate = schedule.getEffectiveDate();
        if(occurrenceDate.isAfter(effectiveEnd) || occurrenceDate.isBefore(effectiveStart))
            return;
        if(!occurrenceDate.getDayOfWeek().equals(schedule.getDayOfWeek()))
            throw new SpecificationBrokenException(String.format(
                    "Cannot initialize Schedule with ID %s, because occurrence date dayOfWeek %s is not matching Schedule's dayOfWeek %s",
                    schedule.getId(),
                    occurrenceDate,
                    schedule.getDayOfWeek()));
        createSingleOccurrence(schedule, occurrenceDate);
    }

    private void handleRecurrentScheduleInit(ScheduleEntity schedule, LocalDate effectiveStart, LocalDate effectiveEnd) {
        DbLogger.info("Creating recurrent occurrences for Schedule with ID: " + schedule.getId());
        LocalDate currentOccurrenceDate = effectiveStart.with(TemporalAdjusters.nextOrSame(schedule.getDayOfWeek()));
        while(currentOccurrenceDate.isBefore(effectiveEnd) || currentOccurrenceDate.isEqual(effectiveEnd)) {
            createSingleOccurrence(schedule, currentOccurrenceDate);
            switch (schedule.getRecurrenceType()) {
                case WEEKLY:
                    currentOccurrenceDate = currentOccurrenceDate.plusWeeks(1);
                    break;
                case BIWEEKLY:
                    currentOccurrenceDate = currentOccurrenceDate.plusWeeks(2);
                    break;
                case MONTHLY:
                    currentOccurrenceDate = currentOccurrenceDate.plusMonths(1)
                            .with(TemporalAdjusters.nextOrSame(schedule.getDayOfWeek()));
                    break;
                default:
                    break;
            }
        }
    }

    private void createSingleOccurrence(ScheduleEntity schedule, LocalDate occurrenceDate) {
        DbLogger.info("Creating occurrence for Schedule with ID: " + schedule.getId());
        LocalDateTime occurrenceDateTime = LocalDateTime.of(occurrenceDate, schedule.getStartTime());
        if(!occurrenceRepository.existsByScheduleAndOccurrenceDateTime(schedule, occurrenceDateTime)) {
            ScheduleOccurrenceEntity occurrence = new ScheduleOccurrenceEntity();
            occurrence.setSchedule(schedule);
            occurrence.setOccurrenceDateTime(occurrenceDateTime);
            occurrence.setOccurrenceEndTime(schedule.getEndTime());
            occurrence.setStatus(OccurrenceStatus.SCHEDULED);
            ScheduleOccurrenceEntity saved = occurrenceRepository.save(occurrence);
            DbLogger.info("Occurrence successfully created: " + saved.toString());
        }
    }

    @Override
    public List<ScheduleOccurrenceEntity> fetchUninitializedOccurrencesForAttendance() {
        DbLogger.info("Searching for any ScheduleOccurrences which has uninitialized Attendances");
        AppConfigDetailedResponse minInitTimeConfig = configService.getConfigByKey(AppConfigKeys.ATTENDANCE_INITIALIZATION_MIN_TIME.getValue());
        Integer minInitTime = Integer.parseInt(minInitTimeConfig.getValue());
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime maxInitDateTime = dateTimeNow.plusMinutes(minInitTime);
        List<ScheduleOccurrenceEntity> occurrences = occurrenceRepository.findOccurrencesReadyForInitialization(OccurrenceStatus.SCHEDULED, dateTimeNow, maxInitDateTime);
        DbLogger.info(String.format("Found %s ScheduleOccurrences ready for initialize Attendances", occurrences.size()));
        return occurrences;
    }

    @Override
    @Transactional
    public void changeOccurrenceStatus(ScheduleOccurrenceEntity occurrence, OccurrenceStatus occurrenceStatus) {
        if(occurrence.getStatus().equals(occurrenceStatus))
            return;
        DbLogger.info(String.format("Changing ScheduleOccurrence(ID: %s) status from %s to %s", occurrence.getId(), occurrence.getStatus(), occurrenceStatus));
        occurrence.setStatus(occurrenceStatus);
        ScheduleOccurrenceEntity saved = occurrenceRepository.save(occurrence);
        DbLogger.info("Status change successfully for ScheduleOccurrence: " + saved.toString());
    }

    @Override
    public List<ScheduleOccurrenceSummaryResponse> getOccurrencesForYearbook(Integer yearbookId) {
        DbLogger.info("Searching for ScheduleOccurrences for Yearbook with ID: " + yearbookId);
        YearbookEntity yearbook = yearbookService.fetchYearbookEntity(yearbookId);
        Optional<ScheduleVersionEntity> scheduleVersion = scheduleVersionRepository.findByIsActiveTrueAndYearbookId(yearbook.getId());
        if(scheduleVersion.isEmpty())
            throw new SpecificationBrokenException("Cannot fetch Schedule occurrences because there is no active Schedule version for Yearbook with ID " + yearbookId);
        List<ScheduleEntity> activeSchedules = scheduleVersion.get().getSchedules().stream()
                .filter(Objects::nonNull)
                .filter(ScheduleHelper::isActiveSchedule)
                .toList();
        AppConfigDetailedResponse config = configService.getConfigByKey(AppConfigKeys.SCHEDULE_INIT_SEARCH_TIME.getValue());
        Integer daysThreshold = Integer.parseInt(config.getValue());
        LocalDate endThreshold = LocalDate.now().plusDays(daysThreshold);
        LocalDateTime thresholdStart = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0));
        LocalDateTime thresholdEnd = LocalDateTime.of(endThreshold, LocalTime.of(23, 59));
        Set<ScheduleOccurrenceEntity> occurrences = occurrenceRepository.findByScheduleInAndOccurrenceDateTimeBetween(activeSchedules, thresholdStart, thresholdEnd);
        return occurrenceMapper.toSummaryResponseList(occurrences);
    }


}
