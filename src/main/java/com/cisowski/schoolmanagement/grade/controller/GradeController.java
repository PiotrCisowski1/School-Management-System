package com.cisowski.schoolmanagement.grade.controller;

import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.model.gradeType.AddGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeResponse;
import com.cisowski.schoolmanagement.grade.model.gradeType.PatchGradeTypeRequest;
import com.cisowski.schoolmanagement.grade.service.GradeTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Collection;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeTypeService gradeTypeService;

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
}
