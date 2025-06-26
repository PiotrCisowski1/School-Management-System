package com.cisowski.schoolmanagement.schedule.mapper;

import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ScheduleVersionMapper {

    @Autowired
    @Lazy
    private YearbookMapper yearbookMapper;
    @Autowired
    @Lazy
    private ScheduleMapper scheduleMapper;

    @Named("toScheduleVersionSummaryResponse")
    public abstract ScheduleVersionSummaryResponse toSummaryResponse(ScheduleVersionEntity entity);

    @Mapping(target = "yearbook", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    public abstract ScheduleVersionDetailedResponse toDetailedResponse(ScheduleVersionEntity entity);

    @AfterMapping
    protected void mapYearbook(ScheduleVersionEntity entity, @MappingTarget ScheduleVersionDetailedResponse response){
        if(entity != null && entity.getYearbook() != null){
            response.setYearbook(yearbookMapper.toYearbookSummaryResponse(entity.getYearbook()));
        }
    }

    @AfterMapping
    protected void mapSchedules(ScheduleVersionEntity entity, @MappingTarget ScheduleVersionDetailedResponse response){
        if(entity != null && !CollectionUtils.isEmpty(entity.getSchedules())){
            response.setSchedules(scheduleMapper.toDetailedResponseList(entity.getSchedules()));
        }
    }


}
