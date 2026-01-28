package com.cisowski.schoolmanagement.classroom.controller;

import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.service.ClassroomService;
import com.cisowski.schoolmanagement.classroom.service.EquipmentService;
import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/classrooms")
@Tag(name = "Classroom", description = "Management of Classroom")
public class ClassroomController {

    private final EquipmentService equipmentService;
    private final ClassroomService classroomService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/equipments")
    @SecurityResponses
    @Operation(
            summary = "Create equipment",
            description = "Adds a new equipment object, used in classrooms. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Equipment created successfully")
    public ResponseEntity<EquipmentResponse> addEquipment(@Valid @RequestBody EquipmentRequest request){
        DbLogger.info("Received POST Equipment request for: " + request.toString());
        EquipmentResponse response = equipmentService.addEquipment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/equipments/{equipmentId}")
    @SecurityResponses
    @Operation(
            summary = "Delete equipment",
            description = "Removes equipment object used across classrooms, found by ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Equipment successfully removed")
    @ApiResponse(responseCode = "404", description = "Equipment not found by ID")
    public ResponseEntity deleteEquipment(@PathVariable Integer equipmentId){
        DbLogger.info("Received DELETE Equipment request for ID: " + equipmentId);
        equipmentService.deleteEquipment(equipmentId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/equipments")
    @SecurityResponses
    @Operation(
            summary = "Find all equipments",
            description = "Retrieves all equipment objects in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns all existing equipment (empty result as well)")
    public ResponseEntity<Collection<EquipmentResponse>> findAllEqs(){
        DbLogger.info("Received GET all Equipments request");
        Collection<EquipmentResponse> response = equipmentService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/equipments/{equipmentId}")
    @SecurityResponses
    @Operation(
            summary = "Find equipment by ID",
            description = "Retrieves equipment by ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns equipment found by ID")
    @ApiResponse(responseCode = "404", description = "Equipment not found by ID")
    public ResponseEntity<EquipmentResponse> getEqById(@PathVariable Integer equipmentId){
        DbLogger.info("Received GET Equipment request for ID: " + equipmentId);
        EquipmentResponse response = equipmentService.getById(equipmentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping
    @SecurityResponses
    @Operation(
            summary = "Create classroom",
            description = "Adds new classroom to the system and returns created object. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Classroom created successfully")
    public ResponseEntity<ClassroomDetailedResponse> addClassroom(@RequestBody @Valid ClassroomRequest request){
        DbLogger.info("Received Classroom POST request: " + request.toString());
        ClassroomDetailedResponse response = classroomService.addClassroom(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/{classroomId}")
    @SecurityResponses
    @Operation(
            summary = "Delete classroom",
            description = "Removes classroom from the system if not in use. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Classroom removed successfully")
    @ApiResponse(responseCode = "404", description = "Classroom not found with given ID")
    @ApiResponse(responseCode = "406", description = "Cannot remove classroom because it is used by existing Schedule")
    public ResponseEntity deleteClassroom(@PathVariable Integer classroomId){
        DbLogger.info("Received Classroom DELETE request for ID: " + classroomId);
        classroomService.deleteClassroom(classroomId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequiresPermission(resource = ResourceType.CLASSROOM, action = ResourceActionType.READ)
    @GetMapping("/{classroomId}")
    @SecurityResponses
    @Operation(
            summary = "Find classroom by ID",
            description = "Retrieves existing classroom by given ID. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns found classroom")
    @ApiResponse(responseCode = "404", description = "Classroom not found by ID")
    public ResponseEntity<ClassroomDetailedResponse> getClassroom(@PathVariable Integer classroomId){
        DbLogger.info("Received Classroom GET request for ID: " + classroomId);
        ClassroomDetailedResponse response = classroomService.getClassroomById(classroomId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping()
    @SecurityResponses
    @Operation(
            summary = "Get all classrooms",
            description = "Retrieves all existing classrooms. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns list of existing classrooms (empty result as well)")
    public ResponseEntity<Collection<ClassroomSummaryResponse>> getClassrooms(){
        DbLogger.info("Received Classroom GET all request");
        Collection<ClassroomSummaryResponse> response = classroomService.getAllClassrooms();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/{classroomId}")
    @SecurityResponses
    @Operation(
            summary = "Update classroom",
            description = "Modify existing classroom, found by given ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Classroom updated successfully")
    @ApiResponse(responseCode = "404", description = "Classroom not found by given ID")
    public ResponseEntity<ClassroomDetailedResponse> patchClassroom(@Valid @RequestBody PatchClassroomRequest request, @PathVariable Integer classroomId){
        DbLogger.info("Received Classroom PATCH request: " + request.toString());
        ClassroomDetailedResponse response = classroomService.updateClassroom(request, classroomId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
