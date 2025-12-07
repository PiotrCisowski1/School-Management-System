package com.cisowski.schoolmanagement.timetable.schedule.mapper;

import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.PatchScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionDetailedResponse;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.mapper.YearbookMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
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

    public abstract List<ScheduleVersionSummaryResponse> toSummaryResponseList(List<ScheduleVersionEntity> entities);

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

    public abstract ScheduleVersionEntity toEntity(PatchScheduleVersionRequest request);

    public abstract void patchEntity(ScheduleVersionEntity patchingEntity, @MappingTarget ScheduleVersionEntity entityToPatch);

}
