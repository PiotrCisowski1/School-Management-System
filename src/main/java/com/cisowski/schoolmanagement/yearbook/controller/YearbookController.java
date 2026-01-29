package com.cisowski.schoolmanagement.yearbook.controller;

import com.cisowski.schoolmanagement.common.annotation.SecurityResponses;
import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.yearbook.model.AddYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.PatchYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookDetailedResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
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
@RequestMapping("/yearbooks")
@RequiredArgsConstructor
@Tag(name = "Yearbooks", description = "Administration of academic years (yearbooks). Defines the time-frame and organizational structure for students.")
public class YearbookController {
    private final YearbookService yearbookService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Create yearbook",
            description = "Adds new yearbook with unique symbol. Required authority level: Administrator")
    @ApiResponse(responseCode = "201", description = "Created successfully")
    @ApiResponse(responseCode = "404", description = "Head teacher not found")
    @ApiResponse(responseCode = "406", description = "Subject not found")
    @ApiResponse(responseCode = "409", description = "Already exists for symbol or head teacher")
    public ResponseEntity<YearbookDetailedResponse> addYearbook(@Valid @RequestBody AddYearbookRequest request){
        DbLogger.info("Received Yearbook POST request: " + request.toString());
        YearbookDetailedResponse response = yearbookService.addYearbook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{yearbookId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Update yearbook",
            description = "Modifies yearbook data and subject association. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Modification successful")
    @ApiResponse(responseCode = "404", description = "Head teacher not found")
    @ApiResponse(responseCode = "404", description = "Subject not found")
    @ApiResponse(responseCode = "406", description = "Symbol is not unique")
    @ApiResponse(responseCode = "406", description = "Head teacher is already a supervisor of another yearbook")
    public ResponseEntity<YearbookDetailedResponse> updateYearbook(@Valid @RequestBody PatchYearbookRequest request, @PathVariable Integer yearbookId){
        DbLogger.info("Received Yearbook PATCH request: " + request.toString());
        YearbookDetailedResponse response = yearbookService.updateYearbook(request, yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{yearbookId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Delete yearbook",
            description = "Remove yearbook from the system if not associated with students. Required authority level: Administrator")
    @ApiResponse(responseCode = "204", description = "Removed successfully")
    @ApiResponse(responseCode = "404", description = "Yearbook not found")
    @ApiResponse(responseCode = "406", description = "Removal canceled due to existing yearbook association with students")
    public ResponseEntity deleteYearbook(@PathVariable Integer yearbookId){
        DbLogger.info("Received Yearbook DELETE request for YearbookID: " + yearbookId);
        yearbookService.deleteYearbook(yearbookId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{yearbookId}")
    @RequiresPermission(resource = ResourceType.YEARBOOK, action = ResourceActionType.READ)
    @SecurityResponses
    @Operation(
            summary = "Find yearbook with ID",
            description = "Retrieves existing yearbook with given ID. Required authority level: Administrator, Teacher, Student")
    @ApiResponse(responseCode = "200", description = "Returns existing yearbook")
    @ApiResponse(responseCode = "404", description = "Not found with ID")
    public ResponseEntity<YearbookDetailedResponse> getYearbook(@PathVariable Integer yearbookId){
        DbLogger.info("Received Yearbook GET request for YearbookID: " + yearbookId);
        YearbookDetailedResponse response = yearbookService.getYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @SecurityResponses
    @Operation(
            summary = "Find all yearbooks",
            description = "Retrieves all existing yearbooks in the system. Required authority level: Administrator")
    @ApiResponse(responseCode = "200", description = "Returns existing yearbooks list (empty result as well)")
    public ResponseEntity<Collection<YearbookSummaryResponse>> getYearbooks(){
        DbLogger.info("Received GET all Yearbook request");
        Collection<YearbookSummaryResponse> response = yearbookService.getYearbooks();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
