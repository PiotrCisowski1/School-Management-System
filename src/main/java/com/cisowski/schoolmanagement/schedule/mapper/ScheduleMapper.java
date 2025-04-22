package com.cisowski.schoolmanagement.schedule.mapper;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import com.cisowski.schoolmanagement.schedule.model.AddScheduleRequest;
import com.cisowski.schoolmanagement.schedule.model.ScheduleDetailedResponse;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.subject.mapper.SubjectMapper;
import com.cisowski.schoolmanagement.users.teacher.mapper.TeacherMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {SubjectMapper.class, TeacherMapper.class, ClassroomMapper.class, ScheduleVersionMapper.class,
                DateMapper.class})
public interface ScheduleMapper {

    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "scheduleVersion", ignore = true)
    @Mapping(target = "dayOfWeek",source = "dayOfWeek", qualifiedByName = "toDayOfWeek")
    ScheduleEntity toEntity(AddScheduleRequest request);

    @Mapping(target = "subject", source = "subject", qualifiedByName = "toSubjectSummaryResponse")
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "toTeacherSummaryResponse")
    @Mapping(target = "classroom", source = "classroom", qualifiedByName = "toClassroomSummaryResponse")
    @Mapping(target = "scheduleVersion", source = "scheduleVersion", qualifiedByName = "toScheduleVersionSummaryResponse")
    ScheduleDetailedResponse toDetailedResponse(ScheduleEntity saved);
}
