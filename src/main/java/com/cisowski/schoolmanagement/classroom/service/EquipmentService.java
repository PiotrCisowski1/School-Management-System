package com.cisowski.schoolmanagement.classroom.service;

import com.cisowski.schoolmanagement.classroom.mapper.EquipmentMapper;
import com.cisowski.schoolmanagement.classroom.model.Equipment;
import com.cisowski.schoolmanagement.classroom.model.EquipmentRequest;
import com.cisowski.schoolmanagement.classroom.model.EquipmentResponse;
import com.cisowski.schoolmanagement.classroom.repository.EquipmentRepository;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional
    public EquipmentResponse addEquipment(EquipmentRequest request){
        DbLogger.info("Add Equipment for request: " + request.toString());
        Equipment requestEntity = equipmentMapper.toEquipmentEntity(request);
        Equipment saved = equipmentRepository.save(requestEntity);
        DbLogger.info("Equipment was saved successfully: " + saved.toString());
        return equipmentMapper.toEquipmentResponse(saved);
    }
}
