package com.cisowski.schoolmanagement.subject.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.model.*;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.subject.service.SubjectTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
@Tag(name = "Subjects", description = "Management of the school curriculum, including subject definitions, department assignments, and subject-specific configurations.")
public class SubjectController {
    private final SubjectService subjectService;
    private final SubjectTypeService subjectTypeService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping
    @SecurityResponses
    @Operation(
            summary = "Create subject",
            description = "Adds new subject with unique subject code. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "409", description = "Object with given subject code already exists")
    public ResponseEntity<SubjectDetailedResponse> addSubject(@Valid @RequestBody AddSubjectRequest request){
        DbLogger.info("Received POST Subject request: " + request.toString());
        SubjectDetailedResponse response = subjectService.addSubject(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/{subjectId}")
    @SecurityResponses
    @Operation(
            summary = "Update subject",
            description = "Modify existing subject. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modified successfully")
    @ApiResponse(responseCode = "404", description = "Subject not found with given code")
    public ResponseEntity<SubjectDetailedResponse> patchSubject(@Valid @RequestBody PatchSubjectRequest request, @PathVariable Integer subjectId){
        DbLogger.info(String.format("Received PATCH Subject request for ID %s: %s ", subjectId, request.toString()));
        SubjectDetailedResponse response = subjectService.patchSubject(request, subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping
    @SecurityResponses
    @Operation(
            summary = "Find all subjects",
            description = "Retrieves all existing subjects in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns object list (empty result as well)")
    public ResponseEntity<Collection<SubjectSummaryResponse>> getAllSubjects(){
        DbLogger.info("Received GET all Subjects request");
        Collection<SubjectSummaryResponse> response = subjectService.getAllSubjects();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.SUBJECT, action = ResourceActionType.READ)
    @GetMapping("/{subjectId}")
    @SecurityResponses
    @Operation(
            summary = "Find subject with ID",
            description = "Retrieves existing subject with given ID. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Object not found with given ID")
    public ResponseEntity<SubjectDetailedResponse> getSubjectById(@PathVariable Integer subjectId){
        DbLogger.info("Received GET Subject request for ID: " + subjectId);
        SubjectDetailedResponse response = subjectService.getSubjectById(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.SUBJECT, action = ResourceActionType.READ)
    @GetMapping("/code/{subjectCode}")
    @SecurityResponses
    @Operation(
            summary = "Find subject with code",
            description = "Retrieves existing subject with given code. Required authority level: Administrator, Teacher, Student, Parent")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Object not found with given code")
    public ResponseEntity<SubjectDetailedResponse> getSubjectByCode(@PathVariable String subjectCode){
        DbLogger.info("Received GET Subject request for code: " + subjectCode);
        SubjectDetailedResponse response = subjectService.getSubjectByCode(subjectCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/type/{subjectType}")
    @SecurityResponses
    @Operation(
            summary = "Find all subjects with type",
            description = "Retrieves all existing subjects in the system with given subject's type. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing object list (empty result as well)")
    public ResponseEntity<Collection<SubjectSummaryResponse>> getSubjectsByType(@PathVariable String subjectType){
        DbLogger.info("Received GET Subjects request for type: " + subjectType);
        Collection<SubjectSummaryResponse> response = subjectService.getSubjectsByType(subjectType);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/{subjectId}")
    @SecurityResponses
    @Operation(
            summary = "Delete subject",
            description = "Remove existing subject from the system if not in use. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Object not found with given ID")
    @ApiResponse(responseCode = "406", description = "Removal canceled due to Subject relation with Yearbook")
    @ApiResponse(responseCode = "406", description = "Removal canceled due to Subject relation with Teacher")
    public ResponseEntity deleteSubject(@PathVariable Integer subjectId){
        DbLogger.info("Received DELETE Subject request for ID: " + subjectId);
        subjectService.deleteSubject(subjectId);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/subjectTypes")
    @SecurityResponses
    @Operation(
            summary = "Create subject type",
            description = "Adds new subject type with unique name. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "409", description = "Object with given name already exists")
    public ResponseEntity<SubjectTypeResponse> addSubjectType(@Valid @RequestBody SubjectTypeRequest request){
        DbLogger.info("Received POST SubjectType request: " + request.toString());
        SubjectTypeResponse response = subjectTypeService.addSubjectType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/subjectTypes/{subjectTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Update subject type",
            description = "Modify existing subject type with unique name. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modified successfully")
    @ApiResponse(responseCode = "404", description = "Object not found with given ID")
    public ResponseEntity<SubjectTypeResponse> patchSubjectType(@Valid @RequestBody SubjectTypeRequest request, @PathVariable Integer subjectTypeId){
        DbLogger.info("Received PATCH SubjectType request: " + request.toString());
        SubjectTypeResponse response = subjectTypeService.patchSubjectType(request, subjectTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/subjectTypes/{subjectTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Find subject type with ID",
            description = "Retrieves existing subject type with given ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Object not found with given ID")
    public ResponseEntity<SubjectTypeResponse> getSubjectType(@PathVariable Integer subjectTypeId){
        DbLogger.info("Received GET SubjectType with ID: " + subjectTypeId.toString());
        SubjectTypeResponse response = subjectTypeService.getSubjectType(subjectTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/subjectTypes")
    @SecurityResponses
    @Operation(
            summary = "Find all subject types",
            description = "Retrieves all existing subject types in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing objects (empty result as well)")
    public ResponseEntity<Collection<SubjectTypeResponse>> getAllSubjectType(){
        DbLogger.info("Received GET all SubjectTypes");
        Collection<SubjectTypeResponse> response = subjectTypeService.getSubjectTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/subjectTypes/{subjectTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Delete subject type from the system if not in use",
            description = "Remove subject type from the system if not in use. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found with given ID")
    @ApiResponse(responseCode = "409", description = "Object with given name already exists")
    public ResponseEntity<SubjectTypeResponse> deleteSubjectType(@PathVariable Integer subjectTypeId){
        DbLogger.info("Received DELETE SubjectType with ID: " + subjectTypeId.toString());
        subjectTypeService.deleteSubjectType(subjectTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
