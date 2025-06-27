package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.schedule.mapper.ScheduleVersionMapper;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleVersionRepository;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleVersionServiceImpl implements ScheduleVersionService {

    private final ScheduleVersionRepository repository;
    private final YearbookService yearbookService;
    private final ScheduleVersionMapper scheduleVersionMapper;

    @Override
    public Collection<ScheduleVersionSummaryResponse> getScheduleVersionsForYearbook(Integer yearbookId) {
        DbLogger.info(String.format("Searching for all ScheduleVersions for Yearbook with ID: %s", yearbookId));
        List<ScheduleVersionEntity> scheduleVersions = repository.findByYearbookId(yearbookId);
        DbLogger.info(String.format("Found %s ScheduleVersions for Yearbook with ID: %s", scheduleVersions.size(), yearbookId));
        return scheduleVersionMapper.toSummaryResponseList(scheduleVersions);
    }

    @Override
    @Transactional
    public ScheduleVersionDetailedResponse createScheduleVersion(Integer yearbookId, String scheduleName, boolean isActive) {
        DbLogger.info(String.format("Creating new ScheduleVersion for Yearbook with ID: %s", yearbookId));

        ScheduleVersionEntity scheduleVersion = new ScheduleVersionEntity();
        scheduleVersion.setYearbook(yearbookService.fetchYearbookEntity(yearbookId));
        scheduleVersion.setName(scheduleName);
        scheduleVersion.setActive(isActive);

        Optional<ScheduleVersionEntity> activeSchedule = repository.findByIsActiveTrueAndYearbookId(yearbookId);
        if(activeSchedule.isPresent()){
            DbLogger.info(String.format("Found active ScheduleVersion for yearbookID %s, %s", yearbookId, activeSchedule.get().toString()));
            setScheduleVersionActivity(activeSchedule.get(), !isActive);
        }

        ScheduleVersionEntity saved = repository.save(scheduleVersion);
        DbLogger.info(String.format("ScheduleVersion was saved successfully in Yearbook with ID %s: %s", yearbookId, saved.toString()));

        return scheduleVersionMapper.toDetailedResponse(saved);
    }

    @Override
    @Transactional
    public ScheduleVersionDetailedResponse cloneScheduleVersion(Integer scheduleVersionId) {
        DbLogger.info(String.format("Cloning ScheduleVersion with ID %s", scheduleVersionId));

        Optional<ScheduleVersionEntity> foundScheduleVersion = repository.findById(scheduleVersionId);
        if(foundScheduleVersion.isEmpty())
            throw new EntityNotFoundException(ScheduleVersionEntity.class, "ID", scheduleVersionId.toString());

        ScheduleVersionEntity cloned = new ScheduleVersionEntity(foundScheduleVersion.get());
        ScheduleVersionEntity saved = repository.save(cloned);

        DbLogger.info(String.format("ScheduleVersion with ID %s was cloned successfully in Yearbook with ID %s: %s",
                scheduleVersionId,
                saved.getYearbook().getId(),
                saved.toString()));

        return scheduleVersionMapper.toDetailedResponse(saved);
    }

    @Override
    public ScheduleVersionEntity fetchScheduleVersion(Integer scheduleVersionId) {
        if(scheduleVersionId == null || scheduleVersionId <= 0)
            throw new SpecificationBrokenException("Given ScheduleVersion ID is not a valid integer value");
        Optional<ScheduleVersionEntity> scheduleVersion = repository.findById(scheduleVersionId);
        if(scheduleVersion.isEmpty())
            throw new EntityNotFoundException(ScheduleVersionEntity.class, "ID", scheduleVersionId.toString());
        return scheduleVersion.get();
    }

    private void setScheduleVersionActivity(ScheduleVersionEntity scheduleVersion, boolean isActive){
        scheduleVersion.setActive(isActive);
        repository.save(scheduleVersion);
        DbLogger.info(String.format("ScheduleVersion with ID %s was set to %s", scheduleVersion.getId(), isActive ? "active" : "inactive"));
    }

    @Override
    public ScheduleVersionDetailedResponse getScheduleVersion(Integer scheduleVersionId) {
        DbLogger.info(String.format("Searching for ScheduleVersion with ID: %s", scheduleVersionId));
        Optional<ScheduleVersionEntity> scheduleVersion = repository.findById(scheduleVersionId);
        if (scheduleVersion.isEmpty())
            throw new EntityNotFoundException(ScheduleVersionEntity.class, "ID", scheduleVersionId.toString());
        DbLogger.info(String.format("Found ScheduleVersion with ID %s: %s", scheduleVersionId, scheduleVersion.get().toString()));
        return scheduleVersionMapper.toDetailedResponse(scheduleVersion.get());
    }
}
