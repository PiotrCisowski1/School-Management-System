package com.cisowski.schoolmanagement.classroom.mapper;

import com.cisowski.schoolmanagement.classroom.model.Equipment;
import com.cisowski.schoolmanagement.classroom.model.EquipmentRequest;
import com.cisowski.schoolmanagement.classroom.model.EquipmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EquipmentMapper {
    Equipment toEquipmentEntity(EquipmentRequest request);
    EquipmentResponse toEquipmentResponse(Equipment equipment);

    Collection<EquipmentResponse> toEquipmentResponseList(Collection<Equipment> equipments);
}
