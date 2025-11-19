package com.cisowski.schoolmanagement.schedule.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.schedule.model.scheduleChangelog.ScheduleChangeLogEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleChangelog.ScheduleChangelogDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = BaseMapperConfig.class)
public interface ScheduleChangelogMapper {

    @Mapping(target = "changedByUser", ignore = true)
    ScheduleChangeLogEntity toEntity(ScheduleChangelogDto dto);
}
