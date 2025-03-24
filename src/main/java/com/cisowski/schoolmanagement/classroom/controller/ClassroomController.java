package com.cisowski.schoolmanagement.classroom.controller;

import com.cisowski.schoolmanagement.classroom.model.EquipmentRequest;
import com.cisowski.schoolmanagement.classroom.model.EquipmentResponse;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classrooms")
public class ClassroomController {

    private final EquipmentService equipmentService;

    @PostMapping("/equipments")
    public ResponseEntity<EquipmentResponse> addEquipment(@Valid @RequestBody EquipmentRequest request){
        DbLogger.info("Received POST Equipment request for: " + request.toString());
        EquipmentResponse response = equipmentService.addEquipment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
