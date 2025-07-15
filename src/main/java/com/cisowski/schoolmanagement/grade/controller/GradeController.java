package com.cisowski.schoolmanagement.grade.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.model.gradeScale.*;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.service.GradeScaleService;
import com.cisowski.schoolmanagement.grade.service.GradeTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeTypeService gradeTypeService;
    private final GradeScaleService gradeScaleService;

    @PostMapping("/types")
    public ResponseEntity<GradeTypeResponse> addGradeType(@Valid @RequestBody AddGradeTypeRequest request){
        DbLogger.info("Received POST request for GradeType with request: " + request.toString());
        GradeTypeResponse response = gradeTypeService.addGradeType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/types/{gradeTypeId}")
    public ResponseEntity<GradeTypeResponse> patchGradeType(
            @Valid @RequestBody PatchGradeTypeRequest request,
            @PathVariable BigInteger gradeTypeId){
        DbLogger.info(String.format("Received PATCH request for GradeType ID %s with request: %s", gradeTypeId, request.toString()));
        GradeTypeResponse response = gradeTypeService.patchGradeType(request, gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/types/{gradeTypeId}")
    public ResponseEntity deleteGradeType(@PathVariable BigInteger gradeTypeId){
        DbLogger.info("Received DELETE request for GradeType with ID: " + gradeTypeId);
        gradeTypeService.deleteGradeType(gradeTypeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/types/{gradeTypeId}")
    public ResponseEntity<GradeTypeResponse> getGradeType(@PathVariable BigInteger gradeTypeId){
        DbLogger.info("Received GET request for GradeType with ID: " + gradeTypeId);
        GradeTypeResponse response = gradeTypeService.getGradeType(gradeTypeId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/types")
    public ResponseEntity<Collection<GradeTypeResponse>> getGradeTypes(){
        DbLogger.info("Received GET ALL request for GradeType");
        Collection<GradeTypeResponse> response = gradeTypeService.getGradeTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/scales")
    public ResponseEntity<GradeScaleResponse> addGradeScale(@RequestBody @Valid GradeScaleRequest request){
        DbLogger.info("Received POST request for GradeScale with request: " + request.toString());
        GradeScaleResponse response = gradeScaleService.addGradeScale(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/scales/{gradeScaleId}")
    public ResponseEntity<GradeScaleResponse> patchGradeScale(@RequestBody @Valid PatchGradeScaleRequest request, @PathVariable Long gradeScaleId){
        DbLogger.info(String.format("Received PATCH request for GradeScale (ID: %s) with request: %s", gradeScaleId, request.toString()));
        GradeScaleResponse response = gradeScaleService.patchGradeScale(request, gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/scales/{gradeScaleId}")
    public ResponseEntity deleteGradeScale(@PathVariable Long gradeScaleId){
        DbLogger.info("Received DELETE request for GradeScale with ID: %s" + gradeScaleId);
        gradeScaleService.deleteGradeScale(gradeScaleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/scales/{gradeScaleId}/values/{gradeValueId}")
    public ResponseEntity deleteGradeValue(@PathVariable Long gradeScaleId, @PathVariable Long gradeValueId){
        DbLogger.info(String.format("Received DELETE request for GradeValue with ID %s in GradeScale with ID %s", gradeValueId, gradeScaleId));
        gradeScaleService.deleteGradeValue(gradeScaleId, gradeValueId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/scales/{gradeScaleId}/values")
    public ResponseEntity<GradeValueResponse> addGradeValue(@PathVariable Long gradeScaleId, @RequestBody @Valid GradeValueDto request){
        DbLogger.info(String.format("Received POST request for GradeValue: %s,in GradeScale with ID %s", request.toString(), gradeScaleId));
        GradeValueResponse response = gradeScaleService.addGradeValue(request, gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scales/{gradeScaleId}")
    public ResponseEntity<GradeScaleResponse> getGradeScaleById(@PathVariable Long gradeScaleId){
        DbLogger.info("Received GET request for GradeScale with ID %s" + gradeScaleId);
        GradeScaleResponse response = gradeScaleService.getGradeScaleById(gradeScaleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scales")
    public ResponseEntity<List<GradeScaleSummaryResponse>> getAllGradeScales(){
        DbLogger.info("Received GET all request for GradeScale");
        List<GradeScaleSummaryResponse> response = gradeScaleService.getAllGradeScales();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/scales/active")
    public ResponseEntity<GradeScaleResponse> getActiveGradeScale(){
        DbLogger.info("Received GET request for active GradeScale");
        GradeScaleResponse response = gradeScaleService.getActiveGradeScale();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
