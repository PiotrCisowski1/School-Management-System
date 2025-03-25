package com.cisowski.schoolmanagement.classroom.mapper;

import com.cisowski.schoolmanagement.classroom.model.ClassroomDetailedResponse;
import com.cisowski.schoolmanagement.classroom.model.ClassroomEntity;
import com.cisowski.schoolmanagement.classroom.model.ClassroomRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClassroomMapper {

    @Mapping(target = "classroomEquipments", ignore = true)
    ClassroomEntity toClassroomEntity(ClassroomRequest request);

    @Mapping(target = "equipments", source = "classroomEquipments")
    ClassroomDetailedResponse toClassroomDetailedResponse(ClassroomEntity entity);
}
