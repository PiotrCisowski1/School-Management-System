package com.cisowski.schoolmanagement.users.teacher.mapper;

import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.DayOfWeek;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {TeacherMapper.class})
public interface TeacherAvailabilityMapper {

    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "dayOfWeek", source = "dayOfWeek", qualifiedByName = "toDayOfWeek")
    TeacherAvailabilityEntity toEntity(TeacherAvailabilityRequest request);

    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "toTeacherSummaryResponse")
    TeacherAvailabilityResponse toResponse(TeacherAvailabilityEntity entity);

    @Named("toDayOfWeek")
    default DayOfWeek map(Integer dayOfWeek){
        if(dayOfWeek == null)
            return null;
        return DayOfWeek.of(dayOfWeek);
    }
}
