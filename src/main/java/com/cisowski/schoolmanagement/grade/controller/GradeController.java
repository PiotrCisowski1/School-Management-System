package com.cisowski.schoolmanagement.grade.controller;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.model.grade.*;
import com.cisowski.schoolmanagement.grade.model.gradeScale.*;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.service.GradeScaleService;
import com.cisowski.schoolmanagement.grade.service.GradeService;
import com.cisowski.schoolmanagement.grade.service.GradeTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeTypeService gradeTypeService;
    private final GradeScaleService gradeScaleService;
    private final GradeService gradeService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/types")
    public ResponseEntity<GradeTypeResponse> addGradeType(@Valid @RequestBody AddGradeTypeRequest request){
        DbLogger.info("Received POST request for GradeType with request: " + request.toString());
        GradeTypeResponse response = gradeTypeService.addGradeType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/types/{gradeTypeId}")
    public ResponseEntity<GradeTypeResponse> patchGradeType(
            @Valid @RequestBody PatchGradeTypeRequest request,
            @PathVariable Long gradeTypeId){
        DbLogger.info(String.format("Received PATCH request for GradeType ID %s with request: %s", gradeTypeId, request.toString()));
        GradeTypeResponse response = gradeTypeService.patchGradeType(request, gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/types/{gradeTypeId}")
    public ResponseEntity deleteGradeType(@PathVariable Long gradeTypeId){
        DbLogger.info("Received DELETE request for GradeType with ID: " + gradeTypeId);
        gradeTypeService.deleteGradeType(gradeTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/types/{gradeTypeId}")
    public ResponseEntity<GradeTypeResponse> getGradeType(@PathVariable Long gradeTypeId){
        DbLogger.info("Received GET request for GradeType with ID: " + gradeTypeId);
        GradeTypeResponse response = gradeTypeService.getGradeType(gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/types")
    public ResponseEntity<Collection<GradeTypeResponse>> getGradeTypes(){
        DbLogger.info("Received GET ALL request for GradeType");
        Collection<GradeTypeResponse> response = gradeTypeService.getGradeTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/scales")
    public ResponseEntity<GradeScaleResponse> addGradeScale(@RequestBody @Valid GradeScaleRequest request){
        DbLogger.info("Received POST request for GradeScale with request: " + request.toString());
        GradeScaleResponse response = gradeScaleService.addGradeScale(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/scales/{gradeScaleId}")
    public ResponseEntity<GradeScaleResponse> patchGradeScale(@RequestBody @Valid PatchGradeScaleRequest request, @PathVariable Long gradeScaleId){
        DbLogger.info(String.format("Received PATCH request for GradeScale (ID: %s) with request: %s", gradeScaleId, request.toString()));
        GradeScaleResponse response = gradeScaleService.patchGradeScale(request, gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/scales/{gradeScaleId}")
    public ResponseEntity deleteGradeScale(@PathVariable Long gradeScaleId){
        DbLogger.info("Received DELETE request for GradeScale with ID: %s" + gradeScaleId);
        gradeScaleService.deleteGradeScale(gradeScaleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/scales/{gradeScaleId}/values/{gradeValueId}")
    public ResponseEntity deleteGradeValue(@PathVariable Long gradeScaleId, @PathVariable Long gradeValueId){
        DbLogger.info(String.format("Received DELETE request for GradeValue with ID %s in GradeScale with ID %s", gradeValueId, gradeScaleId));
        gradeScaleService.deleteGradeValue(gradeScaleId, gradeValueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/scales/{gradeScaleId}/values")
    public ResponseEntity<GradeValueResponse> addGradeValue(@PathVariable Long gradeScaleId, @RequestBody @Valid GradeValueDto request){
        DbLogger.info(String.format("Received POST request for GradeValue: %s,in GradeScale with ID %s", request.toString(), gradeScaleId));
        GradeValueResponse response = gradeScaleService.addGradeValue(request, gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/scales/{gradeScaleId}")
    public ResponseEntity<GradeScaleResponse> getGradeScaleById(@PathVariable Long gradeScaleId){
        DbLogger.info("Received GET request for GradeScale with ID %s" + gradeScaleId);
        GradeScaleResponse response = gradeScaleService.getGradeScaleById(gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/scales")
    public ResponseEntity<List<GradeScaleSummaryResponse>> getAllGradeScales(){
        DbLogger.info("Received GET all request for GradeScale");
        List<GradeScaleSummaryResponse> response = gradeScaleService.getAllGradeScales();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/scales/active")
    public ResponseEntity<GradeScaleResponse> getActiveGradeScale(){
        DbLogger.info("Received GET request for active GradeScale");
        GradeScaleResponse response = gradeScaleService.getActiveGradeScale();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.CREATE)
    @PostMapping()
    public ResponseEntity<GradeDetailedResponse> addGrade(@Valid @RequestBody AddGradeRequest addGradeRequest){
        DbLogger.info("Received POST request for Grade with request: " + addGradeRequest.toString());
        GradeDetailedResponse response = gradeService.addGrade(addGradeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.UPDATE)
    @PatchMapping("/{gradeId}")
    public ResponseEntity<GradeDetailedResponse> patchGrade(@Valid @RequestBody PatchGradeRequest patchGradeRequest, @PathVariable Long gradeId){
        DbLogger.info(String.format("Received PATCH request for Grade with ID: %s, with request: %s", gradeId, patchGradeRequest.toString()));
        GradeDetailedResponse response = gradeService.patchGrade(patchGradeRequest, gradeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.DELETE)
    @DeleteMapping("/{gradeId}")
    public ResponseEntity deleteGrade(@PathVariable Long gradeId){
        DbLogger.info("Received DELETE request for Grade with ID: " + gradeId);
        gradeService.deleteGrade(gradeId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/{gradeId}")
    public ResponseEntity<GradeDetailedResponse> getGradeById(@PathVariable Long gradeId){
        DbLogger.info("Received GET request for Grade with ID: " + gradeId);
        GradeDetailedResponse response = gradeService.getGradeById(gradeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeByStudentId(@PathVariable Integer studentId){
        DbLogger.info("Received GET request for Grades for Student with ID: " + studentId);
        List<GradeSummaryResponse> response = gradeService.getGradesByStudentId(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/student/{studentId}/subject/{subjectId}")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeByStudentIdAndSubjectId(@PathVariable Integer studentId, @PathVariable Integer subjectId){
        DbLogger.info(String.format("Received GET request for Grades for Student with ID: %s and Subject with ID: %s", studentId, subjectId));
        List<GradeSummaryResponse> response = gradeService.getGradesByStudentIdAndSubject(studentId, subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeBySubjectId(@PathVariable Integer subjectId){
        DbLogger.info("Received GET request for Grades for Subject with ID: " + subjectId);
        List<GradeSummaryResponse> response = gradeService.getGradesBySubjectId(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/type/{gradeTypeId}")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeByGradeType(@PathVariable Long gradeTypeId){
        DbLogger.info("Received GET request for Grades for GradeType with ID: " + gradeTypeId);
        List<GradeSummaryResponse> response = gradeService.getGradesByGradeType(gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
