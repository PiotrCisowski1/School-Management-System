package com.cisowski.schoolmanagement.classroom.service;

import com.cisowski.schoolmanagement.classroom.mapper.EquipmentMapper;
import com.cisowski.schoolmanagement.classroom.model.Equipment;
import com.cisowski.schoolmanagement.classroom.model.EquipmentRequest;
import com.cisowski.schoolmanagement.classroom.model.EquipmentResponse;
import com.cisowski.schoolmanagement.classroom.repository.EquipmentRepository;
import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional
    public EquipmentResponse addEquipment(EquipmentRequest request){
        DbLogger.info("Adding Equipment for request: " + request.toString());
        Equipment requestEntity = equipmentMapper.toEquipmentEntity(request);
        Equipment saved = equipmentRepository.save(requestEntity);
        DbLogger.info("Equipment was saved successfully: " + saved.toString());
        return equipmentMapper.toEquipmentResponse(saved);
    }

    @Transactional
    public void deleteEquipment(Integer equipmentId) {
        DbLogger.info("Deleting equipment for ID: " + equipmentId);
        Optional<Equipment> existingEquipment = equipmentRepository.findById(equipmentId);
        if(existingEquipment.isEmpty())
            throw new EntityNotFoundException(Equipment.class, "ID", equipmentId.toString());
        equipmentRepository.delete(existingEquipment.get());
        DbLogger.info(String.format("Equipment with ID %s was removed successfully", equipmentId));
    }

    public Collection<EquipmentResponse> getAll() {
        DbLogger.info("Searching for all Equipment objects");
        Collection<Equipment> equipments = equipmentRepository.findAll();
        DbLogger.info(String.format("Found %s Equipment objects", equipments.size()));
        return equipmentMapper.toEquipmentResponseList(equipments);
    }

    public EquipmentResponse getById(Integer equipmentId) {
        DbLogger.info("Searching for Equipment with ID: " + equipmentId);
        Optional<Equipment> equipment = equipmentRepository.findById(equipmentId);
        if(equipment.isEmpty())
            throw new EntityNotFoundException(Equipment.class, "ID", equipmentId.toString());
        DbLogger.info(String.format("Found Equipment with ID %s, object: %s", equipmentId, equipment.get().toString()));
        return equipmentMapper.toEquipmentResponse(equipment.get());
    }

    public Equipment fetchEquipment(Integer equipmentId){
        DbLogger.info("Fetching Equipment for ID: " + equipmentId);
        Optional<Equipment> equipment = equipmentRepository.findById(equipmentId);
        if(equipment.isEmpty())
            throw new EntityNotFoundException(Equipment.class, "ID", equipmentId.toString());
        return equipment.get();
    }
}
