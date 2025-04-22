package com.cisowski.schoolmanagement.users.teacher.mapper;

import com.cisowski.schoolmanagement.common.mapper.DateMapper;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TeacherMapper.class, DateMapper.class})
public interface TeacherAvailabilityMapper {

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "dayOfWeek", source = "dayOfWeek", qualifiedByName = "toDayOfWeek")
    TeacherAvailabilityEntity toEntity(TeacherAvailabilityRequest request);

    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "toTeacherSummaryResponse")
    TeacherAvailabilityResponse toResponse(TeacherAvailabilityEntity entity);

    List<TeacherAvailabilityResponse> toResponseList(List<TeacherAvailabilityEntity> entities);

}
