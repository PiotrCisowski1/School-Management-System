package com.cisowski.schoolmanagement.schedule.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.schedule.repository.ScheduleVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleVersionServiceImpl implements ScheduleVersionService {

    private final ScheduleVersionRepository repository;
    @Override
    public Collection<ScheduleVersionSummaryResponse> getScheduleVersionsForYearbook(Integer yearbookId) {
        return null;
    }

    @Override
    public ScheduleVersionDetailedResponse createScheduleVersion(Integer yearbookId) {
        return null;
    }

    @Override
    public ScheduleVersionDetailedResponse cloneScheduleVersion(Integer scheduleVersionId) {
        return null;
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
}
