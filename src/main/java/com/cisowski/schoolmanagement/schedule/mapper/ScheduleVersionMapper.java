package com.cisowski.schoolmanagement.schedule.mapper;

import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.ScheduleVersionSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ScheduleVersionMapper {

    @Named("toScheduleVersionSummaryResponse")
    ScheduleVersionSummaryResponse toSummaryResponse(ScheduleVersionEntity entity);
}
