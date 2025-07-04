package com.cisowski.schoolmanagement.classroom.mapper;

import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface ClassroomMapper {

    @Mapping(target = "classroomEquipments", ignore = true)
    ClassroomEntity toClassroomEntity(ClassroomRequest request);

    @Mapping(target = "equipments", source = "classroomEquipments")
    ClassroomDetailedResponse toClassroomDetailedResponse(ClassroomEntity entity);

    @Named("toClassroomSummaryResponse")
    @Mapping(target = "equipmentCount", source = "classroomEquipments", qualifiedByName = "countEquipments")
    ClassroomSummaryResponse toSummaryResponse(ClassroomEntity entity);

    List<ClassroomSummaryResponse> toSummaryResponseList(List<ClassroomEntity> entities);

    ClassroomEntity toClassroomEntity(PatchClassroomRequest request);

    @Mapping(target = "classroomEquipments", ignore = true)
    void patchClassroom(ClassroomEntity requestEntity, @MappingTarget ClassroomEntity existingEntity);

    @Named("countEquipments")
    default Integer countEquipments(Collection<ClassroomEquipment> equipments){
        if(equipments == null)
            return 0;
        return equipments.size();
    }
}
