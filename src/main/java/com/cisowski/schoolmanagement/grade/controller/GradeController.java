package com.cisowski.schoolmanagement.grade.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
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
import java.util.List;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Management of student academic performance, including grade assignment, grade types, and grading scales.")
public class GradeController {

    private final GradeTypeService gradeTypeService;
    private final GradeScaleService gradeScaleService;
    private final GradeService gradeService;

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/types")
    @SecurityResponses
    @Operation(
            summary = "Create grade type",
            description = "Adds new grade type to the system, describing scope and significance of grade. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Grade type created successfully")
    @ApiResponse(responseCode = "406", description = "Grade type already exists by given grade scope")
    public ResponseEntity<GradeTypeResponse> addGradeType(@Valid @RequestBody AddGradeTypeRequest request){
        DbLogger.info("Received POST request for GradeType with request: " + request.toString());
        GradeTypeResponse response = gradeTypeService.addGradeType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/types/{gradeTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Update grade type",
            description = "Modify existing grade type found by ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Grade type updated successfully")
    @ApiResponse(responseCode = "404", description = "Grade type not found by ID")
    @ApiResponse(responseCode = "406", description = "Grade type already exists by given grade scope")
    public ResponseEntity<GradeTypeResponse> patchGradeType(
            @Valid @RequestBody PatchGradeTypeRequest request,
            @PathVariable Long gradeTypeId){
        DbLogger.info(String.format("Received PATCH request for GradeType ID %s with request: %s", gradeTypeId, request.toString()));
        GradeTypeResponse response = gradeTypeService.patchGradeType(request, gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/types/{gradeTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Delete grade type",
            description = "Remove grade type from the system if not used. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Not found by ID")
    @ApiResponse(responseCode = "406", description = "Grade type in use")
    public ResponseEntity deleteGradeType(@PathVariable Long gradeTypeId){
        DbLogger.info("Received DELETE request for GradeType with ID: " + gradeTypeId);
        gradeTypeService.deleteGradeType(gradeTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/types/{gradeTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Find grade type by ID",
            description = "Retrieve grade type by given ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns found object")
    @ApiResponse(responseCode = "404", description = "Object not found by ID")
    public ResponseEntity<GradeTypeResponse> getGradeType(@PathVariable Long gradeTypeId){
        DbLogger.info("Received GET request for GradeType with ID: " + gradeTypeId);
        GradeTypeResponse response = gradeTypeService.getGradeType(gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/types")
    @SecurityResponses
    @Operation(
            summary = "Get all grade types",
            description = "Retrieves all existing grade types in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns found objects (empty result as well)")
    public ResponseEntity<Collection<GradeTypeResponse>> getGradeTypes(){
        DbLogger.info("Received GET ALL request for GradeType");
        Collection<GradeTypeResponse> response = gradeTypeService.getGradeTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/scales")
    @SecurityResponses
    @Operation(
            summary = "Create new grade scale",
            description = "Adds new grade scale, which represents possible grade values used by all Teachers. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "406", description = "Given grade scale must be set to active, because there is no active scale at the moment")
    public ResponseEntity<GradeScaleResponse> addGradeScale(@RequestBody @Valid GradeScaleRequest request){
        DbLogger.info("Received POST request for GradeScale with request: " + request.toString());
        GradeScaleResponse response = gradeScaleService.addGradeScale(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PatchMapping("/scales/{gradeScaleId}")
    @SecurityResponses
    @Operation(
            summary = "Update grade scale",
            description = "Modify existing grade scale, found by ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modification successful")
    @ApiResponse(responseCode = "404", description = "Object not found by ID")
    @ApiResponse(responseCode = "406", description = "Given grade scale must be set to active, because there is no active scale at the moment")
    public ResponseEntity<GradeScaleResponse> patchGradeScale(@RequestBody @Valid PatchGradeScaleRequest request, @PathVariable Long gradeScaleId){
        DbLogger.info(String.format("Received PATCH request for GradeScale (ID: %s) with request: %s", gradeScaleId, request.toString()));
        GradeScaleResponse response = gradeScaleService.patchGradeScale(request, gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/scales/{gradeScaleId}")
    @SecurityResponses
    @Operation(
            summary = "Delete grade scale",
            description = "Remove grade scale from the system, possible if not the only one in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Object not found by ID")
    @ApiResponse(responseCode = "406", description = "Cannot remove because it is only one active in the system")
    public ResponseEntity deleteGradeScale(@PathVariable Long gradeScaleId){
        DbLogger.info("Received DELETE request for GradeScale with ID: %s" + gradeScaleId);
        gradeScaleService.deleteGradeScale(gradeScaleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @DeleteMapping("/scales/{gradeScaleId}/values/{gradeValueId}")
    @SecurityResponses
    @Operation(
            summary = "Delete grade value",
            description = "Remove grade value from grade scale. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Grade scale not found by ID")
    @ApiResponse(responseCode = "404", description = "Grade value not found by ID")
    public ResponseEntity deleteGradeValue(@PathVariable Long gradeScaleId, @PathVariable Long gradeValueId){
        DbLogger.info(String.format("Received DELETE request for GradeValue with ID %s in GradeScale with ID %s", gradeValueId, gradeScaleId));
        gradeScaleService.deleteGradeValue(gradeScaleId, gradeValueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @PostMapping("/scales/{gradeScaleId}/values")
    @SecurityResponses
    @Operation(
            summary = "Create grade value",
            description = "Adds new grade value to grade scale. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Grade scale not found by ID")
    public ResponseEntity<GradeValueResponse> addGradeValue(@PathVariable Long gradeScaleId, @RequestBody @Valid GradeValueDto request){
        DbLogger.info(String.format("Received POST request for GradeValue: %s,in GradeScale with ID %s", request.toString(), gradeScaleId));
        GradeValueResponse response = gradeScaleService.addGradeValue(request, gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/scales/{gradeScaleId}")
    @SecurityResponses
    @Operation(
            summary = "Find grade scale by ID",
            description = "Retrieves existing grade scale found by ID. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing object")
    @ApiResponse(responseCode = "404", description = "Object not found by ID")
    public ResponseEntity<GradeScaleResponse> getGradeScaleById(@PathVariable Long gradeScaleId){
        DbLogger.info("Received GET request for GradeScale with ID %s" + gradeScaleId);
        GradeScaleResponse response = gradeScaleService.getGradeScaleById(gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/scales")
    @SecurityResponses
    @Operation(
            summary = "Get all grade scales",
            description = "Retrieves list of existing grade scales. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns exiting grade scales (empty result as well)")
    public ResponseEntity<List<GradeScaleSummaryResponse>> getAllGradeScales(){
        DbLogger.info("Received GET all request for GradeScale");
        List<GradeScaleSummaryResponse> response = gradeScaleService.getAllGradeScales();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @GetMapping("/scales/active")
    @SecurityResponses
    @Operation(
            summary = "Find active grade scale",
            description = "Retrieves actual active grade scale at the moment. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns found grade scale")
    @ApiResponse(responseCode = "406", description = "No active grade scale found")
    public ResponseEntity<GradeScaleResponse> getActiveGradeScale(){
        DbLogger.info("Received GET request for active GradeScale");
        GradeScaleResponse response = gradeScaleService.getActiveGradeScale();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.CREATE)
    @PostMapping()
    @SecurityResponses
    @Operation(
            summary = "Create grade",
            description = "Adds new grade for Student by Teacher, related with Subject, with particular value and type. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Student not found by ID")
    @ApiResponse(responseCode = "404", description = "Teacher not found by ID")
    @ApiResponse(responseCode = "404", description = "Subject not found by ID")
    @ApiResponse(responseCode = "404", description = "Grade type not found by ID")
    @ApiResponse(responseCode = "404", description = "Grade value not found by ID")
    @ApiResponse(responseCode = "406", description = "Teacher not associated with Subject")
    @ApiResponse(responseCode = "406", description = "Student not attending Subject")
    public ResponseEntity<GradeDetailedResponse> addGrade(@Valid @RequestBody AddGradeRequest addGradeRequest){
        DbLogger.info("Received POST request for Grade with request: " + addGradeRequest.toString());
        GradeDetailedResponse response = gradeService.addGrade(addGradeRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.UPDATE)
    @PatchMapping("/{gradeId}")
    @SecurityResponses
    @Operation(
            summary = "Update grade",
            description = "Modify grade by Administrator or Teacher issuing grade. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Modified successfully")
    @ApiResponse(responseCode = "404", description = "Grade not found by ID")
    @ApiResponse(responseCode = "406", description = "Teacher not associated with Subject")
    public ResponseEntity<GradeDetailedResponse> patchGrade(@Valid @RequestBody PatchGradeRequest patchGradeRequest, @PathVariable Long gradeId){
        DbLogger.info(String.format("Received PATCH request for Grade with ID: %s, with request: %s", gradeId, patchGradeRequest.toString()));
        GradeDetailedResponse response = gradeService.patchGrade(patchGradeRequest, gradeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.DELETE)
    @DeleteMapping("/{gradeId}")
    @SecurityResponses
    @Operation(
            summary = "Delete grade",
            description = "Remove grade by Administrator or Teacher issuing grade. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Grade not found by ID")
    public ResponseEntity deleteGrade(@PathVariable Long gradeId){
        DbLogger.info("Received DELETE request for Grade with ID: " + gradeId);
        gradeService.deleteGrade(gradeId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/{gradeId}")
    @SecurityResponses
    @Operation(
            summary = "Find grade by ID",
            description = "Find specific grade by ID, if Teacher issuing grade, Student owning grade or Student's head Teacher. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns found grade")
    @ApiResponse(responseCode = "404", description = "Not found by ID")
    public ResponseEntity<GradeDetailedResponse> getGradeById(@PathVariable Long gradeId){
        DbLogger.info("Received GET request for Grade with ID: " + gradeId);
        GradeDetailedResponse response = gradeService.getGradeById(gradeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/student/{studentId}")
    @SecurityResponses
    @Operation(
            summary = "Find grades by Student ID",
            description = "Find all grades for Student by Administrator, Student owning grade or Student's head Teacher. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns list of grades (empty result as well)")
    @ApiResponse(responseCode = "404", description = "Student not found by ID")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeByStudentId(@PathVariable Integer studentId){
        DbLogger.info("Received GET request for Grades for Student with ID: " + studentId);
        List<GradeSummaryResponse> response = gradeService.getGradesByStudentId(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @SecurityResponses
    @Operation(
            summary = "Find grades by Student and Subject",
            description = "Find all grades for Student, related to specific Subject, by Administrator, Student owning grade, head Teacher or Teacher associated with Subject. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns list of grades (empty result as well)")
    @ApiResponse(responseCode = "404", description = "Student not found by ID")
    @ApiResponse(responseCode = "404", description = "Subject not found by ID")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeByStudentIdAndSubjectId(@PathVariable Integer studentId, @PathVariable Integer subjectId){
        DbLogger.info(String.format("Received GET request for Grades for Student with ID: %s and Subject with ID: %s", studentId, subjectId));
        List<GradeSummaryResponse> response = gradeService.getGradesByStudentIdAndSubject(studentId, subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/subject/{subjectId}")
    @SecurityResponses
    @Operation(
            summary = "Get grades for subject",
            description = "Find all grades related with specific subject, by Administrator or Teacher associated with subject. Required authority level: Administrator, Teacher")
    @ApiResponse(responseCode = "200", description = "Returns list of grades (empty result as well)")
    @ApiResponse(responseCode = "404", description = "Subject not found by ID")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeBySubjectId(@PathVariable Integer subjectId){
        DbLogger.info("Received GET request for Grades for Subject with ID: " + subjectId);
        List<GradeSummaryResponse> response = gradeService.getGradesBySubjectId(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequiresPermission(resource = ResourceType.GRADE, action = ResourceActionType.READ)
    @GetMapping("/type/{gradeTypeId}")
    @SecurityResponses
    @Operation(
            summary = "Get grades for grade type",
            description = "Find all grades for specific grade type. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns list of grades (empty result as well)")
    @ApiResponse(responseCode = "404", description = "Grade type not found by ID")
    public ResponseEntity<List<GradeSummaryResponse>> getGradeByGradeType(@PathVariable Long gradeTypeId){
        DbLogger.info("Received GET request for Grades for GradeType with ID: " + gradeTypeId);
        List<GradeSummaryResponse> response = gradeService.getGradesByGradeType(gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
