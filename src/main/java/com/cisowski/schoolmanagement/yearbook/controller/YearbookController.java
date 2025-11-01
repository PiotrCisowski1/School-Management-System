package com.cisowski.schoolmanagement.yearbook.controller;

import com.cisowski.schoolmanagement.common.security.authorization.annotation.RequiresPermission;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.yearbook.model.AddYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.PatchYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookDetailedResponse;
import com.cisowski.schoolmanagement.yearbook.model.YearbookSummaryResponse;
import com.cisowski.schoolmanagement.yearbook.service.YearbookService;
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
public class YearbookController {
    private final YearbookService yearbookService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<YearbookDetailedResponse> addYearbook(@Valid @RequestBody AddYearbookRequest request){
        DbLogger.info("Received Yearbook POST request: " + request.toString());
        YearbookDetailedResponse response = yearbookService.addYearbook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{yearbookId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<YearbookDetailedResponse> updateYearbook(@Valid @RequestBody PatchYearbookRequest request, @PathVariable Integer yearbookId){
        DbLogger.info("Received Yearbook PATCH request: " + request.toString());
        YearbookDetailedResponse response = yearbookService.updateYearbook(request, yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{yearbookId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity deleteYearbook(@PathVariable Integer yearbookId){
        DbLogger.info("Received Yearbook DELETE request for YearbookID: " + yearbookId);
        yearbookService.deleteYearbook(yearbookId);
        return new ResponseEntity(HttpStatusCode.valueOf(204));
    }

    @GetMapping("/{yearbookId}")
    @RequiresPermission(resource = ResourceType.YEARBOOK, action = ResourceActionType.READ)
    public ResponseEntity<YearbookDetailedResponse> getYearbook(@PathVariable Integer yearbookId){
        DbLogger.info("Received Yearbook GET request for YearbookID: " + yearbookId);
        YearbookDetailedResponse response = yearbookService.getYearbook(yearbookId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Collection<YearbookSummaryResponse>> getYearbooks(){
        DbLogger.info("Received GET all Yearbook request");
        Collection<YearbookSummaryResponse> response = yearbookService.getYearbooks();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
