package com.cisowski.schoolmanagement.classroom.mapper;

import com.cisowski.schoolmanagement.classroom.model.Equipment;
import com.cisowski.schoolmanagement.classroom.model.EquipmentRequest;
import com.cisowski.schoolmanagement.classroom.model.EquipmentResponse;
import com.cisowski.schoolmanagement.common.mapper.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;

@Mapper(config = BaseMapperConfig.class)
public interface EquipmentMapper {
    Equipment toEquipmentEntity(EquipmentRequest request);
    EquipmentResponse toEquipmentResponse(Equipment equipment);

    Collection<EquipmentResponse> toEquipmentResponseList(Collection<Equipment> equipments);
}
