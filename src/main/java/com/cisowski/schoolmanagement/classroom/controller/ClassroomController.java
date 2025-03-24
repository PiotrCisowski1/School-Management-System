package com.cisowski.schoolmanagement.classroom.controller;

import com.cisowski.schoolmanagement.classroom.model.EquipmentRequest;
import com.cisowski.schoolmanagement.classroom.model.EquipmentResponse;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

    @DeleteMapping("/equipments/{equipmentId}")
    public ResponseEntity deleteEquipment(@PathVariable Integer equipmentId){
        DbLogger.info("Received DELETE Equipment request for ID: " + equipmentId);
        equipmentService.deleteEquipment(equipmentId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/equipments")
    public ResponseEntity<Collection<EquipmentResponse>> findAllEqs(){
        DbLogger.info("Received GET all Equipments request");
        Collection<EquipmentResponse> response = equipmentService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/equipments/{equipmentId}")
    public ResponseEntity<EquipmentResponse> getEqById(@PathVariable Integer equipmentId){
        DbLogger.info("Received GET Equipment request for ID: " + equipmentId);
        EquipmentResponse response = equipmentService.getById(equipmentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
