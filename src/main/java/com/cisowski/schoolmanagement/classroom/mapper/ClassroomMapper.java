package com.cisowski.schoolmanagement.classroom.mapper;

import com.cisowski.schoolmanagement.classroom.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClassroomMapper {

    @Mapping(target = "classroomEquipments", ignore = true)
    ClassroomEntity toClassroomEntity(ClassroomRequest request);

    @Mapping(target = "equipments", source = "classroomEquipments")
    ClassroomDetailedResponse toClassroomDetailedResponse(ClassroomEntity entity);

    @Mapping(target = "equipmentCount", source = "classroomEquipments", qualifiedByName = "countEquipments")
    ClassroomSummaryResponse toSummaryResponse(ClassroomEntity entity);

    List<ClassroomSummaryResponse> toSummaryResponseList(List<ClassroomEntity> entities);

    @Named("countEquipments")
    default Integer countEquipments(Collection<ClassroomEquipment> equipments){
        if(equipments == null)
            return 0;
        return equipments.size();
    }
}
