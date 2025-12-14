package com.cisowski.schoolmanagement.timetable.scheduleOccurrence.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.timetable.schedule.mapper.ScheduleMapper;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceEntity;
import com.cisowski.schoolmanagement.timetable.scheduleOccurrence.model.ScheduleOccurrenceSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = BaseMapperConfig.class, uses = {ScheduleMapper.class})
public interface ScheduleOccurrenceMapper {

    @Named("toScheduleOccurrenceSummaryResponse")
    @Mapping(target = "schedule", source = "schedule", qualifiedByName = "toScheduleSummaryResponse")
    ScheduleOccurrenceSummaryResponse toSummaryResponse(ScheduleOccurrenceEntity entity);
}
