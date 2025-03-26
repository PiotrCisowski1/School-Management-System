package com.cisowski.schoolmanagement.classroom.controller;

import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
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
    private final ClassroomService classroomService;

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

    @PostMapping
    public ResponseEntity<ClassroomDetailedResponse> addClassroom(@RequestBody @Valid ClassroomRequest request){
        DbLogger.info("Received Classroom POST request: " + request.toString());
        ClassroomDetailedResponse response = classroomService.addClassroom(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{classroomId}")
    public ResponseEntity deleteClassroom(@PathVariable Integer classroomId){
        DbLogger.info("Received Classroom DELETE request for ID: " + classroomId);
        classroomService.deleteClassroom(classroomId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{classroomId}")
    public ResponseEntity<ClassroomDetailedResponse> getClassroom(@PathVariable Integer classroomId){
        DbLogger.info("Received Classroom GET request for ID: " + classroomId);
        ClassroomDetailedResponse response = classroomService.getClassroomById(classroomId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Collection<ClassroomSummaryResponse>> getClassrooms(){
        DbLogger.info("Received Classroom GET all request");
        Collection<ClassroomSummaryResponse> response = classroomService.getAllClassrooms();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
