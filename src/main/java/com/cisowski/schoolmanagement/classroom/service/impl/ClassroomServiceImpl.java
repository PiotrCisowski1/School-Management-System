package com.cisowski.schoolmanagement.classroom.service.impl;

import com.cisowski.schoolmanagement.classroom.mapper.ClassroomMapper;
import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.repository.ClassroomRepository;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @Override
    @Transactional
    public void deleteClassroom(Integer classroomId) {
        DbLogger.info("Removing Classroom with ID: " + classroomId);
        Optional<ClassroomEntity> existingClassroom = classroomRepository.findById(classroomId);
        if(existingClassroom.isEmpty())
            throw new EntityNotFoundException(ClassroomEntity.class, "ID", classroomId.toString());
        //TODO: after schedule - check if classroom is still in use before delete
        classroomRepository.delete(existingClassroom.get());
        DbLogger.info(String.format("Classroom with ID: %s, was successfully removed", classroomId));
    }

    @Override
    public ClassroomDetailedResponse getClassroomById(Integer classroomId) {
        DbLogger.info("Searching for Classroom with ID: " + classroomId);
        Optional<ClassroomEntity> existingClassroom = classroomRepository.findById(classroomId);
        if(existingClassroom.isEmpty())
            throw new EntityNotFoundException(ClassroomEntity.class, "ID", classroomId.toString());
        DbLogger.info(String.format("Found Classroom with ID %s: %s", classroomId, existingClassroom.get().toString()));
        return classroomMapper.toClassroomDetailedResponse(existingClassroom.get());
    }

    @Override
    public Collection<ClassroomSummaryResponse> getAllClassrooms() {
        DbLogger.info("Searching for all Classroom records");
        List<ClassroomEntity> entities = classroomRepository.findAll();
        DbLogger.info(String.format("Found %s Classrooms records", entities.size()));
        return classroomMapper.toSummaryResponseList(entities);
    }
}
