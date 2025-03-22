package com.cisowski.schoolmanagement.subject.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.subject.model.*;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.subject.service.SubjectTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;
    private final SubjectTypeService subjectTypeService;

    @PostMapping
    public ResponseEntity<SubjectDetailedResponse> addSubject(@Valid @RequestBody AddSubjectRequest request){
        DbLogger.info("Received POST Subject request: " + request.toString());
        SubjectDetailedResponse response = subjectService.addSubject(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{subjectId}")
    public ResponseEntity<SubjectDetailedResponse> patchSubject(@Valid @RequestBody PatchSubjectRequest request, @PathVariable Integer subjectId){
        DbLogger.info(String.format("Received PATCH Subject request for ID %s: %s ", subjectId, request.toString()));
        SubjectDetailedResponse response = subjectService.patchSubject(request, subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<SubjectSummaryResponse>> getAllSubjects(){
        DbLogger.info("Received GET all Subjects request");
        Collection<SubjectSummaryResponse> response = subjectService.getAllSubjects();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectDetailedResponse> getSubjectById(@PathVariable Integer subjectId){
        DbLogger.info("Received GET Subject request for ID: " + subjectId);
        SubjectDetailedResponse response = subjectService.getSubjectById(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/code/{subjectCode}")
    public ResponseEntity<SubjectDetailedResponse> getSubjectByCode(@PathVariable String subjectCode){
        DbLogger.info("Received GET Subject request for code: " + subjectCode);
        SubjectDetailedResponse response = subjectService.getSubjectByCode(subjectCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/type/{subjectType}")
    public ResponseEntity<Collection<SubjectSummaryResponse>> getSubjectsByType(@PathVariable String subjectType){
        DbLogger.info("Received GET Subjects request for type: " + subjectType);
        Collection<SubjectSummaryResponse> response = subjectService.getSubjectsByType(subjectType);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity deleteSubject(@PathVariable Integer subjectId){
        DbLogger.info("Received DELETE Subject request for ID: " + subjectId);
        subjectService.deleteSubject(subjectId);
        return new ResponseEntity<>(HttpStatusCode.valueOf(204));
    }

    @PostMapping("/subjectTypes")
    public ResponseEntity<SubjectTypeResponse> addSubjectType(@Valid @RequestBody SubjectTypeRequest request){
        DbLogger.info("Received POST SubjectType request: " + request.toString());
        SubjectTypeResponse response = subjectTypeService.addSubjectType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/subjectTypes/{subjectTypeId}")
    public ResponseEntity<SubjectTypeResponse> patchSubjectType(@Valid @RequestBody SubjectTypeRequest request, @PathVariable Integer subjectTypeId){
        DbLogger.info("Received PATCH SubjectType request: " + request.toString());
        SubjectTypeResponse response = subjectTypeService.patchSubjectType(request, subjectTypeId);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/subjectTypes/{subjectTypeId}")
    public ResponseEntity<SubjectTypeResponse> getSubjectType(@PathVariable Integer subjectTypeId){
        DbLogger.info("Received GET SubjectType with ID: " + subjectTypeId.toString());
        SubjectTypeResponse response = subjectTypeService.getSubjectType(subjectTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/subjectTypes")
    public ResponseEntity<Collection<SubjectTypeResponse>> getAllSubjectType(){
        DbLogger.info("Received GET all SubjectTypes");
        Collection<SubjectTypeResponse> response = subjectTypeService.getSubjectTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/subjectTypes/{subjectTypeId}")
    public ResponseEntity<SubjectTypeResponse> deleteSubjectType(@PathVariable Integer subjectTypeId){
        DbLogger.info("Received DELETE SubjectType with ID: " + subjectTypeId.toString());
        subjectTypeService.deleteSubjectType(subjectTypeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
