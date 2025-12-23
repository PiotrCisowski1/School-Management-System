package com.cisowski.schoolmanagement.timetable.schedule.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.timetable.schedule.mapper.ScheduleChangelogMapper;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangeLogEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangelogDto;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleChangelogRepository;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.users.common.model.UserDetailsEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ScheduleChangelogService {

    private final ScheduleChangelogRepository changelogRepository;
    private final ScheduleChangelogMapper changelogMapper;
    private final ScheduleRepository scheduleRepository;

    public void logChange(@Valid ScheduleChangelogDto changelogDto) {
        DbLogger.info("Creating changelog for Schedule: " + changelogDto.toString());

        if(!scheduleRepository.existsById(changelogDto.getSchedule().getId()))
            throw new EntityNotFoundException(ScheduleEntity.class, "ID", changelogDto.getSchedule().getId().toString());
        UserEntity userChangingData = fetchUserDetails();
        ScheduleChangeLogEntity dtoChangelogEntity = changelogMapper.toEntity(changelogDto);
        dtoChangelogEntity.setChangedByUser(userChangingData);
        changelogRepository.save(dtoChangelogEntity);
        DbLogger.info(String.format("Successfully logged change for Schedule with ID %s", dtoChangelogEntity.getSchedule().getId()));
    }

    private UserEntity fetchUserDetails() {
        UserDetailsEntity authenticatedUserDetails = (UserDetailsEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(authenticatedUserDetails == null || authenticatedUserDetails.getUser() == null) {
            DbLogger.error("Cannot find data about currently authenticated User to create log about Schedule");
            throw new SpecificationBrokenException("Cannot find data about currently authenticated User");
        }
        return authenticatedUserDetails.getUser();
    }
}
