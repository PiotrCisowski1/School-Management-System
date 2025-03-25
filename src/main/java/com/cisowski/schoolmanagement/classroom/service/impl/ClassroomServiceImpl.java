package com.cisowski.schoolmanagement.classroom.service.impl;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.repository.ClassroomRepository;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;
    private final EquipmentService equipmentService;
    @Override
    @Transactional
    public ClassroomDetailedResponse addClassroom(ClassroomRequest request) {
        DbLogger.info("Adding Classroom for request: " + request.toString());
        ClassroomEntity requestEntity = classroomMapper.toClassroomEntity(request);
        setClassroomEqs(requestEntity, request.getEquipments());
        ClassroomEntity saved = classroomRepository.save(requestEntity);
        DbLogger.info("Successfully saved Classroom: " + saved.toString());
        return classroomMapper.toClassroomDetailedResponse(saved);
    }

    private void setClassroomEqs(ClassroomEntity classroom, Collection<EquipmentQuantity> equipments){
        if(CollectionUtils.isEmpty(equipments) || classroom == null)
            return;
        equipments.forEach(eq -> {
            Equipment equipment = equipmentService.fetchEquipment(eq.getEquipmentId());
            classroom.addEquipment(equipment, eq.getQuantity());
        });
    }
}
